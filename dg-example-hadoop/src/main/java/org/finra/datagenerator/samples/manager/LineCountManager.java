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

package org.finra.datagenerator.samples.manager;

/**
 * Marshall Peters
 * Date: 8/15/14
 */
public class LineCountManager extends JettyManager {

    /**
     * Constructor
     *
     * @param maxScenarios the number of output rows to produce
     * @param blockSize the number of output rows each block of work encompasses
     */
    public LineCountManager(final long maxScenarios, final long blockSize) {
        super(maxScenarios);
        long start = 0;

        while (start < maxScenarios) {
            long stop = start + blockSize - 1;
            if (stop > maxScenarios) {
                stop = maxScenarios;
            }

            LineCountBlock block = new LineCountBlock(start, stop);
            addBlock(block);

            start = stop + 1;
        }
    }

    /**
     * Represents a WorkBlock consisting of a contiguous set of rows
     */
    public static class LineCountBlock implements WorkBlock {
        private long start, stop;

        /**
         * Constructor
         *
         * @param start the line count of the first row of output covered by this WorkBlock
         * @param stop the line count of the final row of output covered by this WorkBlock, inclusive
         */
        public LineCountBlock(final long start, final long stop) {
            this.start = start;
            this.stop = stop;
        }

        /**
         * Creates a response for a JettyManager to send out to a consumer when this block of work is requested
         *
         * @return start;stop;
         */
        public String createResponse() {
            return String.valueOf(start) + ";" + String.valueOf(stop) + ";";
        }

        /**
         * Converts a response back into a usable LineCountBlock
         *
         * @param response start;stop;
         */
        public void buildFromResponse(String response) {
            String[] bounds = response.split(";");

            start = Long.valueOf(bounds[0]);
            stop = Long.valueOf(bounds[1]);
        }

        /**
         * Return the line count of the starting row
         *
         * @return start
         */
        public long getStart() {
            return start;
        }

        /**
         * Return the line count of the final row
         *
         * @return stop
         */
        public long getStop() {
            return stop;
        }
    }
}
