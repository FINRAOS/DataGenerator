package org.finra.datagenerator.exec;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.distributor.SearchProblem;
import org.finra.datagenerator.scxml.DataGeneratorExecutor;
import org.finra.datagenerator.scxml.PossibleState;
import org.finra.datagenerator.utils.ScXmlUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

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
     * The initial set of events to trigger before re-searching for a new scenario
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

    private long maxScenarios = -1;

    private int bootstrapMin = 0;
    private InputStream inputFileStream;

    /**
     * Will be shared and used to signal to all threads to exit
     */
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

    public ChartExec setLengthOfScenario(int lengthOfScenario) {
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

    public long getMaxScenarios() {
        return maxScenarios;
    }

    public ChartExec setMaxScenarios(long maxScenarios) {
        this.maxScenarios = maxScenarios;
        return this;
    }

    private boolean doSanityChecks() throws IOException {

        if (inputFileStream == null) {
            throw new IOException("Error:, input file cannot be null");
        }

        // Parse the initial events
        if (initialEvents != null) {
            Iterable<String> events = Splitter.on(",").split(initialVariables);
            initialEventsList.addAll(Lists.newArrayList(events));
        }

        // Parse the initial variables
        if (initialVariables != null) {
            Iterable<String> vars = Splitter.on(",").split(initialVariables);
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
        Set<String> names = ScXmlUtils.getAttributesValues(stateMachineText, "assign", "name");
        return (HashSet<String>) names;
    }

    public List<SearchProblem> prepare(String stateMachineText) throws Exception {
        doSanityChecks();

        DataGeneratorExecutor executor = new DataGeneratorExecutor(stateMachineText);

        varsOut = extractOutputVariables(stateMachineText);
        // Get BFS-generated states for bootstrapping parallel search
        List<PossibleState> bfsStates = executor.searchForScenarios(varsOut, initialVariablesMap, initialEventsList,
                maxEventReps, maxScenarios, lengthOfScenario, bootstrapMin);

        List<SearchProblem> dfsProblems = new ArrayList<SearchProblem>();

        int i = 0;
        for (PossibleState state : bfsStates) {
            dfsProblems.add(new SearchProblem(state, varsOut, initialVariablesMap, initialEventsList, bfsStates.size(), i++));
        }

        return dfsProblems;
    }

    public void process(SearchDistributor distributor) throws Exception {
        String machineText = IOUtils.toString(inputFileStream, "UTF-8");
        List<SearchProblem> dfsProblems = prepare(machineText);

        log.info("Found " + dfsProblems.size() + " states to distribute");
        distributor.setStateMachineText(machineText);
//        distributor.setExitFlag(new AtomicBoolean(false));
        distributor.distribute(dfsProblems);
        log.info("DONE.");
    }
}
