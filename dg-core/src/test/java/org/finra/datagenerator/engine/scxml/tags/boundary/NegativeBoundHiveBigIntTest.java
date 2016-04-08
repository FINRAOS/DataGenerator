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
public class NegativeBoundHiveBigIntTest {

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag neg = new NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag();

        neg.setName("hive_big_test");
        Assert.assertEquals(neg.getName(), "hive_big_test");

        neg.setName("hive_big_test2");
        Assert.assertEquals(neg.getName(), "hive_big_test2");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag neg = new NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag();

        neg.setNullable("true");
        Assert.assertEquals(neg.getNullable(), "true");

        neg.setNullable("false");
        Assert.assertEquals(neg.getNullable(), "false");
    }

    /**
     * check to make sure null is only added when nullable is set to false
     */
    @Test
    public void nullTest2() {
        Map<String, String> variableDomains = new HashMap<>();
        NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag neg = new NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag();
        NegativeBoundHiveBigInt test = new NegativeBoundHiveBigInt();
        List<Map<String, String>> listOfMaps = new LinkedList<>();

        variableDomains.put("test", "");
        listOfMaps.add(variableDomains);

        neg.setMin("10");
        neg.setMax("100");
        neg.setNullable("true");
        neg.setName("test");

        List<Map<String, String>> list = test.pipelinePossibleStates(neg, listOfMaps);
        Assert.assertEquals(list.size(), 2);

        neg.setNullable("false");

        list = test.pipelinePossibleStates(neg, listOfMaps);
        Assert.assertEquals(list.size(), 3);
    }

    /**
     * test for setMin() and getMin()
     */
    @Test
    public void minTest() {
        NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag neg = new NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag();

        neg.setMin("10");
        Assert.assertEquals(neg.getMin(), "10");

        neg.setMin("100");
        Assert.assertEquals(neg.getMin(), "100");
    }

    /**
     * check to make sure min boundary conditions are being added to states list
     */
    @Test
    public void minTest2() {
        Map<String, String> variableDomains = new HashMap<>();
        NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag neg = new NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag();
        NegativeBoundHiveBigInt test = new NegativeBoundHiveBigInt();
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

        Assert.assertTrue(al.contains("9"));
        Assert.assertTrue(al.contains("101"));
    }

    /**
     * test for setMax() and getMax()
     */
    @Test
    public void maxTest() {
        NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag neg = new NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag();

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
        NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag neg = new NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag();
        NegativeBoundHiveBigInt test = new NegativeBoundHiveBigInt();
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

        Assert.assertTrue(al.contains("9"));
        Assert.assertTrue(al.contains("101"));
    }
}
