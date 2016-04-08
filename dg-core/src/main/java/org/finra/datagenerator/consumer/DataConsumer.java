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

import org.apache.log4j.Logger;
import org.finra.datagenerator.reporting.ReportingHandler;
import org.finra.datagenerator.writer.DataWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Processes search results produced by a SearchDistributor.
 */
public class DataConsumer {

    private static final Logger log = Logger.getLogger(DataConsumer.class);
    private DataPipe dataPipe;
    private final List<DataTransformer> dataTransformers = new ArrayList<>();
    private final List<DataWriter> dataWriters = new ArrayList<>();
    private AtomicBoolean hardExitFlag;

    private long maxNumberOfLines = 10000;

    private String reportingHost;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

    /**
     * Public default constructor
     */
    public DataConsumer() {
        this.dataPipe = new DataPipe(this);
    }

    /**
     * Adds a data transformer to the end of the data transformers list
     *
     * @param dc a reference to a DataTransformer
     * @return a reference to the current DataConsumer
     */
    public DataConsumer addDataTransformer(DataTransformer dc) {
        dataTransformers.add(dc);
        return this;
    }

    /**
     * Adds a DataWriter to the end of the dataWriters list
     *
     * @param ow a reference to a data writer
     * @return a reference to the current DataConsumer
     */
    public DataConsumer addDataWriter(DataWriter ow) {
        this.dataWriters.add(ow);
        return this;
    }

    /**
     * A setter for the reporting host
     *
     * @param reportingHost a String containing the URL of the reporting host
     * @return a reference to the current DataConsumer
     */
    public DataConsumer setReportingHost(String reportingHost) {
        this.reportingHost = reportingHost;
        return this;
    }

    /**
     * A setter for maxNumberOfLines
     *
     * @param maxNumberOfLines the max number of lines
     * @return a reference to the current DataConsumer
     */
    public DataConsumer setMaxNumberOfLines(long maxNumberOfLines) {
        this.maxNumberOfLines = maxNumberOfLines;
        return this;
    }

    /**
     * Setter for exit flag
     *
     * @param flag a reference to an AtomicBoolean
     * @return a reference to the current DataConsumer
     */
    public DataConsumer setExitFlag(AtomicBoolean flag) {
        hardExitFlag = flag;
        return this;
    }

    public AtomicBoolean getExitFlag() {
        return hardExitFlag;
    }

    public long getMaxNumberOfLines() {
        return this.maxNumberOfLines;
    }

    public String getReportingHost() {
        return this.reportingHost;
    }

    public DataPipe getDataPipe() {
        return this.dataPipe;
    }

    /**
     * Consumes a produced result. Calls every transformer in sequence, then
     * calls every dataWriter in sequence.
     *
     * @param initialVars a map containing the initial variables assignments
     * @return the number of lines written
     */
    public int consume(Map<String, String> initialVars) {
        this.dataPipe = new DataPipe(this);

        // Set initial variables
        for (Map.Entry<String, String> ent : initialVars.entrySet()) {
            dataPipe.getDataMap().put(ent.getKey(), ent.getValue());
        }

        // Call transformers
        for (DataTransformer dc : dataTransformers) {
            dc.transform(dataPipe);
        }

        // Call writers
        for (DataWriter oneOw : dataWriters) {
            try {
                oneOw.writeOutput(dataPipe);
            } catch (Exception e) { //NOPMD
                log.error("Exception in DataWriter", e);
            }
        }

        return 1;
    }

    /**
     * Consumes a produced result. Calls every transformer in sequence, then
     * returns the produced result(s) back to the caller
     *
     * Children may override this class to produce more than one consumed result
     *
     * @param initialVars a map containing the initial variables assignments
     * @return the produced output map
     */
    public List<Map<String, String>> transformAndReturn(Map<String, String> initialVars) {
        this.dataPipe = new DataPipe(this);

        // Set initial variables
        for (Map.Entry<String, String> ent : initialVars.entrySet()) {
            dataPipe.getDataMap().put(ent.getKey(), ent.getValue());
        }

        // Call transformers
        for (DataTransformer dc : dataTransformers) {
            dc.transform(dataPipe);
        }

        List<Map<String, String>> result = new LinkedList<>();
        result.add(dataPipe.getDataMap());
        return result;
    }

    /**
     * Creates a future for sending a request to the reporting host and ignoring
     * the response.
     *
     * @param path the path at the reporting host where the request should be
     *             sent
     * @return a {@link java.util.concurrent.Future} that wraps this activity
     */
    public Future<String> sendRequest(final String path) {
        return sendRequest(path, null);
    }

    /**
     * Creates a future that will send a request to the reporting host and call
     * the handler with the response
     *
     * @param path             the path at the reporting host which the request will be made
     * @param reportingHandler the handler to receive the response once executed
     *                         and recieved
     * @return a {@link java.util.concurrent.Future} for handing the request
     */
    public Future<String> sendRequest(final String path, final ReportingHandler reportingHandler) {
        return threadPool.submit(new Callable<String>() {
            @Override
            public String call() {
                String response = getResponse(path);

                if (reportingHandler != null) {
                    reportingHandler.handleResponse(response);
                }

                return response;
            }
        });
    }

    /**
     * Sends a synchronous request to the reporting host returning the response
     *
     * @param path the path inside the reporting host to send the request to
     * @return a String containing the response
     */
    public String sendRequestSync(String path) {
        return getResponse(path);
    }

    private String getResponse(String path) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL("http://" + reportingHost + "/" + path);

            URLConnection urlConnection = url.openConnection();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            log.error("Error while reading: " + path + " from " + reportingHost);
        }
        return content.toString();
    }
}
