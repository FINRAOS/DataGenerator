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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.engine.Engine;
import org.finra.datagenerator.engine.scxml.SCXMLEngine;
import org.finra.datagenerator.samples.transformer.SampleMachineTransformer;
import org.finra.datagenerator.utilities.HiveDDLUtils;
import org.finra.datagenerator.writer.CustomHiveWriter;

/**
 * Driver for a simple Data Generator example using the Default Distributor and a single transformer.
 */
public final class CmdLine {

    private CmdLine() {
        // Do nothing
    }

    private static final Logger log = Logger.getLogger(CmdLine.class);

    /**
     * Entry point for the example.
     *
     * @param args Command-line arguments for the example. To use samplemachine.xml from resources, send
     *             no arguments. To use other file, send a filename without xml extension).
     */

    public static void main(String[] args) {
    	String tableName = null;
    	String outputXML = null;
    	try {
    		outputXML = HiveDDLUtils.generateOutputXML("/hiveDDLInput/SampleHiveDDLInput.txt");
    		tableName = HiveDDLUtils.getTableName();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

        Engine engine = new SCXMLEngine();

        //will default to samplemachine, but you could specify a different file if you choose to
        //InputStream is = CmdLine.class.getResourceAsStream("/" + (args.length == 0 ? "samplemachine" : args[0]) + ".xml");
        //InputStream is = CmdLine.class.getResourceAsStream("/samplemachine3.xml");
        InputStream is = new ByteArrayInputStream(outputXML.getBytes(StandardCharsets.UTF_8));

        engine.setModelByInputFileStream(is);

        // Usually, this should be more than the number of threads you intend to run
        engine.setBootstrapMin(1);

        //Prepare the consumer with the proper writer and transformer
        DataConsumer consumer = new DataConsumer();
        consumer.addDataTransformer(new SampleMachineTransformer());
        /*consumer.addDataWriter(new DefaultWriter(System.out,
                new String[]{"tinyIntC", "smallIntC", "intC", "bigIntC", "floatC", "doubleC", "decimalC", "timestampC", "dateC", "stringC", "varcharC", "charC", "booleanC"}));*/
//        consumer.addDataWriter(new CustomHiveWriter(System.out,
//                new String[]{"tinyIntC", "smallIntC", "intC", "bigIntC", "floatC", "doubleC", "decimalC", "timestampC", "dateC", "stringC", "varcharC", "charC", "booleanC"}, tableName));
        consumer.addDataWriter(new CustomHiveWriter(System.out,
              new String[]{"intC", "floatC", "timestampC", "dateC", "stringC", "varcharC"}, tableName));

        //Prepare the distributor
        DefaultDistributor defaultDistributor = new DefaultDistributor();
        defaultDistributor.setThreadCount(1);
        defaultDistributor.setDataConsumer(consumer);
        Logger.getLogger("org.apache").setLevel(Level.WARN);

        engine.process(defaultDistributor);
    }
}
