package org.finra.datagenerator.csp;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/11/14
 */
public class CSPSearchWorker implements Runnable {

    private PartialSolution root;
    private ConstraintSatisfactionProblem csp;
    private Queue queue;
    private Map<String, AtomicBoolean> flags;

    public CSPSearchWorker(PartialSolution root, ConstraintSatisfactionProblem csp, Queue queue, Map<String, AtomicBoolean> flags) {
        this.root = root;
        this.csp = csp;
        this.queue = queue;
        this.flags = flags;
    }

    @Override
    public void run() {
        try {
            CSPExecutor exec = new CSPExecutor(csp);
            exec.DFS(queue, root, flags);
        } catch (Exception e) {

        }
    }
}
