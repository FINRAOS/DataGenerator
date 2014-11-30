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
package org.finra.datagenerator.consumer;

import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.engine.Engine;
import org.finra.datagenerator.engine.scxml.SCXMLEngine;
import org.finra.datagenerator.engine.scxml.tags.CustomTagExtension;
import org.finra.datagenerator.engine.scxml.tags.InLineTransformerExtension;
import org.finra.datagenerator.writer.DefaultWriter;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by weerasin on 11/28/14.
 */
public class EquivalanceClassTransformerTest {
    /**
     * Tests for "regex" EquivalanceClass
     */
    @Test
    public void testRegexEquivalanceClass() {
        DataPipe thePipe = new DataPipe();
        String[] outTemplate = new String[] {"TEST"};
        EquivalenceClassTransformer eqt = new EquivalenceClassTransformer();

        // SAMPLE CUSIP
        thePipe.getDataMap().put("TEST", "%regex(^\\d{5}(?:[-\\s]\\d{4})?$)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax ^\\d{5}(?:[-\\s]\\d{4})?$",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^\\d{5}(?:[-\\s]\\d{4})?$", "")).equals(""));

        // RANDOM REGEX
        thePipe.getDataMap().put("TEST", "%regex(\\d{4}/\\d{2}/\\d{2}-\\d{2}:\\d{2})");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax \\d{4}/\\d{2}/\\d{2}-\\d{2}:\\d{2}",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("\\d{4}/\\d{2}/\\d{2}-\\d{2}:\\d{2}", "")).equals(""));

        // SAMPLE DATE
        thePipe.getDataMap().put("TEST", "%regex(^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate)
                + " VALUE NOT of syntax ^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$", ""))
                .equals(""));

