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
 * Meant to indicate unrecoverable errors with a user's SCXML model encountered at runtime
 * <p/>
 * For example, throw this exception if the user provides a model which cannot be
 * processed correctly by DataGenerator for some reason.
 */
public class DataGeneratorModelException extends RuntimeException {

    /**
     * Create a new DataGeneratorModelException from an existing exception and a message
     *
     * @param message A message for the end user about this error
     * @param e       An existing exception to be wrapped by this error
     */
    public DataGeneratorModelException(final String message, final Exception e) {
        super(message, e);
    }

    /**
     * Create a new DataGeneratorModelException from a message String
     *
     * @param message A message for the end user about this error
     */
    public DataGeneratorModelException(final String message) {
        super(message);
    }
}
