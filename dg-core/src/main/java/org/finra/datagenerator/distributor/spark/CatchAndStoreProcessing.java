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

package org.finra.datagenerator.distributor.spark;

import org.finra.datagenerator.distributor.ProcessingStrategy;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Simple wrapper for storing Frontier results into a List
 */
public class CatchAndStoreProcessing implements ProcessingStrategy, Serializable {

    private final List<Map<String, String>> storage;

    /**
     * Simple wrapper for storing Frontier results into a List
     *
     * @param storage the List to store into
     */
    public CatchAndStoreProcessing(final List<Map<String, String>> storage) {
        this.storage = storage;
    }

    /**
     * Adds the passes results map to the given List. Poll the List to access the results.
     *
     * @param resultsMap map of String and String to add to the list
     */
    @Override
    public void processOutput(Map<String, String> resultsMap) {
        storage.add(resultsMap);
    }
}
