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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.consumer.DataTransformer;
import org.finra.datagenerator.consumer.EquivalenceClassTransformer;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.engine.Engine;
import org.finra.datagenerator.engine.scxml.SCXMLEngine;
import org.finra.datagenerator.engine.scxml.tags.CustomTagExtension;
import org.finra.datagenerator.engine.scxml.tags.InLineTransformerExtension;
import org.finra.datagenerator.samples.transformer.SampleMachineTransformer;
import org.finra.datagenerator.writer.BulkWriter;
import org.finra.datagenerator.writer.JsonWriter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Driver for a simple Data Generator example using the Default Distributor and a single transformer.
 */
public final class CmdLineBulkWriter {

    private CmdLineBulkWriter() {
        // Do nothing
    }

    private static final Logger log = Logger.getLogger(CmdLineBulkWriter.class);

    /**
     * Entry point for the example.
     *
     * @param args Command-line arguments for the example. To use samplemachine.xml from resources, send
     *             no arguments. To use other file, send a filename without xml extension).
     */

    public static void main(String[] args) {
        //Adding custom equivalence class generation transformer - NOTE this will get applied during graph traversal-->
        //MODEL USAGE EXAMPLE: <assign name="var_out_V1_2" expr="%ssn"/> <dg:transform name="EQ"/>
        Map<String, DataTransformer> transformers = new HashMap<String, DataTransformer>();
        transformers.put("EQ", new EquivalenceClassTransformer());
        Vector<CustomTagExtension> cte = new Vector<CustomTagExtension>();
        cte.add(new InLineTransformerExtension(transformers));
        Engine engine = new SCXMLEngine(cte);

        //will default to samplemachine, but you could specify a different file if you choose to
        InputStream is = CmdLineBulkWriter.class.getResourceAsStream("/" + (args.length == 0 ? "samplemachine" : args[0]) + ".xml");

        engine.setModelByInputFileStream(is);

        // Usually, this should be more than the number of threads you intend to run
        engine.setBootstrapMin(1);

        //Prepare the consumer with the proper writer and transformer
        DataConsumer consumer = new DataConsumer();
        consumer.addDataTransformer(new SampleMachineTransformer());

        //Adding custom equivalence class generation transformer - NOTE this will get applied post data generation.
        //MODEL USAGE EXAMPLE: <dg:assign name="var_out_V2" set="%regex([0-9]{3}[A-Z0-9]{5})"/>
        consumer.addDataTransformer(new EquivalenceClassTransformer());


        BulkWriter jsonWriter = new JsonWriter(System.out,
                new String[]{"var_1_1", "var_1_2", "var_1_3", "var_1_4", "var_1_5", "var_1_6",
                        "var_2_1", "var_2_2", "var_2_3", "var_2_4", "var_2_5", "var_2_6"});

        consumer.addDataWriter(jsonWriter);

        //Prepare the distributor
        DefaultDistributor defaultDistributor = new DefaultDistributor();
        defaultDistributor.setThreadCount(1);
        defaultDistributor.setDataConsumer(consumer);
        Logger.getLogger("org.apache").setLevel(Level.WARN);

        engine.process(defaultDistributor);

        //Tells writer, it's finished processing the data
        jsonWriter.finish();

    }
}
