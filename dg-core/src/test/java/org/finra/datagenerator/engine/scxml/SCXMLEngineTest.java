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

package org.finra.datagenerator.engine.scxml;

import org.apache.commons.scxml.model.ModelException;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.consumer.DataTransformer;
import org.finra.datagenerator.consumer.EquivalenceClassTransformer;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.engine.Engine;
import org.finra.datagenerator.engine.scxml.tags.CustomTagExtension;
import org.finra.datagenerator.engine.scxml.tags.InLineTransformerExtension;
import org.finra.datagenerator.writer.DefaultWriter;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Marshall Peters
 * Date: 9/3/14
 */
public class SCXMLEngineTest {

    /**
     * Multiple variable assignments using set:{}
     */
    @Test
    public void testMultiVariableAssignment() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
        e.setModelByInputFileStream(is);

        try {
            List<PossibleState> bfs = e.bfs(100);
            Assert.assertEquals(343, bfs.size());  //7^3, produced by expanding BULK_ASSIGN

            for (PossibleState p: bfs) {
                Assert.assertEquals(p.nextState.getId(), "ASSIGN_WITH_CONDITIONS");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_4"), "Lorem");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_5"), "Ipsum");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_6"), "Doler");
            }
        } catch (ModelException ex) {
            Assert.fail();
        }
    }


    /**
     *
     */
    @Test
    public void testEquivalanceClassTransformerInline() {
        try {
            //Adding custom equivalence class generation transformer - NOTE this will get applied during graph traversal
            Map<String, DataTransformer> transformers = new HashMap<String, DataTransformer>();
            transformers.put("EQ", new EquivalenceClassTransformer());
            Vector<CustomTagExtension> customtags = new Vector<CustomTagExtension>();
            customtags.add(new InLineTransformerExtension(transformers));
            Engine engine = new SCXMLEngine(customtags);

            // Model File
            InputStream is = SCXMLEngineTest.class.getResourceAsStream("/eqtransformerinline.xml");
            engine.setModelByInputFileStream(is);

            // Usually, this should be more than the number of threads you intend to run
            engine.setBootstrapMin(1);

            //Prepare the consumer with the proper writer and transformer
            DataConsumer consumer = new DataConsumer();

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            consumer.addDataWriter(new DefaultWriter(output,
                    new String[]{"V1", "V2", "V3"}));

            //Prepare the distributor
            DefaultDistributor defaultDistributor = new DefaultDistributor();
            defaultDistributor.setThreadCount(1);
            defaultDistributor.setDataConsumer(consumer);

            engine.process(defaultDistributor);

            //System.out.println("OUTPUT testEquivalanceClassTransformerInline:\n" + output.toString());
            StringTokenizer token = new StringTokenizer(output.toString(), "|");

            String s1 = token.nextToken();
            String s2 = token.nextToken();
            String s3 = token.nextToken().replaceAll("\n", "");

            Assert.assertTrue(s1 + " VALUE NOT Between 0-9", ((Integer.parseInt(s1) <= 9) && (Integer.parseInt(s1) >= 0)));
            Assert.assertTrue(s2 + " VALUE NOT Between 10000-99999", ((Integer.parseInt(s2) <= 99999) && (Integer.parseInt(s2) >= 10000)));
            Assert.assertTrue("VALUE IS NOT of length 100 but rather " + s3.length(), (s3.length() == 100));


        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }



    /**
     *
     */
    @Test
    public void testEquivalanceClassTransformerPost() {
        try {

            Engine engine = new SCXMLEngine();

            // Model File
            InputStream is = SCXMLEngineTest.class.getResourceAsStream("/eqtransformerpost.xml");
            engine.setModelByInputFileStream(is);

            // Usually, this should be more than the number of threads you intend to run
            engine.setBootstrapMin(1);

            //Prepare the consumer with the proper writer and transformer
            DataConsumer consumer = new DataConsumer();

            //Adding custom equivalence class generation transformer - NOTE this will get applied post data generation.
            consumer.addDataTransformer(new EquivalenceClassTransformer());

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            consumer.addDataWriter(new DefaultWriter(output,
                    new String[]{"V1", "V2", "V3", "V4"}));

            //Prepare the distributor
            DefaultDistributor defaultDistributor = new DefaultDistributor();
            defaultDistributor.setThreadCount(1);
            defaultDistributor.setDataConsumer(consumer);

            engine.process(defaultDistributor);

            //System.out.println("OUTPUT testEquivalanceClassTransformerPost:\n" + output.toString());


            StringTokenizer token = new StringTokenizer(output.toString(), "\n");

            String str;
            while (token.hasMoreTokens()) {
                str = token.nextToken();
                StringTokenizer token2 = new StringTokenizer(str, "|");
                String s1 = token2.nextToken();
                String s2 = token2.nextToken();
                String s3 = token2.nextToken();
                String s4 = token2.nextToken().replaceAll("\n", "");

                Assert.assertTrue(s1 + " VALUE NOT an integer between 0-999 ", ((Integer.parseInt(s1) <= 999) && (Integer.parseInt(s1) >= 0)));
                Assert.assertTrue(s2 + " VALUE NOT of length 8", s2.length() == 8);
                Assert.assertTrue(s2 + " VALUE NOT of syntax [0-9]{3}[A-Z0-9]{5}", (s2.replaceAll("[0-9]{3}[A-Z0-9]{5}", "")).equals(""));
                Assert.assertTrue(s3 + " VALUE NOT of syntax ddd-dd-dddd", (s3.replaceAll("[0-9]{3}\\-[0-9]{2}\\-[0-9]{4}", "")).equals(""));
                Assert.assertTrue(s4 + " VALUE IS NOT of length 3 but rather " + s4.length(), (s4.length() == 3));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * All variables have a default assignment of ""
     */
    @Test
    public void testInitiallyEmptyAssignment() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
        e.setModelByInputFileStream(is);

        try {
            List<PossibleState> bfs = e.bfs(1);
            Assert.assertEquals(1, bfs.size());

            PossibleState p = bfs.get(0);
            Assert.assertEquals(p.nextState.getId(), "start");

            Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE"), "");
            Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_2"), "");
            Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_3"), "");
            Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_4"), "");
            Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_5"), "");
            Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_6"), "");
            Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_7"), "");
            Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_8"), "");
            Assert.assertEquals(p.variables.keySet().size(), 8);
        } catch (ModelException ex) {
            Assert.fail();
        }
    }

    /**
     * Throws exception when reaching end state
     */
    @Test
    public void testExceptionAtEndState() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
        e.setModelByInputFileStream(is);

        try {
            List<PossibleState> bfs = e.bfs(4000); //too many
            Assert.fail();
        } catch (ModelException ex) {
            Assert.assertEquals(ex.getMessage(), "Could not achieve required bootstrap without reaching end state");
        }
    }

}
