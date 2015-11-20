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
package org.finra.datagenerator.engine.scxml.tags.boundary;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Nathaniel Lee
 * Date: 10/27/15
 */
public class NegativeBoundHiveTinyIntTest {

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger neg = new NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger();

        neg.setName("hive_tiny_test");
        Assert.assertEquals(neg.getName(), "hive_tiny_test");

        neg.setName("hive_tiny_test2");
        Assert.assertEquals(neg.getName(), "hive_tiny_test2");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger neg = new NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger();

        neg.setNullable("true");
        Assert.assertEquals(neg.getNullable(), "true");

        neg.setNullable("false");
        Assert.assertEquals(neg.getNullable(), "false");
    }

    /**
     * test for setMin() and getMin()
     */
    @Test
    public void minTest() {
        NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger neg = new NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger();

        neg.setMin("10");
        Assert.assertEquals(neg.getMin(), "10");

        neg.setMin("100");
        Assert.assertEquals(neg.getMin(), "100");
    }

    /**
     * test for setMax() and getMax()
     */
    @Test
    public void maxTest() {
        NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger neg = new NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger();

        neg.setMax("10");
        Assert.assertEquals(neg.getMax(), "10");

        neg.setMax("100");
        Assert.assertEquals(neg.getMax(), "100");
    }

    /**
     * check to make sure max boundary conditions are being added to states list
     */
    @Test
    public void maxTest2() {
        Map<String, String> variableDomains = new HashMap<>();
        NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger neg = new NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger();
        NegativeBoundHiveTinyInt test = new NegativeBoundHiveTinyInt();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        ArrayList<String> al = new ArrayList<>();

        variableDomains.put("test", "");
        listOfMaps.add(variableDomains);

        neg.setMin("10");
        neg.setMax("100");
        neg.setNullable("true");
        neg.setName("test");

        List<Map<String, String>> list = test.pipelinePossibleStates(neg, listOfMaps);
        for (Map<String, String> map : list) {
            for (String key : map.keySet()) {
                al.add(map.get(key));
            }
        }

        Assert.assertTrue(al.contains("100"));
        Assert.assertTrue(al.contains("101"));
    }

    /**
     * check to make sure default values are populated for max outside of range
     */
    @Test
    public void maxTest3() {
        Map<String, String> variableDomains = new HashMap<>();
        NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger neg = new NegativeBoundHiveTinyInt.NegativeBoundHiveTinyIntTagInteger();
        NegativeBoundHiveTinyInt test = new NegativeBoundHiveTinyInt();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        ArrayList<String> al = new ArrayList<>();

        variableDomains.put("test", "");
        listOfMaps.add(variableDomains);

        neg.setMin("0");
        neg.setMax("12342");
        neg.setNullable("true");
        neg.setName("test");

        List<Map<String, String>> list = test.pipelinePossibleStates(neg, listOfMaps);
        for (Map<String, String> map : list) {
            for (String key : map.keySet()) {
                al.add(map.get(key));
            }
        }

        Assert.assertTrue(al.contains("128"));
        Assert.assertTrue(al.contains("127"));
    }
}
