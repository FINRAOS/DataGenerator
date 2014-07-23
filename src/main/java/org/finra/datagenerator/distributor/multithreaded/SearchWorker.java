package org.finra.datagenerator.distributor.multithreaded;

import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Logger;
import org.finra.datagenerator.distributor.SearchProblem;
import org.finra.datagenerator.scxml.DataGeneratorExecutor;
import org.finra.datagenerator.scxml.PossibleState;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by robbinbr on 3/14/14.
 */
public class SearchWorker implements Runnable {

    protected static final Logger log = Logger.getLogger(SearchWorker.class);

    private PossibleState initialState;
    private Queue queue;
    private DataGeneratorExecutor executor;
    private Set<String> varsOut;
    private Map<String, String> initialVariablesMap;
    private List<String> initialEventsList;
    private Map<String, AtomicBoolean> flags;

    public SearchWorker(SearchProblem problem, String stateMachineText, Queue queue, Map<String, AtomicBoolean> flags) throws ModelException,
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
        } catch (Exception exc) {
            log.error("Exception has occurred during DFS worker thread", exc);
        }

        //flags.put("exit", new AtomicBoolean(true));
    }
}
