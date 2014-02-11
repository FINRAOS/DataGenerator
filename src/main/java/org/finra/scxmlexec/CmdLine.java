/*
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
 *//*
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
package org.finra.scxmlexec;

import java.io.OutputStream;
import java.util.Collection;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.scxml.model.SCXML;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.finra.datagenerator.SystemProperties;

/**
 *
 * @author mosama
 */
public class CmdLine {

    private static final Logger log = Logger.getLogger(CmdLine.class);
    private static final int BUFFER_SIZE = 1024 * 1024;
    private static SequenceFile.Writer writer = null;
    private OutputStream os = null;

    private static SequenceFile.Writer getSequenceFileWriter(String fileOutPath) throws Exception {
        Configuration configuration = new Configuration();
        Path hdfsOutPath = new Path(fileOutPath);
        FileSystem fileSystem = FileSystem.get(configuration);
        String codecClassName = System.getProperty("codecClassName", "org.apache.hadoop.io.compress.SnappyCodec");
        Class<?> codecClass = Class.forName(codecClassName);
        CompressionCodec compressionCodec = (CompressionCodec) ReflectionUtils.newInstance(codecClass, configuration);
        long blocksize = configuration.getLong("dfs.blocksize", 64000000);
        short replication = (short) configuration.getInt("dfs.replication", 3);
        return SequenceFile.createWriter(fileSystem,
                configuration,
                hdfsOutPath,
                LongWritable.class,
                Text.class,
                BUFFER_SIZE,
                replication,
                blocksize,
                SequenceFile.CompressionType.BLOCK,
                compressionCodec,
                null,
                new SequenceFile.Metadata());

    }

    /**
     * Prints the help on the command line
     *
     * @param options
     */
    public static void printHelp(Options options) {
        Collection<Option> c = options.getOptions();
        System.out.println("Command line options are:");
        for (Option op : c) {
            StringBuilder helpLine = new StringBuilder();
            helpLine
                    .append("\t-")
                    .append(op.getOpt())
                    .append(" --")
                    .append(op.getLongOpt())
                    .append("                 ".substring(op.getLongOpt().length()));

            int length = helpLine.length();

            System.out.print("\t-" + op.getOpt() + " --" + op.getLongOpt());
            System.out.print("                 ".substring(op.getLongOpt().length()));
            System.out.println(op.getDescription());
        }
    }

    public static ChartExec parseCommandLine(String args[]) throws ParseException, Exception {
        // create the command line parser
        CommandLineParser parser = new GnuParser();

        // create the Options
        final Options options = new Options()
                .addOption("h", "help", false, "print help.")
                .addOption("i", "inputfile", true, "the scxml input file")
                .addOption("v", "initialvariables", true,
                        "comma separated list of the initial variables and their values in the form of var1=val1,var2=val2")
                .addOption("e", "initalevents", true,
                        "a comma separated list of the initial set of events to trigger before searching for scenarios")
                .addOption("o", "stdout", false,
                        "write the output to the stdout")
                .addOption("V", "generatenegative", false,
                        "generate all negative transitions in addition to the positive ones")
                .addOption("r", "eventreps", true,
                        "the number of times a specific event is allowed to repeat in a scenario. The default is 1")
                .addOption("H", "hdfssequencefile", true,
                        "the path of the hdfs sequence file to write to")
                .addOption("L", "loglevel", true,
                        "set the log level")
                .addOption("s", "maxscenarios", true,
                        "Maximum number of scenarios to generate. Default 10,000");

        CommandLine cmd = parser.parse(options, args);

        ChartExec chartExec = new ChartExec();

        if (cmd.hasOption("h") || cmd.getOptions().length == 0) {
            printHelp(options);
        }

        if (cmd.hasOption("o")) {
            chartExec.setOs(System.out);
        }

        if (cmd.hasOption("i")) {
            chartExec.setInputFileName(cmd.getOptionValue('i'));
        }

        if (cmd.hasOption("v")) {
            chartExec.setInitialVariables(cmd.getOptionValue('v'));
        }

        if (cmd.hasOption("e")) {
            chartExec.setInitialEvents(cmd.getOptionValue('e'));
        }

        if (cmd.hasOption("H")) {
            String sequenceFile = cmd.getOptionValue('H');

            chartExec.setSequenceFileWriter(writer = getSequenceFileWriter(sequenceFile));
        }

        /*        if (cmd.hasOption('l')) {
         String stringValue = cmd.getOptionValue('l');
         if (StringUtils.isNotEmpty(stringValue)) {
         chartExec.setLengthOfScenario(Integer.valueOf(stringValue));
         } else {
         log.error("Unparsable numeric value for option 'l':" + stringValue);
         }
         }*/
        if (cmd.hasOption('L')) {
            LogInitializer.initialize(cmd.getOptionValue('L'));
        }

        if (cmd.hasOption('r')) {
            String stringValue = cmd.getOptionValue('r');
            if (StringUtils.isNotEmpty(stringValue)) {
                chartExec.setMaxEventReps(Integer.valueOf(stringValue));
            } else {
                log.error("Unparsable numeric value for option 'r':" + stringValue);
            }
        }

        if (cmd.hasOption('s')) {
            String stringValue = cmd.getOptionValue('s');
            if (StringUtils.isNotEmpty(stringValue)) {
                chartExec.setMaxScenarios(Integer.valueOf(stringValue));
            } else {
                log.error("Unparsable numeric value for option 's':" + stringValue);
            }
        }

        if (cmd.hasOption("V")) {
            chartExec.setGenerateNegativeScenarios(true);
        }

        return chartExec;
    }

    public static void main(String args[]) throws Exception {
//        File output = new File("/home/mosama/dataoutput");
//        OutputStream myos = new BufferedOutputStream(new FileOutputStream(output));
        // Load the SCXML class
        SCXML scxml = new SCXML();

        try {
            Logger.getLogger("org.apache").setLevel(Level.WARN);
            ChartExec chartExec = parseCommandLine(args);
            //chartExec.setOs(myos);
            System.err.println("Loglevel " + SystemProperties.logLevel);
            System.err.println("Loggerlevel " + log.getLevel());

            chartExec.process();
        } finally {
            //          myos.close();
        }

        if (writer != null) {
            writer.close();
        }
    }
}
