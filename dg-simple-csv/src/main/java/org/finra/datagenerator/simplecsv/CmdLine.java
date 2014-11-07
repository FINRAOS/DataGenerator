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
package org.finra.datagenerator.simplecsv;

import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.engine.scxml.SCXMLEngine;
import org.finra.datagenerator.simplecsv.writer.CSVFileWriter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Marshall Peters
 * Date: 10/2/14
 */
public final class CmdLine {

    private CmdLine() {
        // Do nothing
    }

    /**
     * Main method, handles all the setup tasks for DataGenerator a user would normally do themselves
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        String modelFile = "";
        String outputFile = "";
        int numberOfRows = 0;
        try {
            modelFile = args[0];
            outputFile = args[1];
            numberOfRows = Integer.valueOf(args[2]);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("ERROR! Invalid command line arguments, expecting: <scxml model file> "
                    + "<desired csv output file> <desired number of output rows>");
            return;
        }

        FileInputStream model = null;
        try {
            model = new FileInputStream(modelFile);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR! Model file not found");
            return;
        }

        SCXMLEngine engine = new SCXMLEngine();
        engine.setModelByInputFileStream(model);
        engine.setBootstrapMin(5);

        DataConsumer consumer = new DataConsumer();
        CSVFileWriter writer;
        try {
            writer = new CSVFileWriter(outputFile);
        } catch (IOException e) {
            System.out.println("ERROR! Can not write to output csv file");
            return;
        }
        consumer.addDataWriter(writer);

        DefaultDistributor dist = new DefaultDistributor();
        dist.setThreadCount(5);
        dist.setMaxNumberOfLines(numberOfRows);
        dist.setDataConsumer(consumer);

        engine.process(dist);
        writer.closeCSVFile();

        System.out.println("COMPLETE!");
    }

}
