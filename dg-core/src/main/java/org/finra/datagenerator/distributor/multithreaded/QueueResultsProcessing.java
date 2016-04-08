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
import org.finra.datagenerator.distributor.ProcessingStrategy;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;

/**
 * A simple wrapper for storing Frontier search results into a queue
 */
public class QueueResultsProcessing implements ProcessingStrategy, Serializable {

    private static final Logger log = Logger.getLogger(QueueResultsProcessing.class);

    private final Queue<Map<String, String>> queue;

    /**
     * A simple wrapper for storing Frontier search results into a queue. Poll the provided queue to access the results.
     *
     * @param queue the queue to use as storage of the produced results
     */
    public QueueResultsProcessing(final Queue<Map<String, String>> queue) {
        this.queue = queue;
    }

    /**
     * Stores the output from a Frontier into the queue, pausing and waiting if the given queue is too large
     *
     * @param resultsMap map of String and String representing the output of a Frontier's DFS
     */
    @Override
    public void processOutput(Map<String, String> resultsMap) {
        queue.add(resultsMap);

        if (queue.size() > 10000) {
            log.info("Queue size " + queue.size() + ", waiting");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                log.info("Interrupted ", ex);
            }
        }
    }
}
