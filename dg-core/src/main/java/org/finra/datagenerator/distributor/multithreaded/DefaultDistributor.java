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

import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.engine.Frontier;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Uses a multithreaded approach to process Frontiers in parallel.
 */
public class DefaultDistributor implements SearchDistributor {

    /**
     * Logger
     */
    protected static final Logger log = Logger.getLogger(DefaultDistributor.class);

    private int threadCount = 1;
    private final Queue<Map<String, String>> queue = new ConcurrentLinkedQueue<>();
    private DataConsumer userDataOutput;
    private final AtomicBoolean searchExitFlag = new AtomicBoolean(false);
    private final AtomicBoolean hardExitFlag = new AtomicBoolean(false);
    private long maxNumberOfLines = -1;

    /**
     * Sets the maximum number of lines to generate
     *
     * @param numberOfLines a long containing the maximum number of lines to
     *                      generate
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
        dataConsumer.setExitFlag(hardExitFlag);
        return this;
    }

    @Override
    public void distribute(List<Frontier> frontierList) {

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
        for (Frontier frontier : frontierList) {
            Runnable worker = null;
            worker = new SearchWorker(frontier, queue, searchExitFlag);
            threadPool.execute(worker);
        }

        threadPool.shutdown();
        try {
            // Wait for exit
            while (!threadPool.isTerminated()) {
                log.debug("Waiting for threadpool to terminate");
                Thread.sleep(1000);
            }

            //alert the output thread that the worker threads are done
            searchExitFlag.set(true);

            // Now, wait for the output thread to get done
            outputThread.join();
            log.info("DONE");
        } catch (InterruptedException ex) {
            log.info("Interrupted !!... exiting", ex);
        }
    }

    private void produceOutput() throws IOException {
        long lines = 0;
        while (!Thread.interrupted() && !hardExitFlag.get()) {
            Map<String, String> row = queue.poll();
            if (row != null) {
                lines += userDataOutput.consume(row);
            } else {
                if (searchExitFlag.get()) {
                    break;
                } else if (hardExitFlag.get()) {
                    break;
                }
            }

            if (maxNumberOfLines != -1 && lines >= maxNumberOfLines) {
                break;
            }
        }

        if (searchExitFlag.get()) {
            log.info("Exiting, search exit flag is true");
        }

        if (hardExitFlag.get()) {
            log.info("Exiting, consumer exit flag is true");
        }

        searchExitFlag.set(true);
        hardExitFlag.set(true);
    }

}
