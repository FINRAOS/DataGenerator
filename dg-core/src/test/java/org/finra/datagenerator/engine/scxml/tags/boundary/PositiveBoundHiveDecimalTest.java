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
public class PositiveBoundHiveDecimalTest {

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag pos = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        pos.setName("decimal_test");
        Assert.assertEquals(pos.getName(), "decimal_test");

        pos.setName("decimal_test2");
        Assert.assertEquals(pos.getName(), "decimal_test2");
    }

    /**
     * test for setLength() and getLength()
     */
    @Test
    public void lengthTest() {
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag pos = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        pos.setLength("18,8");
        Assert.assertEquals(pos.getLength(), "18,8");

        pos.setLength("38,10");
        Assert.assertEquals(pos.getLength(), "38,10");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag pos = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        pos.setNullable("true");
        Assert.assertEquals(pos.getNullable(), "true");

        pos.setNullable("false");
        Assert.assertEquals(pos.getNullable(), "false");
    }

    /**
     *  check min and max parameters
     */
    @Test
    public void minMaxTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        PositiveBoundHiveDecimal minMaxTest = new PositiveBoundHiveDecimal();
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag tag = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        tag.setName("Name");
        tag.setMin("10");
        tag.setMax("20");

        Assert.assertEquals(tag.getMin(), "10");
        Assert.assertEquals(tag.getMax(), "20");

        tag.setMin("100");
        tag.setMax("200");

        Assert.assertEquals(tag.getMin(), "100");
        Assert.assertEquals(tag.getMax(), "200");

        List<Map<String, String>> newList = minMaxTest.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(0).get("Name"), "100");
        Assert.assertEquals(newList.get(1).get("Name"), "200");
    }

    /**
     *  check minLen parameter
     */
    @Test
    public void minLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        PositiveBoundHiveDecimal minLenTest = new PositiveBoundHiveDecimal();
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag tag = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        tag.setName("name");
        tag.setMinLen("10");
        Assert.assertEquals(tag.getMinLen(), "10");

        tag.setMinLen("100");
        Assert.assertEquals(tag.getMinLen(), "100");

        List<Map<String, String>> newList = minLenTest.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(1).get("name").length(), 11);
        Assert.assertEquals(newList.get(2).get("name").length(), 10);
    }

    /**
     *  maxLen test
     */
    @Test
    public void maxLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        PositiveBoundHiveDecimal maxLenTest = new PositiveBoundHiveDecimal();
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag tag = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        tag.setName("name");
        tag.setMinLen("100");
        Assert.assertEquals(tag.getMinLen(), "100");

        List<Map<String, String>> newList = maxLenTest.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(1).get("name").length(), 11);
        Assert.assertEquals(newList.get(2).get("name").length(), 10);
    }

    /**
     * testing minLen and maxLen
     */
    @Test
    public void minLenAndmaxLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        PositiveBoundHiveDecimal maxLenTest = new PositiveBoundHiveDecimal();
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag tag = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        tag.setName("name");
        tag.setMinLen("5");
        tag.setMaxLen("9");

        List<Map<String, String>> newList = maxLenTest.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name").length(), 5);
        Assert.assertEquals(newList.get(1).get("name").length(), 9);
    }

    /**
     * no parameters set
     */
    @Test
    public void defaultBehaviorTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        PositiveBoundHiveDecimal setTest = new PositiveBoundHiveDecimal();
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag tag = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();
        tag.setName("Name");

        List<Map<String, String>> newList = setTest.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(1).get("Name").length(), 11);
        Assert.assertEquals(newList.get(2).get("Name").length(), 10);
    }

    /**
     *  test with minLen and max (not maxLen) set
     */
    @Test
    public void minLenAndMaxTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        PositiveBoundHiveDecimal defaultBehavior = new PositiveBoundHiveDecimal();
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag tag = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        tag.setName("name");
        tag.setMinLen("10");
        tag.setMax("1000");

        List<Map<String, String>> newList = defaultBehavior.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name"), "0");
        Assert.assertEquals(newList.get(1).get("name"), "-9999999999");
        Assert.assertEquals(newList.get(2).get("name"), "1000");
        Assert.assertEquals(newList.get(3).get("name"), "-4999999499");
        Assert.assertEquals(newList.get(4).get("name"), "");
    }

    /**
     * test with min (not minLen) and maxLen set
     */
    @Test
    public void minAndMaxLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        PositiveBoundHiveDecimal defaultBehavior = new PositiveBoundHiveDecimal();
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag tag = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        tag.setName("name");
        tag.setMin("10");
        tag.setMaxLen("12");

        List<Map<String, String>> newList = defaultBehavior.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name"), "10");
        Assert.assertEquals(newList.get(1).get("name").length(), 10);
    }
}
