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
 * Date: 8/18/14
 */
public interface WorkManager {

    /**
     * Returns a string to use as a response to a consumer's request for a WorkBlock
     *
     * @param name the name assigned to the requesting consumer
     * @return a description of a WorkBlock
     */
    String requestBlock(String name);

    /**
     * Return a unique name for use identifying a consumer
     *
     * @return a unique name
     */
    String requestName();

    /**
     * Handles a progress report from a consumer
     *
     * @param name the name assigned to the consumer
     * @param report the report
     * @return a response to the report
     */
    String makeReport(String name, String report);

    /**
     * Prepares the WorkManager to receive reports/requests from consumers
     */
    void prepareServer();

    /**
     * Prepares the WorkManager to make progress reports to the user
     */
    void prepareStatus();

    /**
     * Returns the host name of the WorkManager
     *
     * @return the host name
     */
    String getHostName();

    /**
     * Returns the port the WorkManager will be listening on
     *
     * @return the port number
     */
    int getListeningPort();
}
