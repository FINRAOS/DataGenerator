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

import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.distributor.ProcessingStrategy;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Brijesh on 9/29/2015.
 */
public class SingleThreadedProcessing implements ProcessingStrategy, Serializable {

    private DataConsumer userDataOutput;
    private long maxNumberOfLines = -1;
    private AtomicLong lines = new AtomicLong(0);
    private AtomicBoolean hardExitFlag = new AtomicBoolean(false);
    private DataPipe dataPipe;
    //protected static final Logger log = Logger.getLogger(SingleThreadedProcessing.class);

    /**
     * Constructor
     *
     * @param maximumNumberOfLines set maximumNumberOfLines
     */
    public SingleThreadedProcessing(final long maximumNumberOfLines) {
        maxNumberOfLines = maximumNumberOfLines;
    }

    /**
     * Get MaximumNumber of Lines
     * @return Maximum Number of Lines
     */
    public final long getMaximumNumberOfLines() {
        return maxNumberOfLines;
    }

    /**
     * Set Data Consumer
     * @param dataConsumer data consumer
     * @return SingleThreaderProcessing
     */
    public SingleThreadedProcessing setDataConsumer(final DataConsumer dataConsumer) {
        this.userDataOutput = dataConsumer;
        dataConsumer.setExitFlag(hardExitFlag);
        return this;
    }

    /**
     * Pass the generated maps to consumer for processing
     *
     * @param map Map of String and String
     * @throws IOException Input Output exception
     * @return DataPipe
     */
    public DataPipe processOutput(Map<String, String> map) throws IOException {

        long linesLong = lines.longValue();

        while (!hardExitFlag.get() && maxNumberOfLines != -1 && linesLong < maxNumberOfLines) {

            linesLong += 1;

            if (map != null) {
                dataPipe = userDataOutput.consumeMap(map);
            }
        }
        return dataPipe;
    }
}