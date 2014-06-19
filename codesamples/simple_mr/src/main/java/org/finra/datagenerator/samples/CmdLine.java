package org.finra.datagenerator.samples;/*
 * Copyright 2014 mosama.
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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.exec.ChartExec;
import org.finra.datagenerator.exec.LogInitializer;
import org.finra.datagenerator.samples.consumer.SampleMachineConsumer;
import org.finra.datagenerator.samples.distributor.hdfs.HDFSDistributor;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.log.Log;

public class CmdLine extends Configured implements Tool {

    private static final int BUFFER_SIZE = 1024 * 1024;
    private static DefaultDistributor defaultDist = null;
    private static HDFSDistributor hdfsDist = null;
    private Configuration configuration = null;
    static int listeningPort;
    static String hostName;
    private static Server server;
    private static Handler jettyHandler;
    private static int maxScenarios;
    private static AtomicLong globalLineCounter = new AtomicLong(0);
    static final AtomicLong time = new AtomicLong(System.currentTimeMillis());
    static final long startTime = System.currentTimeMillis();
    static long lastCount = 0;
    
    /**
     * Prints the help on the command line
     *
     * @param options
     */
    public static void printHelp(Options options) {
        Collection<Option> c = options.getOptions();
        System.out.println("Command line options are:");
        int longestLongOption = 0;
        for (Option op : c) {
            if (op.getLongOpt().length() > longestLongOption) {
                longestLongOption = op.getLongOpt().length();
            }
        }

        longestLongOption += 2;
        String spaces = StringUtils.repeat(" ", longestLongOption);

        for (Option op : c) {
            System.out.print("\t-" + op.getOpt() + " --" + op.getLongOpt());
            if (op.getLongOpt().length() < spaces.length()) {
                System.out.print(spaces.substring(op.getLongOpt().length()));
            } else {
                System.out.print(" ");
            }
            System.out.println(op.getDescription());
        }
    }

    public ChartExec parseCommandLine(String args[]) throws ParseException, Exception {
        // create the command line parser
        CommandLineParser parser = new GnuParser();

        // create the Options
        final Options options = new Options()
                .addOption("h", "help", false, "print help.")
                .addOption("i", "inputfile", true, "the scxml input file")
                .addOption("v", "initialvariables", true,
                        "comma separated list of the initial variables and their values in the form of var1=val1,"
                        + "var2=val2"
                )
              
                .addOption("e", "initalevents", true,
                        "a comma separated list of the initial set of events to trigger before searching for scenarios")
                .addOption("n", "numberoftimes", true,
                        "an integer, the number of time to run the template of a row")
                .addOption("H", "hdfssequencefile", true,
                        "the path of the hdfs sequence file to write to")
                .addOption("L", "loglevel", true,
                        "set the log level")
                 .addOption("s", "maxscenarios", true,
                        "Maximum number of scenarios to generate. Default 10,000")
                .addOption("d", "dist", true, "distribution method (shared or hdfs). Default is shared.")
                .addOption("libjars", true, "Needed for MR Job")
        		.addOption("m", "minbootstrapstates", true,
                "Minimum number of states to explore using BFS before using parallel DFS search. Default is 0"
                + " (no BFS).");

   
        CommandLine cmd = parser.parse(options, args);

        runJetty();
        ChartExec chartExec = new ChartExec();
       
  

        defaultDist = new DefaultDistributor();
        if (cmd.hasOption("d")) {
            String stringValue = cmd.getOptionValue('d');
            if (StringUtils.isNotEmpty(stringValue)) {
                if (stringValue.equals("hdfs")) {
                    Log.info("Setting to HDFSDistributor");
                	hdfsDist = new HDFSDistributor().setFileRoot("brownbag_demo").setReportingHost(hostName + ":" + listeningPort);;
                    defaultDist = null;
                }
            } else {
                System.err.println("Unparsable numeric value for option 's':" + stringValue);
            }
        }

        if (cmd.hasOption("i")) {
            if (hdfsDist != null) {
                Path inFile = new Path(cmd.getOptionValue('i'));
                FileSystem fs = FileSystem.get(this.getConf());
                FSDataInputStream in = fs.open(inFile);
                chartExec.setInputFileStream(in);
            } else {
                chartExec.setInputFileStream(new FileInputStream(new File(cmd.getOptionValue('i'))));
            }
        } else {
            System.err.println("\nERROR: you must state option -i with an input file\n");
        }

        int n = 1;
        if (cmd.hasOption("n")) {
            n = Integer.valueOf(cmd.getOptionValue("n"));
        }

        if (cmd.hasOption("h") || cmd.getOptions().length == 0) {
            printHelp(options);
        }

        if (cmd.hasOption("v")) {
            chartExec.setInitialVariables(cmd.getOptionValue('v'));
        }

        if (cmd.hasOption("e")) {
            chartExec.setInitialEvents(cmd.getOptionValue('e'));
        }


        if (cmd.hasOption('L')) {
            LogInitializer.initialize(cmd.getOptionValue('L'));
        } else {
            LogInitializer.initialize("WARN");
        }
        
        if (cmd.hasOption('r')) {
            String stringValue = cmd.getOptionValue('r');
            if (StringUtils.isNotEmpty(stringValue)) {
                chartExec.setMaxEventReps(Integer.valueOf(stringValue));
            } else {
                System.err.println("Unparsable numeric value for option 'r':" + stringValue);
            }
        }
        
        if (cmd.hasOption('m')) {
            String stringValue = cmd.getOptionValue('m');
            if (StringUtils.isNotEmpty(stringValue)) {
                chartExec.setBootstrapMin(Integer.parseInt(stringValue));
            } else {
                System.err.println("Unparsable numeric value for option 'm':" + stringValue);
            }
        }

        if (cmd.hasOption('s')) {
            String stringValue = cmd.getOptionValue('s');
            if (StringUtils.isNotEmpty(stringValue)) {
                chartExec.setMaxScenarios(maxScenarios = Integer.valueOf(stringValue));
                if (defaultDist != null) {
                    defaultDist.setMaxNumberOfLines(Long.parseLong(stringValue));
                }
                if (hdfsDist != null) {
                    hdfsDist.setMaxNumberOfLines(Long.parseLong(stringValue));
                }
            } else {
                System.err.println("Unparsable numeric value for option 's':" + stringValue);
            }
        }else if (!cmd.hasOption('s')){
        	if (defaultDist != null) {
                defaultDist.setMaxNumberOfLines(10000);
            }
            if (hdfsDist != null) {
                hdfsDist.setMaxNumberOfLines(10000);
            }
        }
        return chartExec;
    }

    public static void main(String args[]) throws Exception {
        CmdLine cmd = new CmdLine();
        Configuration conf = new Configuration();
        int res = ToolRunner.run(conf, cmd, args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        try {
        	Log.info("defaultDist is " + defaultDist);
        	Logger.getLogger("org.apache").setLevel(Level.WARN);

            ChartExec chartExec = parseCommandLine(args);
//            defaultDist = new DefaultDistributor();
//            defaultDist.setDataConsumer(new SampleMachineConsumer());
          
            Log.info("defaultDist is " + defaultDist);
            if (defaultDist != null) {
                chartExec.process(defaultDist);
            } else {
                hdfsDist.setConfiguration(this.configuration);
                hdfsDist.setOutputFileDir("dg-result");
                chartExec.process(hdfsDist);
            }
        } catch(Exception e){
        	Log.warn(e);
        	throw e;
        }finally {
        	 Log.info("Data Generation Complete");
        }

        return 0;
    }

    @Override
    public void setConf(Configuration c) {
        this.configuration = c;
    }

    @Override
    public Configuration getConf() {
        return configuration;
    }
    
    private static void runJetty() throws Exception {
        server = new Server(0);
        jettyHandler = new AbstractHandler() {
            @Override
            public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
            	response.setContentType("text/plain");
            	if (maxScenarios > -1) {
                    String delta = request.getRequestURI().substring(1); // Skip the /
                    if (StringUtils.isNumeric(delta)) {
                        long increment = Long.valueOf(delta);
                        long currentCount = globalLineCounter.addAndGet(increment);
                        String status = "Lines:" + currentCount + " MaxScenarios: " + maxScenarios;
                        if (currentCount < maxScenarios) {
                            
                        	response.getWriter().write("ok " + currentCount);
                            status += " ok";
                        } else {
                         	response.getWriter().write("exit " + currentCount);
                            status += " exit";
                        }
                        long thisTime = System.currentTimeMillis();
                        if (thisTime - time.get() > 1000) {
                            synchronized (time) {
                                if (thisTime - time.get() > 1000) {
                                    long oldValue = time.get();
                                    time.set(thisTime);
                                    double avgRate = 1000.0 * currentCount / (thisTime - startTime);
                                    double instRate = 1000.0 * (currentCount - lastCount) / (thisTime - oldValue);
                                    lastCount = currentCount;
                                    System.out.println(status + " AvgRate:" + ((int) avgRate) + " lines/sec  instRate:" + ((int) instRate) + " lines/sec");
                                }
                            }
                        }
                    } else {
                        response.getWriter().write("ERROR:" + delta + " is not numeric");
                    }
                } else {
                    response.getWriter().write("ok " + maxScenarios);
                }
                ((Request) request).setHandled(true);
            }
        };

        // Select any available port
        server.setHandler(jettyHandler);

        server.start();
        Connector[] connectors = server.getConnectors();
        listeningPort = connectors[0].getLocalPort();
        hostName = InetAddress.getLocalHost().getHostName();

        System.out.println(
                "Listening on port: " + listeningPort);
    }
}
