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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Nathaniel Lee
 * Date: 10/27/15
 */
public class PositiveBoundHiveSmallIntTest {

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag pos = new PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag();

        pos.setName("hive_small_test");
        Assert.assertEquals(pos.getName(), "hive_small_test");

        pos.setName("hive_small_test2");
        Assert.assertEquals(pos.getName(), "hive_small_test2");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag pos = new PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag();

        pos.setNullable("true");
        Assert.assertEquals(pos.getNullable(), "true");

        pos.setNullable("false");
        Assert.assertEquals(pos.getNullable(), "false");
    }

    /**
     * test for setMin() and getMin()
     */
    @Test
    public void minTest() {
        PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag pos = new PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag();

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
        PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag pos = new PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag();
        PositiveBoundHiveSmallInt test = new PositiveBoundHiveSmallInt();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        pos.setMin("0");
        pos.setMax("100");
        pos.setNullable("true");
        pos.setName("test");

        List<Map<String, String>> list = test.pipelinePossibleStates(pos, listOfMaps);

        Assert.assertEquals(list.get(0).get("test"), "0");
        Assert.assertEquals(list.get(1).get("test"), "1");
        Assert.assertEquals(list.get(2).get("test"), "50");
        Assert.assertEquals(list.get(3).get("test"), "99");
        Assert.assertEquals(list.get(4).get("test"), "100");
    }

    /**
     * check to make sure min boundary conditions are being added to states list
     */
    @Test
    public void minTest3() {
        Map<String, String> variableDomains = new HashMap<>();
        PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag pos = new PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag();
        PositiveBoundHiveSmallInt test = new PositiveBoundHiveSmallInt();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        pos.setMin("-1300");
        pos.setMax("100");
        pos.setNullable("true");
        pos.setName("test");

        List<Map<String, String>> list = test.pipelinePossibleStates(pos, listOfMaps);

        Assert.assertEquals(list.get(0).get("test"), "-1300");
        Assert.assertEquals(list.get(1).get("test"), "-1299");
        Assert.assertEquals(list.get(2).get("test"), "700");
        Assert.assertEquals(list.get(3).get("test"), "99");
        Assert.assertEquals(list.get(4).get("test"), "100");
    }

    /**
     * test for setMax() and getMax()
     */
    @Test
    public void maxTest() {
        PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag pos = new PositiveBoundHiveSmallInt.PositiveBoundHiveSmallIntTag();

        pos.setMax("10");
        Assert.assertEquals(pos.getMax(), "10");

        pos.setMax("100");
        Assert.assertEquals(pos.getMax(), "100");
    }
}
