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
public class PositiveBoundHiveVarcharTest {

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag pos = new PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag();

        pos.setName("varchar_test");
        Assert.assertEquals(pos.getName(), "varchar_test");

        pos.setName("varchar_test2");
        Assert.assertEquals(pos.getName(), "varchar_test2");
    }

    /**
     *
     */
    @Test
    public void lengthTest() {
        PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag pos = new PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag();

        pos.setLength("18");
        Assert.assertEquals(pos.getLength(), "18");

        pos.setLength("10");
        Assert.assertEquals(pos.getLength(), "10");
    }

    /**
     * test for length
     */
    @Test
    public void lengthTest2() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        PositiveBoundHiveVarchar maxLenTest = new PositiveBoundHiveVarchar();
        PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag pos = new PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag();

        pos.setName("name");
        pos.setMaxLen("10");
        Assert.assertEquals(pos.getMaxLen(), "10");

        pos.setMaxLen("12");
        Assert.assertEquals(pos.getMaxLen(), "12");

        List<Map<String, String>> newList = maxLenTest.pipelinePossibleStates(pos, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name").length(), 1);
        Assert.assertEquals(newList.get(1).get("name").length(), 10);
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag pos = new PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag();

        pos.setNullable("true");
        Assert.assertEquals(pos.getNullable(), "true");

        pos.setNullable("false");
        Assert.assertEquals(pos.getNullable(), "false");
    }

    /**
     * test for minLen
     */
    @Test
    public void minLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        PositiveBoundHiveVarchar maxLenTest = new PositiveBoundHiveVarchar();
        PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag neg = new PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag();

        neg.setName("name");
        neg.setMinLen("10");
        Assert.assertEquals(neg.getMinLen(), "10");

        List<Map<String, String>> newList = maxLenTest.pipelinePossibleStates(neg, listOfMaps);
        Assert.assertEquals(newList.get(0).get("name").length(), 10);
    }

    /**
     * test for allCaps
     */
    @Test
    public void allCapsTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        PositiveBoundHiveVarchar maxLenTest = new PositiveBoundHiveVarchar();
        PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag pos = new PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag();

        pos.setName("name");
        pos.setAllCaps("true");

        List<Map<String, String>> newList = maxLenTest.pipelinePossibleStates(pos, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name"), newList.get(0).get("name").toUpperCase());
        Assert.assertEquals(newList.get(1).get("name"), newList.get(1).get("name").toUpperCase());
        Assert.assertEquals(newList.get(3).get("name"), newList.get(3).get("name").toUpperCase());
    }

    /**
     * test for maxLen
     */
    @Test
    public void maxLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        PositiveBoundHiveVarchar maxLenTest = new PositiveBoundHiveVarchar();
        PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag pos = new PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag();
        pos.setLength("18");

        pos.setName("name");
        pos.setMaxLen("10");
        Assert.assertEquals(pos.getMaxLen(), "10");

        pos.setMaxLen("12");
        Assert.assertEquals(pos.getMaxLen(), "12");

        List<Map<String, String>> newList = maxLenTest.pipelinePossibleStates(pos, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name").length(), 1);
        Assert.assertEquals(newList.get(1).get("name").length(), 12);
    }
}
