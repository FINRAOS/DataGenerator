package org.finra.scxmlexec;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.apache.log4j.Logger;

public class ChartExec {

    private static final Logger log = Logger.getLogger(ChartExec.class);

    /**
     * A comma separated list of variables to be passed to the OutputFormatter
     */
    private String outputVariables;

    /**
     * The input SCXML chart file
     */
    private String inputFileName = null;

    /**
     * The set of initial variables that the user wants to set
     */
    private String initialVariables = null;

    private static HashSet<String> varsOut = null;
    /**
     * The initial set of events to trigger before re-searching for a new
     * scenario
     */
    private String initialEvents = null;

    private static final ArrayList<String> initialEventsList = new ArrayList<String>();

    /**
     * Length of scenario
     */
    private int lengthOfScenario = 5;

    /**
     * Generate -ve scenarios
     */
    private boolean generateNegativeScenarios = false;

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
    private static final HashMap<String, String> initialVariablesMap = new HashMap<String, String>();

    private int maxEventReps = 1;

    private int maxScenarios = 10000;

    private static final StateMachineListener listener = new StateMachineListener();

    public String getOutputVariables() {
        return outputVariables;
    }

    public void setOutputVariables(String outputVariables) {
        this.outputVariables = outputVariables;
    }

    public String getInitialEvents() {
        return initialEvents;
    }

    public void setInitialEvents(String initialEvents) {
        this.initialEvents = initialEvents;
    }

    public String getInitialVariables() {
        return initialVariables;
    }

    public void setInitialVariables(String initialVariables) {
        this.initialVariables = initialVariables;
    }

    private static OutputStream os;

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public boolean isGenerateNegativeScenarios() {
        return generateNegativeScenarios;
    }

    public void setGenerateNegativeScenarios(boolean generateNegativeScenarios) {
        this.generateNegativeScenarios = generateNegativeScenarios;
    }

    public int getLengthOfScenario() {
        return lengthOfScenario;
    }

    public void setLengthOfScenario(int lengthOfScenario) {
        this.lengthOfScenario = lengthOfScenario;
    }

    public int getMaxEventReps() {
        return maxEventReps;
    }

    public void setMaxEventReps(int maxEventReps) {
        this.maxEventReps = maxEventReps;
    }

    public int getMaxScenarios() {
        return maxScenarios;
    }

    public void setMaxScenarios(int maxScenarios) {
        this.maxScenarios = maxScenarios;
    }

    public OutputStream getOutputStream() {
        return ChartExec.os;
    }

