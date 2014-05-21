package org.finra.datagenerator.distributor.multithreaded;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.consumer.defaults.ChainConsumer;
import org.finra.datagenerator.consumer.defaults.ConsumerResult;
import org.finra.datagenerator.consumer.defaults.DefaultConsumer;
import org.finra.datagenerator.consumer.defaults.OutputStreamConsumer;
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
    private DataConsumer userDataOutput;
    private String stateMachineText;
    private AtomicBoolean exitFlag = null;
    private long maxNumberOfLines = -1;

    private Object lock;

    public DefaultDistributor() {
        ChainConsumer cc = new ChainConsumer();
        cc.addConsumer(new DefaultConsumer());
        cc.addConsumer(new OutputStreamConsumer(System.out));
        this.userDataOutput = cc;
    }

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
        List<AtomicBoolean> flags = new ArrayList<AtomicBoolean>();
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
                exitFlag.set(checkAllFlags(flags));
            }

            // Now, wait for the output thread to get done
            outputThread.join();
            log.info("DONE");
        } catch (InterruptedException ex) {
            log.info("Interrupted !!... exiting", ex);
        }
    }

    private boolean checkAllFlags(List<AtomicBoolean> flags) {
        for (AtomicBoolean flag : flags) {
            if (!flag.get()) {
                return false;
            }
        }

        return true;
    }

    private void produceOutput() throws IOException {
        long lines = 0;
        while (!Thread.interrupted() && !exitFlag.get()) {
            if (!Thread.interrupted()) {
                HashMap<String, String> row = queue.poll();
                if (row != null) {
                    ConsumerResult cr = new ConsumerResult(maxNumberOfLines);
                    for (Map.Entry<String, String> ent : row.entrySet()) {
                        cr.getDataMap().put(ent.getKey(), ent.getValue());
                    }

                    exitFlag.set(cr.getExitFlag().get());
                    userDataOutput.consume(cr);
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
    public void setExitFlag(AtomicBoolean exitFlag) {
        this.exitFlag = exitFlag;
    }

}
