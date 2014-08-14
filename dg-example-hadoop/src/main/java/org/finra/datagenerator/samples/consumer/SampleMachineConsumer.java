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

package org.finra.datagenerator.samples.consumer;

import org.apache.hadoop.mapreduce.Mapper.Context;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.reporting.ReportingHandler;
import org.finra.datagenerator.samples.transformer.SampleMachineTransformer;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


// TODO: Convert this class to use the new DataTransformer and DataWriter interfaces

/**
 * A Sample consumer which uses messaging to a host to coordinate
 * a global stopping condition based on total line count
 */
public class SampleMachineConsumer extends DataConsumer {
    private long lastReportedLineCount, currentLineCount;
    private JenkinsReportingHandler handler;
    private AtomicBoolean exit;
    private long nextReport;
    private long reportGap;
    private String[] template = new String[]{};

    private class JenkinsReportingHandler implements ReportingHandler {
        private final AtomicBoolean exit;

        public JenkinsReportingHandler(final AtomicBoolean exit) {
            this.exit = exit;
        }

        public void handleResponse(String response) {
            if (response.contains("exit")) {
                exit.set(true);
            }
        }
    }

    private void setReportGap(long reportGap) {
        this.reportGap = reportGap;
    }

    /**
     * Constructor for SampleMachineConsumer - needs the Mapper Context
     *
     * @param context A Hadoop MapReduce Mapper.Context to which this consumer
     *                should writer
     */
    public SampleMachineConsumer(final Context context) {
        super();

        ContextWriter contextWrite = new ContextWriter(context, template);
        this.addDataWriter(contextWrite);
        this.addDataTransformer(new SampleMachineTransformer());

        exit = new AtomicBoolean(false);
        handler = new JenkinsReportingHandler(exit);

        setReportGap(1000);
    }

    private void makeReport() {
        long time = System.currentTimeMillis();
        if (time > nextReport) {
            long delta = currentLineCount - lastReportedLineCount;
            lastReportedLineCount = currentLineCount;

            sendRequest(String.valueOf(delta), handler);

            nextReport = time + reportGap;
        }
    }

    /**
     * Exit reporting up to distributor
     *
     * @return a boolean of whether this consumer should immediately exit
     */
    public boolean isExit() {
        makeReport();

        if (exit.get()) {
            getFlags().put("exitNow", new AtomicBoolean(true));
            return true;
        }

        return false;
    }

    /**
     * Consume a data set (Map of Variable names and their Values)
     *
     * @param initialVars a map containing the initial variables assignments
     * @return I am not sure what this is returning
     */
    public int consume(Map<String, String> initialVars) {
        if (isExit()) {
            return 0;
        }

        lastReportedLineCount++;
        return super.consume(initialVars);
    }
}
