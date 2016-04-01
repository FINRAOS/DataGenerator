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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.finra.datagenerator.reporting.ReportingHandler;
import org.finra.datagenerator.writer.DataWriter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by RobbinBr on 7/2/2014.
 */
public class DataConsumerTest {

    private Server server;
    private int requestCount;

    private String runJetty() throws Exception {
        requestCount = 0;

        if (server != null) {
            server.stop();
            server = null;
        }

        server = new Server(0);
        Handler jettyHandler = new AbstractHandler() {

            @Override
            public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                               HttpServletResponse response) throws IOException, ServletException {
                response.setContentType("text/plain");
                response.getWriter().write(request.getRequestURI().substring(1));
                requestCount++;
                ((Request) request).setHandled(true);
            }
        };

        // Select any available port
        server.setHandler(jettyHandler);
        server.start();
        int port = -3;

        Connector[] connectors = server.getConnectors();
        if (connectors[0] instanceof NetworkConnector) {
            NetworkConnector nc = (NetworkConnector) connectors[0];
            port = nc.getLocalPort();
        }

        return "127.0.0.1:" + port;
    }


    /**
     * Tests initial values
     */
    @Test
    public void testInitialValues() {
        TestTransformerWriter theTransformerWriter = new TestTransformerWriter();
        DataConsumer theConsumer = new DataConsumer();

//        theConsumer.addDataTransformer(theTransformerWriter);
//        theConsumer.addDataWriter(theTransformerWriter);

        Map<String, String> data = new HashMap<String, String>();
        data.put("var1", "var1val");
        data.put("var2", "var2val");
        data.put("var3", "var3val");
        data.put("var4", "var4val");
        data.put("var5", "var5val");

        theConsumer.consume(data);

        Assert.assertEquals("var1val", theConsumer.getDataPipe().getDataMap().get("var1"));
        Assert.assertEquals("var2val", theConsumer.getDataPipe().getDataMap().get("var2"));
        Assert.assertEquals("var3val", theConsumer.getDataPipe().getDataMap().get("var3"));
        Assert.assertEquals("var4val", theConsumer.getDataPipe().getDataMap().get("var4"));
        Assert.assertEquals("var5val", theConsumer.getDataPipe().getDataMap().get("var5"));

    }

    /**
     * Tests writers
     */
    @Test
    public void testWriter() {
        TestTransformerWriter theTransformerWriter = new TestTransformerWriter();
        DataConsumer theConsumer = new DataConsumer();

//        theConsumer.addDataTransformer(theTransformerWriter);
        theConsumer.addDataWriter(theTransformerWriter);

        Map<String, String> data = new HashMap<String, String>();
        data.put("var1", "var1val");
        data.put("var2", "var2val");
        data.put("var3", "var3val");
        data.put("var4", "var4val");
        data.put("var5", "var5val");

        theConsumer.consume(data);

        Assert.assertEquals(1, theTransformerWriter.getData().size());

        Map<String, String> firstMap = theTransformerWriter.getData().get(0);
        Assert.assertEquals(5, firstMap.size());
        Assert.assertEquals("var1val", firstMap.get("var1"));
        Assert.assertEquals("var2val", firstMap.get("var2"));
        Assert.assertEquals("var3val", firstMap.get("var3"));
        Assert.assertEquals("var4val", firstMap.get("var4"));
        Assert.assertEquals("var5val", firstMap.get("var5"));
    }

    /**
     * Tests transformers
     */
    @Test
    public void testTransformer() {
        TestTransformerWriter theTransformerWriter = new TestTransformerWriter();
        DataConsumer theConsumer = new DataConsumer();

        theConsumer.addDataTransformer(theTransformerWriter);
//        theConsumer.addDataWriter(theTransformerWriter);

        Map<String, String> data = new HashMap<String, String>();
        data.put("var1", "var1val");
        data.put("var2", "var2val");
        data.put("var3", "var3val");
        data.put("var4", "var4val");
        data.put("var5", "var5val");

        theConsumer.consume(data);

        Assert.assertEquals(0, theTransformerWriter.getData().size());

        Map<String, String> firstMap = theConsumer.getDataPipe().getDataMap();
        Assert.assertEquals(5, firstMap.size());
        Assert.assertEquals("transformed_var1val", firstMap.get("var1"));
        Assert.assertEquals("transformed_var2val", firstMap.get("var2"));
        Assert.assertEquals("transformed_var3val", firstMap.get("var3"));
        Assert.assertEquals("transformed_var4val", firstMap.get("var4"));
        Assert.assertEquals("transformed_var5val", firstMap.get("var5"));
    }

    /**
     * Tests sending a request with no handling
     *
     * @throws Exception exception
     */
    @Test
    public void testSendRequestNoHandling() throws Exception {
        String reportingHost = runJetty();
        DataConsumer theConsumer = new DataConsumer();
        theConsumer.setReportingHost(reportingHost);

        Future<String> response = theConsumer.sendRequest("test1");
        Assert.assertEquals("test1", response.get().trim());
    }

    /**
     * Tests sending a synchronized request
     *
     * @throws Exception exception
     */
    @Test
    public void testSendRequestSync() throws Exception {
        String reportingHost = runJetty();
        DataConsumer theConsumer = new DataConsumer();
        theConsumer.setReportingHost(reportingHost);

        String response = theConsumer.sendRequestSync("test1");
        Assert.assertEquals("test1", response.trim());
    }

    /**
     * Tests sending a request from the writer
     *
     * @throws Exception exception
     */
    @Test
    public void testSendRequestFromWriter() throws Exception {
        String reportingHost = runJetty();
        DataConsumer theConsumer = new DataConsumer();
        theConsumer.setReportingHost(reportingHost);

        TestWriterWithCountingHandler theWriter = new TestWriterWithCountingHandler();
        theConsumer.addDataWriter(theWriter);

        Map<String, String> data = new HashMap<String, String>();
        data.put("var1", "1_var1val");
        data.put("var2", "1_var2val");
        data.put("var3", "1_var3val");
        data.put("var4", "1_var4val");
        data.put("var5", "1_var5val");
        theConsumer.consume(data);

        Map<String, String> data2 = new HashMap<String, String>();
        data2.put("var1", "2_var1val");
        data2.put("var2", "2_var2val");
        data2.put("var3", "2_var3val");
        data2.put("var4", "2_var4val");
        data2.put("var5", "2_var5val");
        theConsumer.consume(data2);

        Map<String, String> data3 = new HashMap<String, String>();
        data3.put("var1", "3_var1val");
        data3.put("var2", "3_var2val");
        data3.put("var3", "3_var3val");
        data3.put("var4", "3_var4val");
        data3.put("var5", "3_var5val");
        theConsumer.consume(data3);

        Map<String, String> data4 = new HashMap<String, String>();
        data4.put("var1", "4_var1val");
        data4.put("var2", "4_var2val");
        data4.put("var3", "4_var3val");
        data4.put("var4", "4_var4val");
        data4.put("var5", "4_var5val");
        theConsumer.consume(data4);

        Map<String, String> data5 = new HashMap<String, String>();
        data5.put("var1", "5_var1val");
        data5.put("var2", "5_var2val");
        data5.put("var3", "5_var3val");
        data5.put("var4", "5_var4val");
        data5.put("var5", "5_var5val");
        theConsumer.consume(data5);
        Thread.sleep(5000);

        Assert.assertEquals(5, theWriter.getData().size());
        Assert.assertEquals(5, requestCount);

        Assert.assertEquals(theWriter.getData().size(), theWriter.getLatestResponse());
    }


    private class TestWriterWithCountingHandler implements DataWriter, ReportingHandler {

        private long latestResponse = -3;
        private List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        @Override
        public void handleResponse(String response) {
            this.latestResponse = Integer.parseInt(response.trim());
        }

        @Override
        public void writeOutput(DataPipe cr) {
            data.add(cr.getDataMap());
            cr.getDataConsumer().sendRequest("" + data.size(), this);
//            try {
//                String waitForIt = cr.getDataConsumer().sendRequest("" + data.size(), this).get().trim();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                Assert.fail();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//                Assert.fail();
//            }

            System.out.println("Output saw a : " + cr.getDataMap());
        }

        public List<Map<String, String>> getData() {
            return data;
        }

        public long getLatestResponse() {
            return latestResponse;
        }
    }

    private class TestTransformerWriter implements DataTransformer, DataWriter {

        private List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        @Override
        public void transform(DataPipe cr) {
            for (String key : cr.getDataMap().keySet()) {
                String value = cr.getDataMap().get(key);
                cr.getDataMap().put(key, "transformed_" + value);
            }
        }

        @Override
        public void writeOutput(DataPipe cr) {
            data.add(cr.getDataMap());
            System.out.println("Output saw a : " + cr.getDataMap());
        }

        public List<Map<String, String>> getData() {
            return data;
        }
    }
}