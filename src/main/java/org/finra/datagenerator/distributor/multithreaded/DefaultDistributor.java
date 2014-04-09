package org.finra.datagenerator.distributor.multithreaded;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.consumer.defaults.DefaultOutput;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.distributor.SearchProblem;
import org.xml.sax.SAXException;

/**
 * Created by robbinbr on 3/24/14.
 */
public class DefaultDistributor implements SearchDistributor {

    protected static final Logger log = Logger.getLogger(DefaultDistributor.class);

    private int threadCount = 1;
    private final Queue<HashMap<String, String>> queue = new ConcurrentLinkedQueue<HashMap<String, String>>();
    private Thread outputThread;
    private DataConsumer userDataOutput = new DefaultOutput(System.out);
    private String stateMachineText;
    private AtomicBoolean exitFlag = null;
    private long maxNumberOfLines = -1;

    public DefaultDistributor setDataConsumer(DataConsumer dataConsumer) {
        this.userDataOutput = dataConsumer;
        return this;
    }

    public DefaultDistributor setMaxNumberOfLines(long numberOfLines) {
        this.maxNumberOfLines = numberOfLines;
        return this;
    }

    public DefaultDistributor setThreadCount(int threadCount) {
        this.threadCount = threadCount;
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
        outputThread = new Thread() {
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
            } catch (ModelException e) {
                log.error("Error while initializing SearchWorker threads", e);
            } catch (SAXException e) {
                log.error("Error while initializing SearchWorker threads", e);
            } catch (IOException e) {
                log.error("Error while initializing SearchWorker threads", e);
            }

            threadPool.execute(worker);
        }

        // Wait for threadPool shutdown to complete
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            try {
                //log.debug("process() is waiting for thread pool to terminate");
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }

        // Wait for queue to empty (finish processing any pending output)
        while (!exitFlag.get() && !queue.isEmpty()) {
            try {
                //log.debug("process() is waiting for queue to empty");
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void produceOutput() throws IOException {
        long lines = 0;
        while (!Thread.interrupted() && !exitFlag.get()) {
            while (!Thread.interrupted() && queue.isEmpty()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
            }

            if (!Thread.interrupted()) {
                HashMap<String, String> row = queue.poll();
                userDataOutput.consume(row, exitFlag);
                lines++;
                if (maxNumberOfLines != -1 && lines >= maxNumberOfLines) {
                    exitFlag.set(true);
                    break;
                }
            }
        }
    }

    @Override
    public void setExitFlag(AtomicBoolean exitFlag) {
        this.exitFlag = exitFlag;
    }

}
