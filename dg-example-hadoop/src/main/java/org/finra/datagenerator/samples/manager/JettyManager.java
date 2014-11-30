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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Marshall Peters
 * Date: 8/15/14
 */
public class JettyManager implements WorkManager {

    private Handler jettyHandler;
    private AtomicLong globalLineCounter;
    private AtomicLong remainingBlocks;
    private int listeningPort;
    private String hostName;
    private Server server;
    private AtomicLong time;
    private final long maxScenarios;
    private long startTime;
    private long lastCount;

    private Queue<WorkBlock> blocks;
    private AtomicLong serial;

    /**
     * Constructor
     *
     * @param maxScenarios the number of output rows to produce
     */
    public JettyManager(final long maxScenarios) {
        server = null;
        blocks = new LinkedList<>();

        serial = new AtomicLong(0);
        remainingBlocks = new AtomicLong(0);

        this.maxScenarios = maxScenarios;
    }

    /**
     * Returns a description a block of work, or "exit" if no more blocks exist
     *
     * @param name the assigned name of the consumer requesting a block of work
     * @return a description a block of work, or "exit" if no more blocks exist
     */
    public synchronized String requestBlock(String name) {
        if (blocks.isEmpty()) {
            return "exit";
        }

        remainingBlocks.decrementAndGet();
        return blocks.poll().createResponse();
    }

    /**
     * Assigns unique names to each consumer to use in reporting
     *
     * @return an unused name for identifying a consumer
     */
    public synchronized String requestName() {
        return String.valueOf(serial.incrementAndGet());
    }

    /**
     * Handles reports by consumers
     *
     * @param name the name of the reporting consumer
     * @param report the number of lines the consumer has written since last report
     * @return "exit" if maxScenarios has been reached, "ok" otherwise
     */
    public String makeReport(String name, String report) {
        long increment = Long.valueOf(report);
        long currentLineCount = globalLineCounter.addAndGet(increment);

        if (currentLineCount >= maxScenarios) {
            return "exit";
        } else {
            return "ok";
        }
    }

    /**
     * Adds a provided WorkBlock to the Queue of WorkBlocks to be processed by consumers
     *
     * @param block a WorkBlock
     */
    public void addBlock(WorkBlock block) {
        blocks.add(block);
        remainingBlocks.incrementAndGet();
    }

    /**
     * Establishes a thread that on one second intervals reports the number of remaining WorkBlocks enqueued and the
     * total number of lines written and reported by consumers
     */
    public void prepareStatus() {
        globalLineCounter = new AtomicLong(0);
        time = new AtomicLong(System.currentTimeMillis());
        startTime = System.currentTimeMillis();
        lastCount = 0;

        // Status thread regularly reports on what is happening
        Thread statusThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Status thread interrupted");
                    }

                    long thisTime = System.currentTimeMillis();
                    long currentCount = globalLineCounter.get();

                    if (thisTime - time.get() > 1000) {
                        long oldTime = time.get();
                        time.set(thisTime);
                        double avgRate = 1000.0 * currentCount / (thisTime - startTime);
                        double instRate = 1000.0 * (currentCount - lastCount) / (thisTime - oldTime);
                        lastCount = currentCount;
                        System.out.println(currentCount + " AvgRage:" + ((int) avgRate) + " lines/sec  instRate:"
                                + ((int) instRate) + " lines/sec  Unassigned Work: "
                                + remainingBlocks.get() + " blocks");
                    }
                }
            }
        };
        statusThread.start();
    }

    /**
     * Prepares a Jetty server for communicating with consumers.
     */
    public void prepareServer() {
        try {
            server = new Server(0);
            jettyHandler = new AbstractHandler() {
                public void handle(String target, Request req, HttpServletRequest request,
                                   HttpServletResponse response) throws IOException, ServletException {
                    response.setContentType("text/plain");

                    String[] operands = request.getRequestURI().split("/");

                    String name = "";
                    String command = "";
                    String value = "";

                    //operands[0] = "", request starts with a "/"

                    if (operands.length >= 2) {
                        name = operands[1];
                    }

                    if (operands.length >= 3) {
                        command = operands[2];
                    }

                    if (operands.length >= 4) {
                        value = operands[3];
                    }

                    if (command.equals("report")) { //report a number of lines written
                        response.getWriter().write(makeReport(name, value));
                    } else if (command.equals("request") && value.equals("block")) { //request a new block of work
                        response.getWriter().write(requestBlock(name));
                    } else if (command.equals("request") && value.equals("name")) { //request a new name to report with
                        response.getWriter().write(requestName());
                    } else { //non recognized response
                        response.getWriter().write("exit");
                    }

                    ((Request) request).setHandled(true);
                }
            };

            server.setHandler(jettyHandler);

            // Select any available port
            server.start();
            Connector[] connectors = server.getConnectors();
            NetworkConnector nc = (NetworkConnector) connectors[0];
            listeningPort = nc.getLocalPort();
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the port the Jetty server will be listening on
     *
     * @return the port number
     */
    public int getListeningPort() {
        return listeningPort;
    }

    /**
     * Returns the host name of the Jetty server
     *
     * @return the host name
     */
    public String getHostName() {
        return hostName;
    }

}
