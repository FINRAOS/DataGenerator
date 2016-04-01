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
public class PositiveBoundHiveBigIntTest {

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag pos = new PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag();

        pos.setName("hive_big_test");
        Assert.assertEquals(pos.getName(), "hive_big_test");

        pos.setName("hive_big_test2");
        Assert.assertEquals(pos.getName(), "hive_big_test2");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag pos = new PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag();

        pos.setNullable("true");
        Assert.assertEquals(pos.getNullable(), "true");

        pos.setNullable("false");
        Assert.assertEquals(pos.getNullable(), "false");
    }

    /**
     * check to make sure null is only added when nullable is set to true
     */
    @Test
    public void nullTest2() {
        Map<String, String> variableDomains = new HashMap<>();
        PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag pos = new PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag();
        PositiveBoundHiveBigInt test = new PositiveBoundHiveBigInt();
        List<Map<String, String>> listOfMaps = new LinkedList<>();

        variableDomains.put("test", "");
        listOfMaps.add(variableDomains);

        pos.setMin("10");
        pos.setMax("100");
        pos.setName("test");

        pos.setNullable("true");
        List<Map<String, String>> list = test.pipelinePossibleStates(pos, listOfMaps);
        Assert.assertEquals(list.size(), 6);

        pos.setNullable("false");
        list = test.pipelinePossibleStates(pos, listOfMaps);
        Assert.assertEquals(list.size(), 5);
    }

    /**
     * test for setMin() and getMin()
     */
    @Test
    public void minTest() {
        PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag pos = new PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag();

        pos.setMin("10");
        Assert.assertEquals(pos.getMin(), "10");

        pos.setMin("100");
        Assert.assertEquals(pos.getMin(), "100");
    }

    /**
     * check to make sure min boundary conditions are being added to states list
     */
    @Test
    public void minTest2() {
        Map<String, String> variableDomains = new HashMap<>();
        PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag pos = new PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag();
        PositiveBoundHiveBigInt test = new PositiveBoundHiveBigInt();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        ArrayList<String> al = new ArrayList<>();

        variableDomains.put("test", "");
        listOfMaps.add(variableDomains);

        pos.setMin("10");
        pos.setMax("100");
        pos.setNullable("true");
        pos.setName("test");

        List<Map<String, String>> list = test.pipelinePossibleStates(pos, listOfMaps);
        for (Map<String, String> map : list) {
            for (String key : map.keySet()) {
                al.add(map.get(key));
            }
        }

        Assert.assertTrue(al.contains("10"));
        Assert.assertTrue(al.contains("11"));
    }

    /**
     * check to make sure min boundary conditions are being added to states list
     */
    @Test
    public void minTest3() {
        Map<String, String> variableDomains = new HashMap<>();
        PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag pos = new PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag();
        PositiveBoundHiveBigInt test = new PositiveBoundHiveBigInt();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        ArrayList<String> al = new ArrayList<>();

        variableDomains.put("test", "");
        listOfMaps.add(variableDomains);

        pos.setMin("1232132132234234234234234231321321213");
        pos.setMax("-9994234234234234234234234223423");
        pos.setNullable("true");
        pos.setName("test");

        List<Map<String, String>> list = test.pipelinePossibleStates(pos, listOfMaps);
        for (Map<String, String> map : list) {
            for (String key : map.keySet()) {
                al.add(map.get(key));
            }
        }

        Assert.assertTrue(al.contains(Long.toString(Long.MIN_VALUE)));
        Assert.assertTrue(al.contains(Long.toString(Long.MIN_VALUE + 1)));
    }

    /**
     * test for setMax() and getMax()
     */
    @Test
    public void maxTest() {
        PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag pos = new PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag();

        pos.setMax("10");
        Assert.assertEquals(pos.getMax(), "10");

        pos.setMax("100");
        Assert.assertEquals(pos.getMax(), "100");
    }

    /**
     * check to make sure max boundary conditions are being added to states list
     */
    @Test
    public void maxTest2() {
        Map<String, String> variableDomains = new HashMap<>();
        PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag pos = new PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag();
        PositiveBoundHiveBigInt test = new PositiveBoundHiveBigInt();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        ArrayList<String> al = new ArrayList<>();

        variableDomains.put("test", "");
        listOfMaps.add(variableDomains);

        pos.setMin("10");
        pos.setMax("100");
        pos.setNullable("true");
        pos.setName("test");

        List<Map<String, String>> list = test.pipelinePossibleStates(pos, listOfMaps);
        for (Map<String, String> map : list) {
            for (String key : map.keySet()) {
                al.add(map.get(key));
            }
        }
        Assert.assertTrue(al.contains("100"));
        Assert.assertTrue(al.contains("99"));
    }

    /**
     * check to make sure zero is added to states when crossing the threshold
     */
    @Test
    public void zeroTest() {
        Map<String, String> variableDomains = new HashMap<>();
        PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag pos = new PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag();
        PositiveBoundHiveBigInt test = new PositiveBoundHiveBigInt();
        List<Map<String, String>> listOfMaps = new LinkedList<>();

        variableDomains.put("test", "");
        listOfMaps.add(variableDomains);

        pos.setMin("-1");
        pos.setMax("100");
        pos.setName("test");

        pos.setNullable("true");
        List<Map<String, String>> list = test.pipelinePossibleStates(pos, listOfMaps);
        Assert.assertEquals(list.size(), 7);

        pos.setNullable("false");
        list = test.pipelinePossibleStates(pos, listOfMaps);
        Assert.assertEquals(list.size(), 6);
    }
}
