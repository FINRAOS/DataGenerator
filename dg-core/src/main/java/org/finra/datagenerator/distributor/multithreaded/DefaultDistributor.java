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

import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.distributor.SearchProblem;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by robbinbr on 3/24/14.
 */
public class DefaultDistributor implements SearchDistributor {

    /**
     * Logger
     */
    protected static final Logger log = Logger.getLogger(DefaultDistributor.class);

    private int threadCount = 1;
    private final Queue<HashMap<String, String>> queue = new ConcurrentLinkedQueue<>();
    private DataConsumer userDataOutput;
    private String stateMachineText;
    private final Map<String, AtomicBoolean> flags = new HashMap<>();
    private long maxNumberOfLines = -1;

    /**
     * Sets the maximum number of lines to generate
     *
     * @param numberOfLines a long containing the maximum number of lines to
     * generate
     * @return a reference to the current DefaultDistributor
     */
    public DefaultDistributor setMaxNumberOfLines(long numberOfLines) {
        this.maxNumberOfLines = numberOfLines;
        return this;
    }

    /**
     * Sets the number of threads to use
     *
     * @param threadCount an int containing the thread count
     * @return a reference to the current DefaultDistributor
     */
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
                worker = new SearchWorker(problem, stateMachineText, queue, flags);
                threadPool.execute(worker);
            } catch (ModelException | SAXException | IOException e) {
                log.error("Error while initializing SearchWorker threads", e);
            }
        }

        threadPool.shutdown();
        try {
            // Wait for exit
            while (!threadPool.isTerminated()) {
                log.debug("Waiting for threadpool to terminate");
                Thread.sleep(1000);
            }

            //alert the output thread that the worker threads are done
            flags.put("exit", new AtomicBoolean(true));

            // Now, wait for the output thread to get done
            outputThread.join();
            log.info("DONE");
        } catch (InterruptedException ex) {
            log.info("Interrupted !!... exiting", ex);
        }
    }

    private void produceOutput() throws IOException {
        long lines = 0;
        while (!Thread.interrupted() && (!flags.containsKey("exitNow") || !flags.get("exitNow").get())) {
            HashMap<String, String> row = queue.poll();
            if (row != null) {
                userDataOutput.consume(row);
                lines++;
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
            log.info("Exiting, exit flag ('exit') is true");
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

    @Override
    public void setFlag(String name, AtomicBoolean flag) {
        flags.put(name, flag);
    }
}
