package org.finra.scxmlexec;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.env.jsp.ELContext;
import org.apache.commons.scxml.env.jsp.ELEvaluator;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ChartExec {

    private static final Logger log = Logger.getLogger(ChartExec.class);

    /**
     * The input SCXML chart file
     */
    private static String inputFileName = null;

    /**
     * The set of initial variables that the user wants to set
     */
    private static String initialVariables = null;

    private static HashSet<String> varsOut = null;
    /**
     * The initial set of events to trigger before re-searching for a new
     * scenario
     */
    private static String initialEvents = null;

    private static final ArrayList<String> initialEventsList = new ArrayList<>();

    /**
     * Length of scenario
     */
    private static int lengthOfScenario = 5;

    /**
     * Generate -ve scenarios
     */
    private static boolean generateNegativeScenarios = false;

    /**
     * The state machine
     */
    private static SCXML stateMachine = null;

    /**
     * The state machine executor
     */
    private static SCXMLExecutor executor;

    /**
     * The state machine evaluator
     */
    private static ELEvaluator elEvaluator;

    /**
     * The state machine executor
     */
    private static Context context;

    /**
     * Initial variables map
     */
    private static final HashMap<String, String> initialVariablesMap = new HashMap<>();

    private static int maxEventReps = 1;

    private static int maxScenarios = 10000;

    private static final StateMachineListener listener = new StateMachineListener();

    /**
     * Prints the help on the command line
     *
     * @param options
     */
    public static void printHelp(Options options) {
        Collection<Option> c = options.getOptions();
        System.out.println("Command line options are:");
        for (Option op : c) {
            StringBuilder helpLine = new StringBuilder();
            helpLine
                    .append("\t-")
                    .append(op.getOpt())
                    .append(" --")
                    .append(op.getLongOpt())
                    .append("                 ".substring(op.getLongOpt().length()));

            int length = helpLine.length();

            System.out.print("\t-" + op.getOpt() + " --" + op.getLongOpt());
            System.out.print("                 ".substring(op.getLongOpt().length()));
            System.out.println(op.getDescription());
        }
    }

    public static void parseCommandLine(String args[]) throws ParseException {
        // create the command line parser
        CommandLineParser parser = new GnuParser();

        // create the Options
        final Options options = new Options()
                .addOption("h", "help", false, "print help.")
                .addOption("i", "inputfile", true, "the scxml input file")
                .addOption("v", "initialvariables", true,
                        "comma separated list of the initial variables and their values in the form of var1=val1,var2=val2")
                .addOption("e", "initalevents", true,
                        "a comma separated list of the initial set of events to trigger before searching for scenarios")
                .addOption("l", "lengthofscenario", true,
                        "the number of events to trigger searching for scenarios, default is 5")
                .addOption("V", "generatenegative", false,
                        "generate all negative transitions in addition to the positive ones")
                .addOption("r", "eventreps", true,
                        "the number of times a specific event is allowed to repeat in a scenario. The default is 1")
                .addOption("s", "maxscenarios", true,
                        "Maximum number of scenarios to generate. Default 10,000");

        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h") || cmd.getOptions().length == 0) {
            printHelp(options);
        }

        if (cmd.hasOption("i")) {
            inputFileName = cmd.getOptionValue('i');
        }

        if (cmd.hasOption("v")) {
            initialVariables = cmd.getOptionValue('v');
        }

        if (cmd.hasOption("e")) {
            initialEvents = cmd.getOptionValue('e');
        }

        if (cmd.hasOption('l')) {
            String stringValue = cmd.getOptionValue('l');
            if (StringUtils.isNotEmpty(stringValue)) {
                lengthOfScenario = Integer.valueOf(stringValue);
            } else {
                log.error("Unparsable numeric value for option 'l':" + stringValue);
            }
        }

        if (cmd.hasOption('r')) {
            String stringValue = cmd.getOptionValue('r');
            if (StringUtils.isNotEmpty(stringValue)) {
                maxEventReps = Integer.valueOf(stringValue);
            } else {
                log.error("Unparsable numeric value for option 'r':" + stringValue);
            }
        }

        if (cmd.hasOption('s')) {
            String stringValue = cmd.getOptionValue('s');
            if (StringUtils.isNotEmpty(stringValue)) {
                maxScenarios = Integer.valueOf(stringValue);
            } else {
                log.error("Unparsable numeric value for option 's':" + stringValue);
            }
        }

        if (cmd.hasOption("V")) {
            generateNegativeScenarios = true;
        }

    }

    private static boolean doSanityChecks() throws IOException {
        if (inputFileName == null) {
            log.error("Error:, input file cannot be null");
            return false;
        }

        if (!(new File(inputFileName)).exists()) {
            log.error("Error:, input file does not exist");
            return false;
        }

        // Parse the initial events
        if (initialEvents != null) {
            initialEventsList.addAll(Arrays.asList(StringUtils.split(initialEvents, ",")));
        }

        // Parse the initial variables
        if (initialVariables != null) {
            String[] vars = StringUtils.split(initialVariables, ",");
            for (String var : vars) {
                if (var.contains("=")) {
                    String[] assignment = var.split("=");
                    if (assignment[0] == null || assignment[1] == null || assignment[0].length() == 0
                            || assignment[1].length() == 0) {
                        throw new IOException("Error while processing initial variable assignment for: " + var);
                    }
                    initialVariablesMap.put(assignment[0], assignment[1]);
                } else {
                    throw new IOException("Error while processing initial variable assignment for: " + var);
                }
            }
        }

        return true;
    }

    /**
     * Reset the state machine, set the initial variables, and trigger the
     * initial events
     *
     * @throws ModelException
     */
    private static void resetStateMachine() throws ModelException {
        // Go to the initial state
        executor.reset();

        // Set the initial variables values
        for (String var : varsOut) {
            context.set(var, "");
        }

        for (Map.Entry<String, String> entry : initialVariablesMap.entrySet()) {
            context.set(entry.getKey(), entry.getValue());
        }

        listener.reset();
        executor.go();
        fireEvents(initialEvents);
    }

    /**
     * Executes a list of given events. The format is
     * [beforeState]-event-[afterState] with the before and after states and
     * their separators optional
     *
     * @param commaSeparatedEvents
     * @throws ModelException
     */
    private static void fireEvents(String commaSeparatedEvents) throws ModelException {
        if (commaSeparatedEvents == null) {
            return;
        }
        commaSeparatedEvents = commaSeparatedEvents.trim();
        if (commaSeparatedEvents == null || commaSeparatedEvents.length() == 0) {
            return;
        }

        String[] events = commaSeparatedEvents.split(",");
        for (String event : events) {
            log.debug("Firing event: " + event);
            String eventName = event;
            if (eventName.contains("-")) {
                String[] parts = event.split("-");
                eventName = parts[1];
            }
            log.debug("EventName:" + eventName);
            executor.triggerEvent(new TriggerEvent(eventName, TriggerEvent.SIGNAL_EVENT));
        }
    }

    private static void fireEvents(ArrayList<String> events) throws ModelException {
        if (events == null) {
            return;
        }

        for (String event : events) {
            log.debug("Firing event: " + event);
            String eventName = event;
            if (eventName.contains("-")) {
                String[] parts = event.split("-");
                eventName = parts[1];
            }
            log.debug("EventName:" + eventName);
            executor.triggerEvent(new TriggerEvent(eventName, TriggerEvent.SIGNAL_EVENT));
        }
    }

    private static HashSet<String> extractOutputVariables(String filePathName) throws IOException {
        List<String> lines = FileUtils.readLines(new File(filePathName));
        HashSet<String> outputVars = new HashSet<>();
        for (String line : lines) {
            if (line.contains("var_out")) {
                int startIndex = line.indexOf("var_out");
                int lastIndex = startIndex;
                while (lastIndex < line.length() && (Character.isAlphabetic(line.charAt(lastIndex))
                        || line.charAt(lastIndex) == '_'
                        || line.charAt(lastIndex) == '-')) {
                    lastIndex++;
                }
                if (lastIndex == line.length()) {
                    throw new IOException("Reached the end of the line while parsing variable name in line: '" + line + "'.");
                }
                outputVars.add(line.substring(startIndex, lastIndex));
            }
        }

        return outputVars;
    }

    private static void process() throws Exception {
        // Load the state machine
        String absolutePath = (new File(inputFileName)).getAbsolutePath();
        varsOut = extractOutputVariables(absolutePath);
        stateMachine = SCXMLParser.parse(new URL("file://" + absolutePath), null);

        executor = new SCXMLExecutor();
        elEvaluator = new ELEvaluator();
        context = new ELContext();

        executor.setEvaluator(elEvaluator);
        executor.setStateMachine(stateMachine);
        executor.setRootContext(context);
        executor.addListener(stateMachine, listener);

        searchForScenarios();
    }

    private static void findEvents(ArrayList<String> positive, ArrayList<String> negative) throws ModelException,
            SCXMLExpressionException, IOException {
        positive.clear();
        negative.clear();
        TransitionTarget currentState = listener.getCurrentState();
        if (currentState == null) {
            throw new ModelException("Reached a null state");
        }
        List<Transition> transitions = currentState.getTransitionsList();
        for (Transition transition : transitions) {
            String condition = transition.getCond();
            List<TransitionTarget> targets = transition.getTargets();

            // In our case we should only have one target always
            if (targets == null) {
                throw new IOException("Found null targets for transition: " + transition.getEvent() + " in state: "
                        + currentState.getId());
            }

            if (targets.size() > 1 || targets.isEmpty()) {
                throw new IOException("Found incorrect number of targets:" + targets.size() + "for transition: "
                        + transition.getEvent() + " in state: " + currentState.getId());
            }

            String nextStateId = targets.get(0).getId();
            String transitionCode = currentState.getId() + "-" + transition.getEvent() + "-" + nextStateId;
            if (condition == null) {
                positive.add(transitionCode);
            } else {
                Object result = elEvaluator.eval(context, condition);
                if (result == null) {
                    throw new ModelException("Condition: " + condition + " evaluates to null");
                }
                String boolRes = result.toString().toLowerCase();
                switch (boolRes) {
                    case "true":
                        positive.add(transitionCode);
                        break;
                    case "false":
                        negative.add(transitionCode);
                        break;
                    default:
                        throw new ModelException("Got result: " + boolRes + " while evaluating condition: " + condition
                                + " for event " + transition.getEvent());
                }
            }
        }
    }

    private static void printEvents(String type, ArrayList<String> events) {
        StringBuilder b = new StringBuilder();
        b.append(type);

        if (initialEvents != null) {
            b
                    .append(initialEvents)
                    .append(",");
        }

        boolean firstEvent = true;
        for (String event : events) {
            if (firstEvent) {
                firstEvent = false;
            } else {
                b.append(",");
            }
            b.append(event);
        }

        log.info(b);
    }

    /**
     * Defines a possible state that a state can be in. A possible state is a
     * combination of a state and values for variables.
     */
    class PossibleState {

        /**
         * The name of the next state
         */
        String nextStateName;

        /**
         * The variables that need to be set before jumping to that state
         */
        final HashMap<String, String> variablesAssignmane = new HashMap<>();
    }

    /**
     * Check all the variables in the context. Generate a state with a list of
     * variables correctly assigned
     *
     * @param possiblePositiveStates
     */
    private static void findPossibleStates(ArrayList<PossibleState> possiblePositiveStates) {

    }

    /**
     * Do a depth first search looking for scenarios
     *
     * @throws ModelException
     * @throws SCXMLExpressionException
     * @throws IOException
     */
    private static void searchForScenariosDFS() throws ModelException, SCXMLExpressionException, IOException {
        ArrayList<ArrayList<PossibleState>> possiblePositiveStatesList = new ArrayList<>();
        ArrayList<Integer> activePostiveState = new ArrayList<>();

        // First we have to generate the first level in the depth, so that we have something to start
        // the recursion from
        resetStateMachine();

        ArrayList<PossibleState> possiblePositiveStates = new ArrayList<>();
        findPossibleStates(possiblePositiveStates);
    }

    private static void searchForScenarios() throws ModelException, SCXMLExpressionException, IOException {
        ArrayDeque<ArrayList<String>> stack = new ArrayDeque<>();
        int numberOfScenariosGenerated = 0;

        ArrayList<String> positiveEvents = new ArrayList<>();
        ArrayList<String> negativeEvents = new ArrayList<>();

        resetStateMachine();
        findEvents(positiveEvents, negativeEvents);

        // Add every positive event by itself, since it will be executed after the initial ones
        for (String event : positiveEvents) {
            ArrayList<String> singleEventList = new ArrayList<>();
            singleEventList.add(event);
            singleEventList = pruneEvents(singleEventList);

            if (singleEventList != null) {
                stack.push(singleEventList);
                printEvents("+ve:", singleEventList);
                numberOfScenariosGenerated++;
            }
        }

        // Check the -ve events
        for (String event : negativeEvents) {
            ArrayList<String> singleEventList = new ArrayList<>();
            singleEventList.add(event);
            singleEventList = pruneEvents(singleEventList);

            if (singleEventList != null) {
                printEvents("-ve:", singleEventList);
                numberOfScenariosGenerated++;
            }
        }

        while (stack.size() > 0 && numberOfScenariosGenerated < maxScenarios) {
            ArrayList<String> scenario = stack.pop();

            log.debug("Searching for more scenarios using: " + scenario);

            resetStateMachine();
            fireEvents(scenario);
            findEvents(positiveEvents, negativeEvents);

            // Add every positive event by itself, since it will be executed after the initial ones
            for (String event : positiveEvents) {
                ArrayList<String> eventList = new ArrayList<>();
                log.debug("Scenario:" + scenario + " new event:" + event);
                eventList.addAll(scenario);
                eventList.add(event);
                eventList = pruneEvents(eventList);

                if (eventList != null) {
                    stack.push(eventList);
                    printEvents("+ve:", eventList);
                    numberOfScenariosGenerated++;
                }
            }

            // Check the -ve events
            for (String event : negativeEvents) {
                ArrayList<String> eventList = new ArrayList<>();
                eventList.addAll(scenario);
                eventList.add(event);
                eventList = pruneEvents(eventList);

                if (eventList != null) {
                    printEvents("-ve:", eventList);
                    numberOfScenariosGenerated++;
                }
            }
        }
    }

    /**
     * Delete the scenario if an event is repeated more than maxEventReps times
     *
     * @param eventList
     * @return
     */
    private static ArrayList<String> pruneEvents(ArrayList<String> eventList) {
        // Count the number of repetitions of every event
        ArrayList<String> all = new ArrayList<>();
        all.addAll(initialEventsList);
        all.addAll(eventList);

        if (all.size() > lengthOfScenario) {
            return null;
        }

        HashMap<String, Integer> count = new HashMap<>();
        for (String event : all) {
            Integer counter = count.get(event);
            if (counter == null) {
                counter = 0;
            }
            counter++;
            count.put(event, counter);
            if (counter > maxEventReps) {
                return null;
            }
        }
        return eventList;
    }

    public static void main(String args[]) throws Exception {
        Logger.getLogger("org.apache").setLevel(Level.WARN);
        LogInitializer.initialize();
        parseCommandLine(args);

        if (doSanityChecks()) {
            process();
        }
    }
}
