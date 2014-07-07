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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by RobbinBr on 5/18/2014.
 */
public class DataConsumer {
    private static final Logger log = Logger.getLogger(DataConsumer.class);
    private DataPipe dataPipe = null;
    private final List<DataTransformer> dataTransformers = new ArrayList<DataTransformer>();
    private List<DataWriter> dataWriters = new ArrayList<DataWriter>();


    private final AtomicBoolean exitFlag = new AtomicBoolean();
    private long maxNumberOfLines = 10000;

    private String reportingHost = null;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

    public DataConsumer() {
        this.dataPipe = new DataPipe(this);
    }

    public DataConsumer addDataTransformer(DataTransformer dc) {
        dataTransformers.add(dc);
        return this;
    }

    public DataConsumer addDataWriter(DataWriter ow) {
        this.dataWriters.add(ow);
        return this;
    }

    public DataConsumer setReportingHost(String reportingHost) {
        this.reportingHost = reportingHost;
        return this;
    }

    public DataConsumer setMaxNumberOfLines(long maxNumberOfLines) {
        this.maxNumberOfLines = maxNumberOfLines;
        return this;
    }

    public AtomicBoolean getExitFlag() {
        return this.exitFlag;
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

    public void consume(Map<String, String> initialVars) {
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
            oneOw.writeOutput(dataPipe);
        }
    }

    public Future<String> sendRequest(final String path) {
        return sendRequest(path, null);
    }

    public Future<String> sendRequest(final String path, final ReportingHandler reportingHandler) {
        return threadPool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String response = getResponse(path);

                if (reportingHandler != null) {
                    reportingHandler.handleResponse(response);
                }

                return response;
            }
        });
    }

    public String sendRequestSync(String path) {
        return getResponse(path);
    }

    private String getResponse(String path) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL("http://" + reportingHost + "/" + path);

            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            log.error("Error while reading: " + path + " from " + reportingHost);
        }
        return content.toString();
    }
}