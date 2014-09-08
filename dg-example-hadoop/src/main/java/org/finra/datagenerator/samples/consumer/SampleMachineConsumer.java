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
import org.finra.datagenerator.samples.manager.LineCountManager;
import org.finra.datagenerator.samples.transformer.SampleMachineTransformer;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private String[] template = new String[]{"var_out_V1", "var_out_V2", "var_out_V3",
            "var_out_V4", "var_out_V5", "var_out_V6",
            "var_out_V7", "var_out_V8", "var_out_V9"};

    private long currentRow, finalRow;

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

        currentRow = -1;
        finalRow = -2;

        setReportGap(1000);
    }

    private void makeReport(boolean force) {
        long time = System.currentTimeMillis();
        if (time > nextReport || force) {
            long delta = currentLineCount - lastReportedLineCount;
            lastReportedLineCount = currentLineCount;

            sendRequest("this/report/" + String.valueOf(delta), handler);

            nextReport = time + reportGap;
        }
    }

    /**
     * Exit reporting up to distributor, using information gained from status reports to the LineCountManager
     *
     * @return a boolean of whether this consumer should immediately exit
     */
    public boolean isExit() {
        if (currentRow > finalRow) { //request new block of work
            String newBlock = this.sendRequestSync("this/request/block");
            LineCountManager.LineCountBlock block = new LineCountManager.LineCountBlock(0, 0);

            if (newBlock.contains("exit")) {
                getExitFlag().set(true);
                makeReport(true);
                return true;
            } else {
                block.buildFromResponse(newBlock);
            }

            currentRow = block.getStart();
            finalRow = block.getStop();
        } else { //report the number of lines written
            makeReport(false);

            if (exit.get()) {
                getExitFlag().set(true);
                return true;
            }
        }

        return false;
    }

    /**
     * Consume a data set (Map of Variable names and their Values)
     *
     * @param initialVars a map containing the initial variables assignments
     * @return Number of rows written; 0 rows if exiting 1 row otherwise
     */
    public int consume(Map<String, String> initialVars) {
        if (isExit()) {
            return 0;
        }

        currentLineCount++;
        currentRow++;
        return super.consume(initialVars);
    }
}
