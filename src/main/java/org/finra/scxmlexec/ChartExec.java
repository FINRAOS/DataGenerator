package org.finra.scxmlexec;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class ChartExec {

    protected static final Logger log = Logger.getLogger(ChartExec.class);

    private static boolean isDebugEnabled = false;

    /**
     * A comma separated list of variables to be passed to the OutputFormatter
     */
    private String outputVariables;

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
     * Initial variables map
     */
    private static final HashMap<String, String> initialVariablesMap = new HashMap<String, String>();

    private int maxEventReps = 1;

    private int maxScenarios = 10000;

    private int bootstrapMin = 0;
    private InputStream inputFileStream;

    public ChartExec() {
        isDebugEnabled = false;
    }

    public ChartExec setBootstrapMin(int depth) {
        this.bootstrapMin = depth;
        return this;
    }

    public String getOutputVariables() {
        return outputVariables;
    }

    public ChartExec setOutputVariables(String outputVariables) {
        this.outputVariables = outputVariables;
        return this;
    }

    public String getInitialEvents() {
        return initialEvents;
    }

    public ChartExec setInitialEvents(String initialEvents) {
        this.initialEvents = initialEvents;
        return this;
    }

    public String getInitialVariables() {
        return initialVariables;
    }

    public ChartExec setInitialVariables(String initialVariables) {
        this.initialVariables = initialVariables;
        return this;
    }

    @Deprecated
    public ChartExec setInputFileName(String inputFileName) {
        new File(inputFileName);
        try {
            this.inputFileStream = new FileInputStream(new File(inputFileName));
        } catch (FileNotFoundException e) {
            log.error("Error creating InputStream for file " + inputFileName, e);
        }
        return this;
    }

    public ChartExec setInputFileStream(InputStream inputFileStream) {
        this.inputFileStream = inputFileStream;
        return this;
    }

    public boolean isGenerateNegativeScenarios() {
        return generateNegativeScenarios;
    }

    public ChartExec setGenerateNegativeScenarios(boolean generateNegativeScenarios) {
        this.generateNegativeScenarios = generateNegativeScenarios;
        return this;
    }

    public int getLengthOfScenario() {
        return lengthOfScenario;
    }

    public ChartExec setLengthOfScenario(int lengthOfScenario){
        this.lengthOfScenario = lengthOfScenario;
        return this;
    }

    public int getMaxEventReps() {
        return maxEventReps;
    }

    public ChartExec setMaxEventReps(int maxEventReps) {
        this.maxEventReps = maxEventReps;
        return this;
    }

    public int getMaxScenarios() {
        return maxScenarios;
    }

    public ChartExec setMaxScenarios(int maxScenarios) {
        this.maxScenarios = maxScenarios;
        return this;
    }

    private boolean doSanityChecks() throws IOException {

        if (inputFileStream == null) {
            throw new IOException("Error:, input file cannot be null");
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

    private HashSet<String> extractOutputVariables(String stateMachineText) throws IOException {
        InputStream is = new ByteArrayInputStream(stateMachineText.getBytes());
        BufferedReader bReader = new BufferedReader(new InputStreamReader(is));

        HashSet<String> outputVars = new HashSet<String>();
        String line = bReader.readLine();
        while (line != null) {
            if (line.contains("var_out")) {
                int startIndex = line.indexOf("var_out");
                int lastIndex = startIndex;
                while (lastIndex < line.length() && (Character.isLetter(line.charAt(lastIndex))
                        || Character.isDigit(line.charAt(lastIndex))
                        || line.charAt(lastIndex) == '_'
                        || line.charAt(lastIndex) == '-')) {
                    lastIndex++;
                }
                if (lastIndex == line.length()) {
                    throw new IOException("Reached the end of the line while parsing variable name in line: '" + line
                            + "'.");
                }
                String varName = line.substring(startIndex, lastIndex);
                log.info("Found variable: " + varName);
                outputVars.add(varName);
            }
            line = bReader.readLine();
        }

        return outputVars;
    }

    public List<SearchProblem> prepare(String stateMachineText) throws Exception {
        doSanityChecks();

        DataGeneratorExecutor executor = new DataGeneratorExecutor(stateMachineText);

        varsOut = extractOutputVariables(stateMachineText);
        // Get BFS-generated states for bootstrapping parallel search
        List<PossibleState> bfsStates = executor.searchForScenarios(varsOut, initialVariablesMap, initialEventsList,
                maxEventReps, maxScenarios, lengthOfScenario, bootstrapMin);

        List<SearchProblem> dfsProblems = new ArrayList<SearchProblem>();

        for (PossibleState state : bfsStates) {
            dfsProblems.add(new SearchProblem(state, varsOut, initialVariablesMap, initialEventsList));
        }

        return dfsProblems;
    }

    public void process(SearchDistributor distributor) throws Exception {
        String machineText = IOUtils.toString(inputFileStream, "UTF-8");
        List<SearchProblem> dfsProblems = prepare(machineText);

        log.info("Found " + dfsProblems.size() + " states to distribute");
        distributor.setStateMachineText(machineText);
        distributor.distribute(dfsProblems);
        log.info("DONE.");
    }
}