    private boolean doSanityChecks() throws IOException {
        if (outputVariables == null) {
            throw new IOException("Cannot continuw with outputVariables=null");
        }

        if (inputFileName == null) {
            throw new IOException("Error:, input file cannot be null");
        }

        if (!(new File(inputFileName)).exists()) {
            throw new IOException("Error:, input file does not exist");
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
    private void resetStateMachine() throws ModelException {
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
    private void fireEvents(String commaSeparatedEvents) throws ModelException {
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

    private void fireEvents(ArrayList<String> events) throws ModelException {
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

    private HashSet<String> extractOutputVariables(String filePathName) throws IOException {
        log.info("Extracting variables from file: " + filePathName);
        List<String> linesRead = FileUtils.readLines(new File(filePathName));
        HashSet<String> outputVars = new HashSet<String>();
        for (String line : linesRead) {
            if (line.contains("var_out")) {
                int startIndex = line.indexOf("var_out");
                int lastIndex = startIndex;
                while (lastIndex < line.length() && (Character.isAlphabetic(line.charAt(lastIndex))
                        || Character.isDigit(line.charAt(lastIndex))
                        || line.charAt(lastIndex) == '_'
                        || line.charAt(lastIndex) == '-')) {
                    lastIndex++;
                }
                if (lastIndex == line.length()) {
                    throw new IOException("Reached the end of the line while parsing variable name in line: '" + line + "'.");
                }
                String varName = line.substring(startIndex, lastIndex);
                log.info("Found variable: " + varName);
                outputVars.add(varName);
            }
        }

        return outputVars;
    }

    public void process() throws Exception {
        doSanityChecks();
        // Load the state machine
        String absolutePath = (new File(inputFileName)).getAbsolutePath();
        log.info("Processing file:" + absolutePath);
        varsOut = extractOutputVariables(absolutePath);
        stateMachine = SCXMLParser.parse(new URL("file://" + absolutePath), null);

        executor = new SCXMLExecutor();
        elEvaluator = new ELEvaluator();
        context = new ELContext();

        executor.setEvaluator(elEvaluator);
        executor.setStateMachine(stateMachine);
        executor.setRootContext(context);
        executor.addListener(stateMachine, listener);

        searchForScenariosDFS();
    }

    private void findEvents(ArrayList<String> positive, ArrayList<String> negative) throws ModelException,
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
                Boolean result = (Boolean) elEvaluator.eval(context, condition);
                if (result == null) {
                    throw new ModelException("Condition: " + condition + " evaluates to null");
                }

                if (result) {
                    positive.add(transitionCode);
                } else {
                    negative.add(transitionCode);
                }
            }
        }
    }

    private void printEvents(String type, ArrayList<String> events) {
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
    static class PossibleState {

        /**
         * The name of the next state
         */
        String nextStateName;
        String transitionEvent;

        boolean varsInspected = false;

        /**
         * The variables that need to be set before jumping to that state
         */
        final HashMap<String, String> variablesAssignment = new HashMap<String, String>();

        @Override
        public String toString() {
            return "[" + nextStateName + "," + transitionEvent + "," + varsInspected + "," + variablesAssignment + "]";
        }
    }

    /**
     * Check all the variables in the context. Generate a state with a list of
     * variables correctly assigned
     *
     * @param possiblePositiveStates
     */
    private ArrayList<PossibleState> findPossibleStates() throws ModelException, SCXMLExpressionException, IOException {
        ArrayList<PossibleState> possiblePositiveStates = new ArrayList<PossibleState>();
        ArrayList<String> positive = new ArrayList<String>();
        ArrayList<String> negative = new ArrayList<String>();
        findEvents(positive, negative);
        for (String state : positive) {
            PossibleState possibleState = new PossibleState();
            String[] parts = state.split("-");
            possibleState.nextStateName = parts[2];
            possibleState.transitionEvent = parts[1];
            possiblePositiveStates.add(possibleState);
        }
        return possiblePositiveStates;
    }

    private static HashMap<String, String> readVarsOut() {
        HashMap<String, String> result = new HashMap<String, String>();
        for (String varName : varsOut) {
            result.put(varName, (String) context.get(varName));
        }
        return result;
    }

    private void traceDepth(ArrayList<ArrayList<PossibleState>> possiblePositiveStatesList) throws ModelException, IOException, SCXMLExpressionException {
        resetStateMachine();
        while (listener.getCurrentState() == null
                || (listener.getCurrentState() != null && !listener.getCurrentState().getId().equals("end"))) {
            log.debug("Resetting state maching");
            resetStateMachine();

            // Replay the initial states
            for (ArrayList<PossibleState> states : possiblePositiveStatesList) {
                PossibleState initialState = states.get(0);
                // Fire the event of the state
                HashMap<String, String> varsVals = null;
                HashMap<String, String> nextVarsVals;
                if (!initialState.varsInspected) {
                    varsVals = readVarsOut();
                }
                try {
                    executor.triggerEvent(new TriggerEvent(initialState.transitionEvent, TriggerEvent.SIGNAL_EVENT));
                } catch (Exception e) {
                    throw new IOException("Exception while triggering transition event " + initialState.transitionEvent, e);
                }
                if (!initialState.varsInspected) {
                    nextVarsVals = readVarsOut();
                    if (varsVals == null || nextVarsVals == null) {
                        throw new IOException("NULL in nextVarsVals or varsVals");
                    }
                    for (Entry<String, String> var : varsVals.entrySet()) {
                        String nextVal = nextVarsVals.get(var.getKey());
                        if (!nextVal.equals(var.getValue())) {
                            if (nextVal.startsWith("set:{")) {
                                // Remove the set:{ and }
                                String[] vals = nextVal.substring(5, nextVal.length() - 1).split(",");

                                // Delete this state from the list
                                states.remove(0);
                                for (String val : vals) {
                                    PossibleState possibleState = new PossibleState();
                                    possibleState.nextStateName = initialState.nextStateName;
                                    possibleState.transitionEvent = initialState.transitionEvent;
                                    possibleState.variablesAssignment.putAll(initialState.variablesAssignment);
                                    possibleState.variablesAssignment.put(var.getKey(), val);
                                    possibleState.varsInspected = true;
                                    states.add(0, possibleState);
                                    log.debug("Adding:" + possibleState);
                                }
                            } else {
                                states.get(0).variablesAssignment.put(var.getKey(), nextVal);
                                states.get(0).varsInspected = true;
                            }
                        }
                    }
                    initialState = states.get(0);
                }

                // Set the variables
                for (Entry<String, String> var : initialState.variablesAssignment.entrySet()) {
                    context.set(var.getKey(), var.getValue());
                }
            }

            ArrayList<PossibleState> nextPositiveStates = findPossibleStates();

            possiblePositiveStatesList.add(nextPositiveStates);
        }

        // Remove end
        possiblePositiveStatesList.remove(possiblePositiveStatesList.size() - 1);
    }

    /**
     * Do a depth first search looking for scenarios
     *
     * @throws ModelException
     * @throws SCXMLExpressionException
     * @throws IOException
     */
    private void searchForScenariosDFS() throws ModelException, SCXMLExpressionException, IOException {
        log.info("Search for scenarios using depth first search");
        ArrayList<ArrayList<PossibleState>> possiblePositiveStatesList = new ArrayList<ArrayList<PossibleState>>();
        ArrayList<String> currentStates = new ArrayList<String>();
        ArrayList<Integer> activePostiveState = new ArrayList<Integer>();

        // First we have to generate the first level in the depth, so that we have something to start
        // the recursion from
        log.debug("Searching for the initial next possible states");
        traceDepth(possiblePositiveStatesList);
        log.debug("Initial depth trace: " + possiblePositiveStatesList);

        // Now we have the initial list with sets decompressed
        produceOutput();
        while (true) {
            // Recursively delete one node from the end
            boolean empty;
            do {
                if (possiblePositiveStatesList.isEmpty()) {
                    empty = true;
                    break;
                }
                empty = false;
                int lastDepth = possiblePositiveStatesList.size() - 1;
                log.debug("Removing: " + possiblePositiveStatesList.get(lastDepth).remove(0).nextStateName);
                if (possiblePositiveStatesList.get(lastDepth).isEmpty()) {
                    log.debug("Empty at level: " + possiblePositiveStatesList.size());
                    possiblePositiveStatesList.remove(possiblePositiveStatesList.size() - 1);
                    empty = true;
                }
            } while (empty);

            if (empty) {
                // We're done
                break;
            }

            log.debug("**After removing, depth trace: " + possiblePositiveStatesList);
            traceDepth(possiblePositiveStatesList);
            log.debug("**After finding next, depth trace: " + possiblePositiveStatesList);

            produceOutput();
        }
    }

    private void searchForScenarios() throws ModelException, SCXMLExpressionException, IOException {
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
    private ArrayList<String> pruneEvents(ArrayList<String> eventList) {
        // Count the number of repetitions of every event
        ArrayList<String> all = new ArrayList<String>();
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
}
