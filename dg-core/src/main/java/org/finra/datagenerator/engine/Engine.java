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

package org.finra.datagenerator.engine;

import org.finra.datagenerator.distributor.SearchDistributor;

import java.io.InputStream;

/**
 * Performs the bootstrapping search on a data model, producing Frontiers for a SearchDistributor to distribute.
 */
public interface Engine {

    /**
     * Processes the model, produces the required bootstrap Frontiers,
     * and has the provided distributor distribute those Frontiers
     *
     * @param distributor the distributor
     */
    void process(SearchDistributor distributor);

    /**
     * Sets the model, passing it as an InputStream
     *
     * @param inputFileStream the model as an InputStream
     */
    void setModelByInputFileStream(InputStream inputFileStream);

    /**
     * Sets the model, passing it as a String
     *
     * @param model the model as a String
     */
    void setModelByText(String model);

    /**
     * Sets the minimum number of bootstrap states to produce
     *
     * @param min the minimum number of bootstrap states to produce
     * @return an equivalent engine with the minimum number of bootstrap states to produce set
     */
    Engine setBootstrapMin(int min);
}
