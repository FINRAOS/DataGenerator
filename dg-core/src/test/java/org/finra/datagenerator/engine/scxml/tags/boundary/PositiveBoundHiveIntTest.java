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
public class PositiveBoundHiveIntTest {

    /**
     * test for setMax() and getMax()
     */
    @Test
    public void maxTest() {
        PositiveBoundHiveInt.PositiveBoundHiveIntTag pos = new PositiveBoundHiveInt.PositiveBoundHiveIntTag();

        pos.setMax("10");
        Assert.assertEquals(pos.getMax(), "10");

        pos.setMax("100");
        Assert.assertEquals(pos.getMax(), "100");
    }

    /**
     * test for setMin() and getMin()
     */
    @Test
    public void minTest() {
        PositiveBoundHiveInt.PositiveBoundHiveIntTag pos = new PositiveBoundHiveInt.PositiveBoundHiveIntTag();

        pos.setMin("10");
        Assert.assertEquals(pos.getMin(), "10");

        pos.setMin("100");
        Assert.assertEquals(pos.getMin(), "100");
    }

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        PositiveBoundHiveInt.PositiveBoundHiveIntTag pos = new PositiveBoundHiveInt.PositiveBoundHiveIntTag();

        pos.setName("hive_int_test");
        Assert.assertEquals(pos.getName(), "hive_int_test");

        pos.setName("hive_int_test2");
        Assert.assertEquals(pos.getName(), "hive_int_test2");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        PositiveBoundHiveInt.PositiveBoundHiveIntTag pos = new PositiveBoundHiveInt.PositiveBoundHiveIntTag();

        pos.setNullable("true");
        Assert.assertEquals(pos.getNullable(), "true");

        pos.setNullable("false");
        Assert.assertEquals(pos.getNullable(), "false");
    }

    /**
     * check to make sure null is only added when nullable=true
     */
    @Test
    public void nullTest2() {
        Map<String, String> variableDomains = new HashMap<>();
        PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag pos = new PositiveBoundHiveBigInt.PositiveBoundHiveBigIntTag();
        PositiveBoundHiveBigInt test = new PositiveBoundHiveBigInt();
        List<Map<String, String>> listOfMaps = new LinkedList<>();

        variableDomains.put("test", "");
        listOfMaps.add(variableDomains);

        pos.setMin("20");
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
