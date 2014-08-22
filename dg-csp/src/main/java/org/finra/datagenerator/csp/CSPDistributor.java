package org.finra.datagenerator.csp;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/11/14
 */

import org.finra.datagenerator.consumer.DataConsumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class CSPDistributor {
    private int threadCount = 1;
    private final Queue<HashMap<String, String>> queue = new ConcurrentLinkedQueue<>();
    private DataConsumer userDataOutput;
    private ConstraintSatisfactionProblem csp;
    private final Map<String, AtomicBoolean> flags = new HashMap<>();
    private long maxNumberOfLines = -1;

    /**
     * Sets the maximum number of lines to generate
     *
     * @param numberOfLines a long containing the maximum number of lines to
     * generate
     * @return a reference to the current DefaultDistributor
     */
    public CSPDistributor setMaxNumberOfLines(long numberOfLines) {
        this.maxNumberOfLines = numberOfLines;
        return this;
    }

    /**
     * Sets the number of threads to use
     *
     * @param threadCount an int containing the thread count
     * @return a reference to the current DefaultDistributor
     */
    public CSPDistributor setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public DataConsumer getUserDataOutput() {
        return userDataOutput;
    }

    public void setUserDataOutput(DataConsumer userDataOutput) {
        this.userDataOutput = userDataOutput;
    }

    public ConstraintSatisfactionProblem getCsp() {
        return csp;
    }

    public void setCsp(ConstraintSatisfactionProblem csp) {
        this.csp = csp;
    }

    public void distribute(List<CSPPossibleState> searchProblemList) {

        // Start output thread (consumer)
        Thread outputThread = new Thread() {
            @Override
            public void run() {
                try {
                    produceOutput();
                } catch (IOException ex) {
                    //log.error("Error during output", ex);
                }
            }
        };
        outputThread.start();

        // Start search threads (producers)
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
        for (CSPPossibleState problem : searchProblemList) {
            Runnable worker = null;
            try {
                worker = new CSPSearchWorker(problem, csp, queue, flags);
                threadPool.execute(worker);
            } catch (Exception e) {
                //log.error("Error while initializing SearchWorker threads", e);
            }
        }

        threadPool.shutdown();
        try {
            // Wait for exit
            while (!threadPool.isTerminated()) {
                //log.debug("Waiting for threadpool to terminate");
                Thread.sleep(1000);
            }

            //alert the output thread that the worker threads are done
            flags.put("exit", new AtomicBoolean(true));

            // Now, wait for the output thread to get done
            outputThread.join();
            //log.info("DONE");
        } catch (InterruptedException ex) {
            //log.info("Interrupted !!... exiting", ex);
        }
    }

    private void produceOutput() throws IOException {
        long lines = 0;
        while (!Thread.interrupted() && (!flags.containsKey("exitNow") || !flags.get("exitNow").get())) {
            HashMap<String, String> row = queue.poll();
            if (row != null) {
                lines += userDataOutput.consume(row);
            } else {
                if (flags.containsKey("exit") && flags.containsKey("exit")) {
                    break;
                }
            }

            if (maxNumberOfLines != -1 && lines >= maxNumberOfLines) {
                flags.put("exitNow", new AtomicBoolean(true));
                break;
            }
        }

        if (null != flags && flags.containsKey("exit") && flags.get("exit").get()) {
            //log.info("Exiting, exit flag ('exit') is true");
        }
    }

    /**
     * Returns true if any of the flags is true
     *
     * @param flags a map of atomic integers
     * @return a boolean
     *
     * TODO: needs to be refactored or removed when flags are changed
     */
    public static boolean isSomeFlagTrue(Map<String, AtomicBoolean> flags) {
        for (AtomicBoolean flag : flags.values()) {
            if (flag.get()) {
                return true;
            }
        }
        return false;
    }

    public void setFlag(String name, AtomicBoolean flag) {
        flags.put(name, flag);
    }
}

