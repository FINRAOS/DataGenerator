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
package org.finra.datagenerator.distributor.multithreaded;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Logger;
import org.finra.datagenerator.distributor.SearchProblem;
import org.finra.datagenerator.scxml.DataGeneratorExecutor;
import org.finra.datagenerator.scxml.PossibleState;
import org.xml.sax.SAXException;

/**
 * Created by robbinbr on 3/14/14.
 */
public class SearchWorker implements Runnable {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(SearchWorker.class);

    private final PossibleState initialState;
    private final Queue queue;
    private final DataGeneratorExecutor executor;
    private final Set<String> varsOut;
    private final Map<String, String> initialVariablesMap;
    private final List<String> initialEventsList;
    private final Map<String, AtomicBoolean> flags;

    /**
     * Public constructor
     *
     * @param problem the problem to search
     * @param stateMachineText the xml text of the state machine
     * @param queue a queue that will receive the results
     * @param flags shared flags
     * @throws ModelException IOException due to errors instantiating the
     * {@link DataGeneratorExecutor}
     * @throws IOException due to errors instantiating the
     * {@link DataGeneratorExecutor}
     * @throws SAXException due to errors instantiating the
     * {@link DataGeneratorExecutor}
     */
    public SearchWorker(final SearchProblem problem, final String stateMachineText,
            final Queue queue, final Map<String, AtomicBoolean> flags) throws ModelException,
            IOException, SAXException {
        this.queue = queue;
        this.executor = new DataGeneratorExecutor(stateMachineText);
        this.initialState = problem.getInitialState();
        this.varsOut = problem.getVarsOut();
        this.initialVariablesMap = problem.getInitialVariablesMap();
        this.initialEventsList = problem.getInitialEventsList();
        this.flags = flags;
    }

    @Override
    public void run() {
        try {
            log.info(Thread.currentThread().getName() + " is starting DFS");
            executor.searchForScenariosDFS(initialState, queue, varsOut, initialVariablesMap, initialEventsList, flags);
            log.info(Thread.currentThread().getName() + " is done with DFS");
        } catch (ModelException | SCXMLExpressionException exc) {
            log.error("Exception has occurred during DFS worker thread", exc);
        }
    }
}
