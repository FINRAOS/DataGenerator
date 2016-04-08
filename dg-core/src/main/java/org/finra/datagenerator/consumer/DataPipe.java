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
package org.finra.datagenerator.consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Wrapper for search results.
 */
public class DataPipe {

    private final Map<String, String> dataMap = new HashMap<>();
    private final DataConsumer dataConsumer;

    /**
     * Default constructor. Initializes the dataConsumer to {@link DataConsumer}
     */
    public DataPipe() {
        dataConsumer = new DataConsumer();

    }

    /**
     * Constructor sets a user given {@link DataConsumer}
     *
     * @param dataConsumer a reference to {@link DataConsumer}
     */
    public DataPipe(final DataConsumer dataConsumer) {
        this.dataConsumer = dataConsumer;
    }

    /**
     * Constructor sets a max number of lines and shares an exit flag with the
     * DataPipe
     *
     * @param maxNumberOfLines a long containing the maximum number of lines
     * expected to flow through this pipe
     * @param flag an AtomicBoolean exit flag
     */
    public DataPipe(final long maxNumberOfLines, final AtomicBoolean flag) {
        this.dataConsumer = new DataConsumer().setMaxNumberOfLines(maxNumberOfLines).setExitFlag(flag);
    }

    public DataConsumer getDataConsumer() {
        return this.dataConsumer;
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    /**
     * Given an array of variable names, returns a pipe delimited {@link String}
     * of values.
     *
     * @param outTemplate an array of {@link String}s containing the variable
     * names.
     * @return a pipe delimited {@link String} of values
     */
    public String getPipeDelimited(String[] outTemplate) {
        return getDelimited(outTemplate, "|");
    }

    /**
     * Given an array of variable names, returns a delimited {@link String}
     * of values.
     *
     * @param outTemplate an array of {@link String}s containing the variable
     * names.
     * @param separator the delimiter to use
     * @return a pipe delimited {@link String} of values
     */
    public String getDelimited(String[] outTemplate, String separator) {
        StringBuilder b = new StringBuilder(1024);

        for (String var : outTemplate) {
            if (b.length() > 0) {
                b.append(separator);
            }
            b.append(getDataMap().get(var));
        }

        return b.toString();
    }
}
