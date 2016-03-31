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
public class NegativeBoundHiveVarcharTest {

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag neg = new NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag();

        neg.setName("decimal_test");
        Assert.assertEquals(neg.getName(), "decimal_test");

        neg.setName("decimal_test2");
        Assert.assertEquals(neg.getName(), "decimal_test2");
    }

    /**
     * test for setLength() and getLength()
     */
    @Test
    public void lengthTest() {
        NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag neg = new NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag();

        neg.setLength("18");
        Assert.assertEquals(neg.getLength(), "18");

        neg.setLength("38");
        Assert.assertEquals(neg.getLength(), "38");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag neg = new NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag();

        neg.setNullable("true");
        Assert.assertEquals(neg.getNullable(), "true");

        neg.setNullable("false");
        Assert.assertEquals(neg.getNullable(), "false");
    }

    /**
     * test for minLen
     */
    @Test
    public void minLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveVarchar maxLenTest = new NegativeBoundHiveVarchar();
        NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag neg = new NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag();

        neg.setName("name");
        neg.setLength("18");
        neg.setMinLen("10");
        Assert.assertEquals(neg.getMinLen(), "10");

        List<Map<String, String>> newList = maxLenTest.pipelinePossibleStates(neg, listOfMaps);
        Assert.assertEquals(newList.get(0).get("name").length(), 9);
        Assert.assertEquals(newList.get(1).get("name").length(), 19);
    }

    /**
     * test for allCaps
     */
    @Test
    public void allCapsTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveVarchar maxLenTest = new NegativeBoundHiveVarchar();
        NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag neg = new NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag();

        neg.setName("name");
        neg.setAllCaps("true");

        List<Map<String, String>> newList = maxLenTest.pipelinePossibleStates(neg, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name"), newList.get(0).get("name").toUpperCase());
        Assert.assertEquals(newList.get(1).get("name"), newList.get(1).get("name").toUpperCase());
    }

    /**
     * test for maxLen
     */
    @Test
    public void maxLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveVarchar maxLenTest = new NegativeBoundHiveVarchar();
        NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag neg = new NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag();

        neg.setName("name");
        neg.setLength("18");
        neg.setMaxLen("10");
        Assert.assertEquals(neg.getMaxLen(), "10");

        List<Map<String, String>> newList = maxLenTest.pipelinePossibleStates(neg, listOfMaps);

        neg.setMaxLen("100");
        Assert.assertEquals(newList.get(0).get("name").length(), 1);
        Assert.assertEquals(newList.get(1).get("name").length(), 11);
    }
}