        // SAMPLE EMAIL
        thePipe.getDataMap().put("TEST", "%regex(^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax ^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$", "")).equals(""));
    }

    /**
     * Tests for "alpha" EquivalanceClass
     */
    @Test
    public void testAlphaEquivalanceClass() {
        DataPipe thePipe = new DataPipe();
        String[] outTemplate = new String[] {"TEST"};
        EquivalenceClassTransformer eqt = new EquivalenceClassTransformer();

        thePipe.getDataMap().put("TEST", "%alpha(1)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %alpha(1)",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^[a-z0-9A-Z]{1}$", "")).equals(""));

        thePipe.getDataMap().put("TEST", "%alpha(100)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %alpha(100)", 
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^[a-z0-9A-Z]{100}$", "")).equals(""));

        thePipe.getDataMap().put("TEST", "%alpha(0)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %alpha(0)", 
                (thePipe.getPipeDelimited(outTemplate)).equals(""));
    }

    /**
     * Tests for "alphaWithSpaces" EquivalanceClass
     */
    @Test
    public void testAlphaWithSpacesEquivalanceClass() {
        DataPipe thePipe = new DataPipe();
        String[] outTemplate = new String[] {"TEST"};
        EquivalenceClassTransformer eqt = new EquivalenceClassTransformer();

        thePipe.getDataMap().put("TEST", "%alphaWithSpaces(1)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %alphaWithSpaces(1)",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^[a-z0-9A-Z\\s]{1}$", "")).equals(""));

        thePipe.getDataMap().put("TEST", "%alphaWithSpaces(100)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %alphaWithSpaces(100)",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^[a-z0-9A-Z\\s]{100}$", "")).equals(""));

        thePipe.getDataMap().put("TEST", "%alphaWithSpaces(0)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %alphaWithSpaces(0)",
                (thePipe.getPipeDelimited(outTemplate)).equals(""));
    }

    /**
     * Tests for "digits" EquivalanceClass
     */
    @Test
    public void testDigitsEquivalanceClass() {
        DataPipe thePipe = new DataPipe();
        String[] outTemplate = new String[] {"TEST"};
        EquivalenceClassTransformer eqt = new EquivalenceClassTransformer();

        thePipe.getDataMap().put("TEST", "%digits(5)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %digits(5)",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^[\\d]{5}$", "")).equals(""));

        thePipe.getDataMap().put("TEST", "%digits(10,10)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %digits(10,10)",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^[\\d]{10}\\.[\\d]{10}$", "")).equals(""));

        thePipe.getDataMap().put("TEST", "%digits(0,0)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %digits(0,0)",
                (thePipe.getPipeDelimited(outTemplate)).equals(""));
    }

    /**
     * Tests for "number" EquivalanceClass
     */
    @Test
    public void testNumberEquivalanceClass() {
        DataPipe thePipe = new DataPipe();
        String[] outTemplate = new String[] {"TEST"};
        EquivalenceClassTransformer eqt = new EquivalenceClassTransformer();

        thePipe.getDataMap().put("TEST", "%number(5)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %number(5)",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^[1-9]{1}[\\d]{4}$", "")).equals(""));

        thePipe.getDataMap().put("TEST", "%number(10,10)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %number(10,10)",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^[1-9]{1}[\\d]{9}\\.[\\d]{10}$", "")).equals(""));

        thePipe.getDataMap().put("TEST", "%number(0,0)");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %number(0,0)",
                (thePipe.getPipeDelimited(outTemplate)).equals(""));
    }

    /**
     * Tests for "Currency" EquivalanceClass
     */
    @Test
    public void testCurrencyEquivalanceClass() {
        DataPipe thePipe = new DataPipe();
        String[] outTemplate = new String[] {"TEST"};
        EquivalenceClassTransformer eqt = new EquivalenceClassTransformer();

        thePipe.getDataMap().put("TEST", "%currency");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %currency",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^[A-Z]{3}$", "")).equals(""));
    }

    /**
     * Tests for "SSN" EquivalanceClass
     */
    @Test
    public void testSSNEquivalanceClass() {
        DataPipe thePipe = new DataPipe();
        String[] outTemplate = new String[] {"TEST"};
        EquivalenceClassTransformer eqt = new EquivalenceClassTransformer();

        thePipe.getDataMap().put("TEST", "%ssn");
        eqt.transform(thePipe);
        Assert.assertTrue(thePipe.getPipeDelimited(outTemplate) + " VALUE NOT of syntax %ssn ^\\d{3}-\\d{2}-\\d{4}$",
                (thePipe.getPipeDelimited(outTemplate).replaceAll("^\\d{3}-\\d{2}-\\d{4}$", "")).equals(""));
    }

    /**
     * More of an code integration test rather than a unit test
     *
     * WHILE TRAVERSAL EQUIVALANCE CLASS transformation test. This will get applied during graph traversal
     * //MODEL USAGE EXAMPLE: <assign name="var_out_V1_2" expr="%ssn"/> <dg:transform name="EQ"/>
     */
    @Test
    public void testEquivalanceClassTransformerInlineIntegrated() {
        try {
            // Adding custom equivalence class generation transformer - NOTE this will get applied during graph traversal
            Map<String, DataTransformer> transformers = new HashMap<String, DataTransformer>();
            transformers.put("EQ", new EquivalenceClassTransformer());
            Vector<CustomTagExtension> customtags = new Vector<CustomTagExtension>();
            customtags.add(new InLineTransformerExtension(transformers));
            Engine engine = new SCXMLEngine(customtags);

            // Model File
            InputStream is = EquivalanceClassTransformerTest.class.getResourceAsStream("/eqtransformerinline.xml");
            engine.setModelByInputFileStream(is);

            // Usually, this should be more than the number of threads you intend to run
            engine.setBootstrapMin(1);

            // Prepare the consumer with the proper writer and transformer
            DataConsumer consumer = new DataConsumer();

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            consumer.addDataWriter(new DefaultWriter(output, new String[] {"V1", "V2", "V3"}));

            // Prepare the distributor
            DefaultDistributor defaultDistributor = new DefaultDistributor();
            defaultDistributor.setThreadCount(1);
            defaultDistributor.setDataConsumer(consumer);

            engine.process(defaultDistributor);

            // System.out.println("OUTPUT testEquivalanceClassTransformerInline:\n" + output.toString());
            StringTokenizer token = new StringTokenizer(output.toString(), "|");

            String s1 = token.nextToken();
            String s2 = token.nextToken();
            String s3 = token.nextToken().replaceAll("\n", "");

            Assert.assertTrue(s1 + " VALUE NOT Between 0-9", ((Integer.parseInt(s1) <= 9) && (Integer.parseInt(s1) >= 0)));
            Assert.assertTrue(s2 + " VALUE NOT Between 10000-99999", ((Integer.parseInt(s2) <= 99999) && (Integer.parseInt(s2) >= 10000)));
            Assert.assertTrue("VALUE IS NOT of length 100 but rather " + s3.length(), (s3.length() == 100));
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    /**
     * More of an code integration test rather than a unit test
     *
     * POST EQUIVALANCE CLASS transformation test. This will get applied post data generation.
     * MODEL USAGE EXAMPLE: <dg:assign name="var_out_V2" set="%regex([0-9]{3}[A-Z0-9]{5})"/>
     */
    @Test
    public void testEquivalanceClassTransformerPostIntegrated() {
        try {

            Engine engine = new SCXMLEngine();

            // Model File
            InputStream is = EquivalanceClassTransformerTest.class.getResourceAsStream("/eqtransformerpost.xml");
            engine.setModelByInputFileStream(is);

            // Usually, this should be more than the number of threads you intend to run
            engine.setBootstrapMin(1);

            // Prepare the consumer with the proper writer and transformer
            DataConsumer consumer = new DataConsumer();

            // Adding custom equivalence class generation transformer - NOTE this will get applied post data generation.
            consumer.addDataTransformer(new EquivalenceClassTransformer());

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            consumer.addDataWriter(new DefaultWriter(output, new String[] {"V1", "V2", "V3", "V4"}));

            // Prepare the distributor
            DefaultDistributor defaultDistributor = new DefaultDistributor();
            defaultDistributor.setThreadCount(1);
            defaultDistributor.setDataConsumer(consumer);

            engine.process(defaultDistributor);

            // System.out.println("OUTPUT testEquivalanceClassTransformerPost:\n" + output.toString());

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
            Assert.fail(ex.getMessage());
        }
    }
}
