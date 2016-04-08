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
package org.finra.datagenerator.distributor;

import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.engine.Frontier;

import java.util.List;

/**
 * Takes Frontiers produced by an Engine and completes the search on each Frontier,
 * giving the results to a DataConsumer for post-processing and outputting.
 */
public interface SearchDistributor {

    /**
     * Sets the data consumer
     *
     * @param dataConsumer the DataConsumer
     * @return a reference to the current SearchDistributor
     */
    SearchDistributor setDataConsumer(DataConsumer dataConsumer);

    /**
     * Distributes the list of the problems
     *
     * @param frontierList a list containing the search problems to
     * distribute
     */
    void distribute(List<Frontier> frontierList);

}
