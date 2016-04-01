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
import org.finra.datagenerator.engine.Frontier;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  Implementation of a partial search produced by an Engine's bootstrapping.
 */
public class SearchWorker implements Runnable {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(SearchWorker.class);

    private final Frontier frontier;
    private final Queue<Map<String, String>> queue;
    private final AtomicBoolean flag;

    /**
     * Public constructor
     *
     * @param frontier the problem to search
     * @param queue    a queue that will receive the results
     * @param flag     shared exit flag
     */
    public SearchWorker(final Frontier frontier, final Queue<Map<String, String>> queue, final AtomicBoolean flag) {
        this.queue = queue;
        this.frontier = frontier;
        this.flag = flag;
    }

    @Override
    public void run() {
        log.info(Thread.currentThread().getName() + " is starting DFS");
        frontier.searchForScenarios(new QueueResultsProcessing(queue), flag);
        log.info(Thread.currentThread().getName() + " is done with DFS");
    }
}
