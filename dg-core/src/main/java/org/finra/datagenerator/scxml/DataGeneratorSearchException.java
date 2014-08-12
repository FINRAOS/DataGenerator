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

package org.finra.datagenerator.scxml;

/**
 * Created by robbinbr on 8/11/2014.
 * <p/>
 * Meant to indicate unrecoverable errors which occur during search operations
 * <p/>
 * For example, throw this exception if a user requests to do a search via
 * input parameters which is infeasible or impossible given the corresponding
 * SCXML model.
 * <p/>
 * Note this is different from DataGeneratorModelException, as the models in this
 * case may be perfectly valid.
 */
public class DataGeneratorSearchException extends RuntimeException {

    /**
     * Create a new DataGeneratorSearchException from an existing exception and a message
     *
     * @param message A message for the end user about this error
     * @param e       An existing exception to be wrapped by this error
     */
    public DataGeneratorSearchException(final String message, final Exception e) {
        super(message, e);
    }

    /**
     * Create a new DataGeneratorSearchException from an existing exception and a message
     *
     * @param message A message for the end user about this error
     */
    public DataGeneratorSearchException(final String message) {
        super(message);
    }

}
