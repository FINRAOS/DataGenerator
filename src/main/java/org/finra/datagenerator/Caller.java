/*
 * (C) Copyright 2013 DataGenerator Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.finra.datagenerator;

import com.google.common.base.Stopwatch;
import java.io.File;
import java.util.Scanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.finra.datagenerator.output.TemplatingProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Entry point class.
 *
 * @author ChamberA
 *
 *
 * -- Equivalence class based -- -dataspec "C:\Users\MeleS\Desktop\Data Generator\input\spec.xls" -templates
 * "C:\Users\MeleS\Desktop\Data Generator\input\template_PW.txt" -config "C:\Users\MeleS\Desktop\Data
 * Generator\input\dd.properties" -out "C:\Users\MeleS\Desktop\Data Generator\output" -pairwise -f -isoNeg
 *
 * Purcahse and Sales -dataspec "C:\Users\MeleS\Desktop\Data Generator\input\Purchases and Sales\purchase_sales.xls"
 * -templates "C:\Users\MeleS\Desktop\Data Generator\input\purchases and sales\Purch_Sales_Template.txt" -config
 * "C:\Users\MeleS\Desktop\Data Generator\input\dd.properties" -out "C:\Users\MeleS\Desktop\Data
 * Generator\output\Purchases and Sales" -pairwise -f -isoNeg
 *
 * -dataspec "C:\Users\MeleS\Desktop\Data Generator\input\purchase_sales.xls" -templates "C:\Users\MeleS\Desktop\Data
 * Generator\input\Purch_Sales_Template.txt" -config "C:\Users\MeleS\Desktop\Data Generator\input\dd.properties" -out
 * "C:\Users\MeleS\Desktop\Data Generator\output" -pairwise -f -isoNeg
 *
 * -- Branch coverage bases -- -dataspec "C:\RC\RCVIZ_EQ\DG\input\spec.xls" -templates
 * "C:\RC\RCVIZ_EQ\DG\input\template_BC.txt" -config "C:\RC\RCVIZ_EQ\DG\input\dd.properties" -out
 * "C:\RC\RCVIZ_EQ\DG\out" -branchgraph "C:\RC\RCVIZ_EQ\DG\input\spec.vdx" -allPaths -f
 *
 */
public class Caller {

    private static Logger log = Logger.getLogger(Caller.class);

    public static void main(String[] args) {

        Stopwatch stopwatch = new Stopwatch().start();

        // create Spring app context
        ApplicationContext AppContext = new ClassPathXmlApplicationContext("SpringConfig.xml");

        //parse command line args
        Options options = new Options();

        options.addOption("dataspec", true, "Directory or file containing dataspec");
        options.getOption("dataspec").setRequired(true);
        options.getOption("dataspec").setArgName("path");

        options.addOption("branchgraph", true, "Directory or file containing branchgraph");
        options.getOption("branchgraph").setArgName("path");

        options.addOption("templates", true, "Directory or file containing templates");
        options.getOption("templates").setRequired(true);
        options.getOption("templates").setArgName("path");

        options.addOption("config", true, "Properties file for templating");
        options.getOption("config").setArgName("path");

        options.addOption("out", true, "Output directory where templated output will be written");
        options.getOption("out").setRequired(true);
        options.getOption("out").setArgName("path");

        options.addOption("f", false, "Clean output directory without prompt if pre-existing.");

        options.addOption("pairwise", false, "Generates datasets that cover all pairwise combinations of positive values. Ignores groups.");

        options.addOption("allCombos", false, "Generates datasets that cover all possible combinations of positive values. Careful, this could be a lot.");

        options.addOption("isoNeg", false, "Generates datasets that cover each negative value in isolation. ");

        options.addOption("isoPos", false, "Generates datasets that cover each positive value in isolation. ");

        options.addOption("allPaths", false, "Generates datasets that cover all possible paths through a branch diagram.");

        options.addOption("allEdges", false, "Generates datasets that cover all edges of a branch diagram. A subset of allPaths.");

        options.addOption("help", false, "Print this message");

        final String USAGE = "-dataspec <path> [-branchgraph <path>] -templates <path> [-config <path>] -out <path> [options]";

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmdLine = parser.parse(options, args);

            if (cmdLine.hasOption("help")) {
                new HelpFormatter().printHelp(USAGE, options);
                System.exit(0);
            }

            if (!cmdLine.hasOption("dataspec")||!cmdLine.hasOption("out")) {
                new HelpFormatter().printHelp(USAGE, options);
                System.exit(1);
            }

            File outputDir = new File(cmdLine.getOptionValue("out"));

            // make sure the user really intends to overwrite output dir
            if (outputDir.exists()) {

                if (cmdLine.hasOption("f")) {
                    FileUtils.deleteDirectory(outputDir);
                } else {
                    log.warn("Output dir "+outputDir.getPath()+" already exists. Overwrite? (y/n)");
                    Scanner scanner = new Scanner(System.in);
                    String response = scanner.next();
                    while (!response.equalsIgnoreCase("y")&&!response.equalsIgnoreCase("n")){
                        response = scanner.next();
                    }
                    if (response.equalsIgnoreCase("y")) {
                        FileUtils.deleteDirectory(outputDir);
                    } else {
                        log.info("Exiting");
                        System.exit(0);
                    }
                }
            }

            if (!outputDir.mkdir()) {
                log.error("Error creating output dir"+outputDir.getPath());
                System.exit(1);
            }

            if (cmdLine.hasOption("config")) {
                TemplatingProperties.loadProperties(new File(cmdLine.getOptionValue("config")));
            }

            boolean pairwiseFlag = cmdLine.hasOption("pairwise");
            boolean allCombosFlag = cmdLine.hasOption("allCombos");
            boolean isoNegFlag = cmdLine.hasOption("isoNeg");
            boolean isoPosFlag = cmdLine.hasOption("isoPos");
            boolean allPathsFlag = cmdLine.hasOption("allPaths");
            boolean allEdgesFlag = cmdLine.hasOption("allEdges");

            //create the executor and tell it to do stuff
            Executor exec = (Executor) AppContext.getBean("executor");

            // input reading
            exec.readDataSpec(cmdLine.getOptionValue("dataspec"));

            exec.readTemplateInput(cmdLine.getOptionValue("templates"));

            if (allPathsFlag||allEdgesFlag) {
                exec.readBranchGraph(cmdLine.getOptionValue("branchgraph"));
            }

            // dataset generation
            exec.generateDefaultCombiDataset();

            if (isoPosFlag) {
                exec.generateIsoPosCombiDataSets();
            }

            if (isoNegFlag) {
                exec.generateIsoNegCombiDataSets();
            }

            if (pairwiseFlag) {
                exec.generatePairwiseCombiDataSets();
            }

            if (allCombosFlag) {
                exec.generateAllCombosDataSets();
            }

            if (allPathsFlag) {
                exec.generateAllPathsDataSets();
            }

            if (allEdgesFlag) {
                exec.generateAllEdgesDataSets();
            }

            log.info("Done with generation. Writing output ...");

            // output writing
            exec.writeOutput(outputDir);

            exec.shutdownTaskExecutor();

        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        log.info("Complete!");
        log.info("---");
        log.info("Time Taken: "+stopwatch);
    }

}
