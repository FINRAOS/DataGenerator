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

package org.finra.datagenerator.distributor.spark;

import org.apache.spark.api.java.JavaRDD;
import org.finra.datagenerator.engine.scxml.SCXMLEngine;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.Map;

/**
 * Integration test for SparkDistributor
 *
 * Needs proper unit tests some day
 */
public class SparkDistributorTest {

    /**
     * Simple integration test to smoke test the SparkDistributor
     */
    @Test
    public void testSparkDistributor() {
        InputStream is = SparkDistributorTest.class.getResourceAsStream("/spark/samplemachine.xml");

        SCXMLEngine scxmlEngine = new SCXMLEngine();

        scxmlEngine.setModelByInputFileStream(is);
        scxmlEngine.setBootstrapMin(1);

        SparkDistributor sparkDistributor = new SparkDistributor("local[5]");
        scxmlEngine.process(sparkDistributor);

        JavaRDD<Map<String, String>> results = sparkDistributor.getGeneratedMaps();
        Assert.assertEquals(243, results.collect().size());
    }
}
