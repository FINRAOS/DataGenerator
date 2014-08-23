#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package};

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.finra.datagenerator.exec.ChartExec;
import org.finra.datagenerator.exec.LogInitializer;
import ${package}.distributor.hdfs.HDFSDistributor;
import ${package}.manager.LineCountManager;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * An example of distributing Data Generator over Hadoop HDFS
 * using Hadoop MapReduce.
 */
public final class CmdLine extends Configured implements Tool {

    private static HDFSDistributor hdfsDist;
    private Configuration configuration;

    /**
     * Prints the help on the command line
     *
     * @param options Options object from commons-cli
     */
    public static void printHelp(final Options options) {
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
            System.out.print("${symbol_escape}t-" + op.getOpt() + " --" + op.getLongOpt());
            if (op.getLongOpt().length() < spaces.length()) {
                System.out.print(spaces.substring(op.getLongOpt().length()));
            } else {
                System.out.print(" ");
            }
            System.out.println(op.getDescription());
        }
    }

    /**
     * Parse command line arguments for the example
     *
     * @param args Command-line arguments for HDFS example
     * @return a ChartExec object resulting from the parsing of command-line options
     * @throws ParseException when args cannot be parsed
     * @throws IOException    when input file cannot be found
     */
    public ChartExec parseCommandLine(final String[] args) throws ParseException, IOException {
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
                        "an integer, the number of TIME to run the template of a row")
                .addOption("H", "hdfssequencefile", true,
                        "the path of the hdfs sequence file to write to")
                .addOption("L", "loglevel", true,
                        "set the log level")
                .addOption("s", "maxscenarios", true,
                        "Maximum number of scenarios to generate. Default 10,000")
                .addOption("m", "minbootstrapstates", true,
                        "Minimum number of states to explore using BFS before using parallel DFS search. Default is 0"
                                + " (no BFS).");


        CommandLine cmd = parser.parse(options, args);

        ChartExec chartExec = new ChartExec();

        hdfsDist = new HDFSDistributor();


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
            System.err.println("${symbol_escape}nERROR: you must state option -i with an input file${symbol_escape}n");
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

        if (cmd.hasOption('m')) {
            String stringValue = cmd.getOptionValue('m');
            if (StringUtils.isNotEmpty(stringValue)) {
                chartExec.setBootstrapMin(Integer.parseInt(stringValue));
            } else {
                System.err.println("Unparsable numeric value for option 'm':" + stringValue);
            }
        }

        long maxLines = 0;
        if (cmd.hasOption('s')) {
            String stringValue = cmd.getOptionValue('s');
            maxLines = Long.valueOf(stringValue);

            if (StringUtils.isNotEmpty(stringValue)) {
                if (hdfsDist != null) {
                    hdfsDist.setMaxNumberOfLines(maxLines);
                }
            } else {
                System.err.println("Unparsable numeric value for option 's':" + stringValue);
            }
        } else if (!cmd.hasOption('s')) {
            maxLines = 10000;

            if (hdfsDist != null) {
                hdfsDist.setMaxNumberOfLines(maxLines);
            }
        }

        LineCountManager jetty = new LineCountManager(maxLines, 500);
        jetty.prepareServer();
        jetty.prepareStatus();
        hdfsDist = hdfsDist.setFileRoot("brownbag_demo").setReportingHost(jetty.getHostName()
                + ":" + jetty.getListeningPort());

        return chartExec;
    }

    /**
     * Entry point for this example
     * Uses HDFS ToolRunner to wrap processing of
     *
     * @param args Command-line arguments for HDFS example
     */
    public static void main(String[] args) {
        CmdLine cmd = new CmdLine();
        Configuration conf = new Configuration();
        int res = 0;
        try {
            res = ToolRunner.run(conf, cmd, args);
        } catch (Exception e) {
            System.err.println("Error while running MR job");
            e.printStackTrace();
        }
        System.exit(res);
    }

    @Override
    public int run(final String[] args) throws ModelException, SCXMLExpressionException, SAXException, IOException, ParseException {
        Logger.getLogger("org.apache").setLevel(Level.WARN);

        ChartExec chartExec = parseCommandLine(args);

        hdfsDist.setConfiguration(this.configuration);
        hdfsDist.setOutputFileDir("dg-result");
        chartExec.process(hdfsDist);

        return 0;
    }

    @Override
    public void setConf(final Configuration c) {
        this.configuration = c;
    }

    @Override
    public Configuration getConf() {
        return configuration;
    }

}
