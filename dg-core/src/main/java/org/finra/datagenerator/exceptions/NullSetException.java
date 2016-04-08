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

package org.finra.datagenerator.exceptions;

/**
 * A null set exception
 */
public class NullSetException extends RuntimeException {
    /**
     * Constructor with a message
     * @param msg The message
     */
    public NullSetException(final String msg) {
        super(msg);
    }

    /**
     * Constructor with a Throwable
     * @param e The throwable
     */
    public NullSetException(final Throwable e) {
        super(e);
    }

    /**
     * Constructor with a message and a throwable
     * @param msg The message
     * @param e a throwable
     */
    public NullSetException(final String msg, final Throwable e) {
        super(msg, e);
    }
}