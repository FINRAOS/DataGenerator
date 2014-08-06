package org.finra.datagenerator.samples.consumer;

import org.apache.hadoop.mapreduce.Mapper.Context;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.reporting.ReportingHandler;
import org.finra.datagenerator.samples.transformer.SampleMachineTransformer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SampleMachineConsumer extends DataConsumer {
    private long lastReportedLineCount = 0, currentLineCount = 0;
    private JenkinsReportingHandler handler;
    private AtomicBoolean exit;
    private long nextReport;
    private long reportGap;
    private String[] template = new String[] {};

    private class JenkinsReportingHandler implements ReportingHandler {
        private final AtomicBoolean exit;

        public JenkinsReportingHandler(AtomicBoolean exit) {
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

    public SampleMachineConsumer(Context context) throws IOException {
        super();

        ContextWriter contextWrite = new ContextWriter(context, template);
        this.addDataWriter(contextWrite);
        this.addDataTransformer(new SampleMachineTransformer());

        exit = new AtomicBoolean(false);
        handler = new JenkinsReportingHandler(exit);

        setReportGap(1000);
    }

    public void makeReport() {
        long time = System.currentTimeMillis();
        if (time > nextReport) {
            long delta = currentLineCount - lastReportedLineCount;
            lastReportedLineCount = currentLineCount;

            sendRequest(String.valueOf(delta), handler);

            nextReport = time + reportGap;
        }
    }

    public boolean isExit() {
        makeReport();

        if (exit.get()) {
            getFlags().put("exitNow", new AtomicBoolean(true));
            return true;
        }

        return false;
    }

    public int consume(Map<String, String> initialVars) {
        if (isExit())
            return 0;

        lastReportedLineCount++;
        return super.consume(initialVars);
    }
}
