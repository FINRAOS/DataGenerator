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
package org.finra.datagenerator.samples;

import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.exec.ChartExec;
import org.finra.datagenerator.samples.transformer.SampleMachineTransformer;
import org.finra.datagenerator.writer.DefaultWriter;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Driver for a simple Data Generator example using the Default Distributor and a single transformer.
 */
public final class CmdLine {

    private CmdLine() {
        // Do nothing
    }

    private static final Logger log = Logger.getLogger(CmdLine.class);
    private static final DefaultDistributor DEFAULT_DISTRIBUTOR = new DefaultDistributor();

    /**
     * Entry point for the example.
     *
     * @param args Command-line arguments for the example. To use samplemachine.xml from resources, send
     *             no arguments. To use samplemachineN.xml, send a single argument for N (e.g., "2" for
     *             samplemachine2.xml).
     */

    public static void main(String[] args) {

        ChartExec chartExec = new ChartExec();

        //will default to samplemachine, but you could specify a different file if you choose to
        InputStream is = CmdLine.class.getResourceAsStream("/" + (args.length == 0 ? "samplemachine" : args[0]) + ".xml");

        chartExec.setInputFileStream(is);

        // Usually, this should be more than the number of threads you intend to run
        chartExec.setBootstrapMin(1);

        //Prepare the consumer with the proper writer and transformer
        DataConsumer consumer = new DataConsumer();
        consumer.addDataTransformer(new SampleMachineTransformer());
        consumer.addDataWriter(new DefaultWriter(System.out,
                new String[]{"var_out_V1_1", "var_out_V1_2", "var_out_V1_3", "var_out_V2", "var_out_V3"}));

        DEFAULT_DISTRIBUTOR.setThreadCount(1);
        DEFAULT_DISTRIBUTOR.setDataConsumer(consumer);
        Logger.getLogger("org.apache").setLevel(Level.WARN);

        try {
            chartExec.process(DEFAULT_DISTRIBUTOR);
        } catch (IOException | ModelException | SCXMLExpressionException | SAXException e) {
            log.fatal("Encountered exception while generating data", e);
        }
    }
}
