package org.finra.datagenerator.distributor.multithreaded;

import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.distributor.SearchProblem;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by robbinbr on 3/24/14.
 */
public class DefaultDistributor implements SearchDistributor {

    protected static final Logger log = Logger.getLogger(DefaultDistributor.class);

    private int threadCount = 1;
    private final Queue<HashMap<String, String>> queue = new ConcurrentLinkedQueue<HashMap<String, String>>();
    private DataConsumer userDataOutput;
    private String stateMachineText;
    private AtomicBoolean exitFlag = null;
    private long maxNumberOfLines = -1;

    private Object lock;

    public DefaultDistributor setMaxNumberOfLines(long numberOfLines) {
        this.maxNumberOfLines = numberOfLines;
        return this;
    }

    public DefaultDistributor setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    @Override
    public SearchDistributor setDataConsumer(DataConsumer dataConsumer) {
        this.userDataOutput = dataConsumer;
        return this;
    }

    @Override
    public SearchDistributor setStateMachineText(String stateMachine) {
        this.stateMachineText = stateMachine;
        return this;
    }

    @Override
    public void distribute(List<SearchProblem> searchProblemList) {

        // Start output thread (consumer)
        Thread outputThread = new Thread() {
            @Override
            public void run() {
                try {
                    produceOutput();
                } catch (IOException ex) {
                    log.error("Error during output", ex);
                }
            }
        };
        outputThread.start();

        // Start search threads (producers)
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
        for (SearchProblem problem : searchProblemList) {
            Runnable worker = null;
            try {
                worker = new SearchWorker(problem, stateMachineText, queue, exitFlag);
                threadPool.execute(worker);
            } catch (ModelException e) {
                log.error("Error while initializing SearchWorker threads", e);
            } catch (SAXException e) {
                log.error("Error while initializing SearchWorker threads", e);
            } catch (IOException e) {
                log.error("Error while initializing SearchWorker threads", e);
            }
        }

        threadPool.shutdown();
        try {
            // Wait for exit
            while (!threadPool.isTerminated()) {
                log.debug("Waiting for threadpool to terminate");
                Thread.sleep(10);
            }

            while (!exitFlag.get()) {
                log.debug("Waiting for exit");
                Thread.sleep(10);
            }

            // Now, wait for the output thread to get done
            outputThread.join();
            log.info("DONE");
        } catch (InterruptedException ex) {
            log.info("Interrupted !!... exiting", ex);
        }
    }

    private void produceOutput() throws IOException {
        long lines = 0;
        while (!Thread.interrupted() && !exitFlag.get()) {
            if (!Thread.interrupted()) {
                HashMap<String, String> row = queue.poll();
                if (row != null) {
                    userDataOutput.consume(row);
                    lines++;
                }

                if (maxNumberOfLines != -1 && lines >= maxNumberOfLines) {
                    exitFlag.set(true);
                    break;
                }
            }
        }

        if (exitFlag.get()) {
            log.info("Exiting, exitFlag=true");
        }
    }

    @Override
    public DefaultDistributor setExitFlag(AtomicBoolean exitFlag) {
        this.exitFlag = exitFlag;
        return this;
    }

}
