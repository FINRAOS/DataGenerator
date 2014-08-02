/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.finra.datagenerator.exec;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Logger;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.distributor.SearchProblem;
import org.finra.datagenerator.scxml.DataGeneratorExecutor;
import org.finra.datagenerator.scxml.PossibleState;
import org.finra.datagenerator.utils.ScXmlUtils;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Executes a state chart
 */
public class ChartExec {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ChartExec.class);

    /**
     * A comma separated list of variables to be passed to the OutputFormatter
     */
    private String outputVariables;

    /**
     * The set of initial variables that the user wants to set
     */
    private String initialVariables;

    private static HashSet<String> varsOut;
    /**
     * The initial set of events to trigger before re-searching for a new
     * scenario
     */
    private String initialEvents;

    private static final ArrayList<String> INITIAL_EVENTS_LIST = new ArrayList<>();

    /**
     * Initial variables map
     */
    private static final HashMap<String, String> INITIAL_VARIABLES_MAP = new HashMap<>();

    private long maxRecords = -1;

    private int bootstrapMin;
    private InputStream inputFileStream;

    /**
     * Will be shared and used to signal to all threads to exit
     */
    public ChartExec() {
    }

    /**
     * Sets the smallest number of subproblems to attempt to split this problem
     * to before calling the distributor
     *
     * @param numberOfSubproblems the number of sub problems to split the given
     * problem to
     * @return a reference to the current ChartExec
     */
    public ChartExec setBootstrapMin(int numberOfSubproblems) {
        this.bootstrapMin = numberOfSubproblems;
        return this;
    }

    public String getOutputVariables() {
        return outputVariables;
    }

    /**
     * A comma separated list of output variables names
     *
     * @param outputVariables a comma separated list of output variables names
     * @return a reference to the current ChartExec
     */
    public ChartExec setOutputVariables(String outputVariables) {
        this.outputVariables = outputVariables;
        return this;
    }

    public String getInitialEvents() {
        return initialEvents;
    }

    /**
     * Sets the initial events
     *
     * @param initialEvents a comma separated list of initial events
     * @return a reference to the current ChartExec
     */
    public ChartExec setInitialEvents(String initialEvents) {
        this.initialEvents = initialEvents;
        return this;
    }

    public String getInitialVariables() {
        return initialVariables;
    }

    /**
     * Sets the initial variable using a comma separated assignments
     *
     * @param initialVariables a string containing comma separated assignments
     * @return a reference to the current ChartExec
     */
    public ChartExec setInitialVariables(String initialVariables) {
        this.initialVariables = initialVariables;
        return this;
    }

    /**
     * Sets the name of the input file
     *
     * @param inputFileName a string containing the input file name
     * @return a reference to the current ChartExec
     * @deprecated Use {@link ChartExec#setInputFileStream(java.io.InputStream)}
     * instead
     */
    @Deprecated
    public ChartExec setInputFileName(String inputFileName) {
        try {
            this.inputFileStream = new FileInputStream(new File(inputFileName));
        } catch (FileNotFoundException e) {
            log.error("Error creating InputStream for file " + inputFileName, e);
        }
        return this;
    }

    /**
     * Sets the input file using a stream
     *
     * @param inputFileStream an input stream that will be used to read the file
     * @return a reference to the current ChartExec
     */
    public ChartExec setInputFileStream(InputStream inputFileStream) {
        this.inputFileStream = inputFileStream;
        return this;
    }

    public long getMaxScenarios() {
        return maxRecords;
    }

    /**
     * Sets the maximum number of records to generate
     *
     * @param maxRecords a long containing the maximum number of records to
     * generate
     * @return a reference to the current ChartExec
     */
    public ChartExec setMaxRecords(long maxRecords) {
        this.maxRecords = maxRecords;
        return this;
    }

    /**
     * TODO: This is not accurate. The inputFileStream can be null if the user
     * uses the deprecated function and sets the file name
     */
    private boolean doSanityChecks() throws IOException {

        if (inputFileStream == null) {
            throw new IOException("Error:, input file cannot be null");
        }

        // Parse the initial events
        if (initialEvents != null) {
            Iterable<String> events = Splitter.on(",").split(initialVariables);
            INITIAL_EVENTS_LIST.addAll(Lists.newArrayList(events));
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
                    INITIAL_VARIABLES_MAP.put(assignment[0], assignment[1]);
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

    private List<SearchProblem> prepare(String stateMachineText) throws ModelException, IOException, SCXMLExpressionException, SAXException {
        doSanityChecks();

        DataGeneratorExecutor executor = new DataGeneratorExecutor(stateMachineText);

        varsOut = extractOutputVariables(stateMachineText);
        // Get BFS-generated states for bootstrapping parallel search
        List<PossibleState> bfsStates = executor.searchForScenarios(varsOut, INITIAL_VARIABLES_MAP, INITIAL_EVENTS_LIST,
                maxRecords, bootstrapMin);

        List<SearchProblem> dfsProblems = new ArrayList<>();

        int i = 0;
        for (PossibleState state : bfsStates) {
            dfsProblems.add(new SearchProblem(state, varsOut, INITIAL_VARIABLES_MAP, INITIAL_EVENTS_LIST, bfsStates.size(), i++));
        }

        return dfsProblems;
    }

    /**
     * Executes the problem over the distributor
     *
     * @param distributor the distributor to use
     * @throws java.io.IOException due to errors in IOUtils.toString or prepare
     * @throws org.apache.commons.scxml.model.ModelException due to errors in
     * prepare
     * @throws org.apache.commons.scxml.SCXMLExpressionException due to errors
     * in prepare
     * @throws org.xml.sax.SAXException due to errors in prepare
     */
    public void process(SearchDistributor distributor) throws IOException, ModelException, SCXMLExpressionException, SAXException {
        String machineText = IOUtils.toString(inputFileStream, "UTF-8");
        List<SearchProblem> dfsProblems = prepare(machineText);

        log.info("Found " + dfsProblems.size() + " states to distribute");
        distributor.setStateMachineText(machineText);
//        distributor.setExitFlag(new AtomicBoolean(false));
        distributor.distribute(dfsProblems);
        log.info("DONE.");
    }
}
