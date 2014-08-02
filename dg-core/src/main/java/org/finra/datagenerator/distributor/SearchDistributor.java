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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author robbinbr
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
     * Sets the state machine XML
     *
     * @param stateMachineText a String containing the state machine XML
     * @return a reference to the current SearchDistributor
     */
    SearchDistributor setStateMachineText(String stateMachineText);

    /**
     * Distributes the list of the problems
     *
     * @param searchProblemList a list containing the search problems to
     * distribute
     */
    void distribute(List<SearchProblem> searchProblemList);

    /**
     * TODO: Buggy !! this function is adding a new instance of AromicBoolen and
     * not setting the value of a current atomic boolean
     *
     * @param name the name of the flag
     * @param flag the value of the flag
     */
    void setFlag(String name, AtomicBoolean flag);
}
