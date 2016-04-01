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

import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
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
public class NegativeBoundHiveDecimalTest {

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag neg = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();

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
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag neg = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();

        neg.setLength("18,8");
        Assert.assertEquals(neg.getLength(), "18,8");

        neg.setLength("38,10");
        Assert.assertEquals(neg.getLength(), "38,10");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag neg = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();

        neg.setNullable("true");
        Assert.assertEquals(neg.getNullable(), "true");

        neg.setNullable("false");
        Assert.assertEquals(neg.getNullable(), "false");
    }

    /**
     * check that action does nothing
     */
    @Test
    public void actionTest() {
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag neg = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();
        try {
            neg.execute(null, null, null, null, null);
        } catch (SCXMLExpressionException | ModelException e) {
            Assert.fail("Exceptions should never be thrown");
        }
    }

    /**
     * check to make sure null is only added when nullable=true
     */
    @Test
    public void nullTest2() {
        Map<String, String> variableDomains = new HashMap<>();
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag neg = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();
        NegativeBoundHiveDecimal test = new NegativeBoundHiveDecimal();
        List<Map<String, String>> listOfMaps = new LinkedList<>();

        variableDomains.put("decimal", "");
        listOfMaps.add(variableDomains);

        neg.setLength("18,8");
        neg.setName("decimal");

        neg.setNullable("true");
        List<Map<String, String>> list = test.pipelinePossibleStates(neg, listOfMaps);
        Assert.assertEquals(list.size(), 2);

        neg.setNullable("false");
        list = test.pipelinePossibleStates(neg, listOfMaps);
        Assert.assertEquals(list.size(), 3);
    }

    /**
     *  check min and max parameters
     */
    @Test
    public void minMaxTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveDecimal minMaxTest = new NegativeBoundHiveDecimal();
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag tag = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();

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

        Assert.assertEquals(newList.get(0).get("Name"), "99");
        Assert.assertEquals(newList.get(1).get("Name"), "201");
    }

    /**
     *  check minLen parameter
     */
    @Test
    public void minLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveDecimal minLenTest = new NegativeBoundHiveDecimal();
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag tag = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();

        tag.setName("name");
        tag.setMinLen("10");
        Assert.assertEquals(tag.getMinLen(), "10");

        tag.setMinLen("100");
        Assert.assertEquals(tag.getMinLen(), "100");

        List<Map<String, String>> newList = minLenTest.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name").length(), 9);
        Assert.assertEquals(newList.get(1).get("name").length(), 11);
    }

    /**
     * maxLen test
     */
    @Test
    public void maxLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveDecimal maxLenTest = new NegativeBoundHiveDecimal();
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag tag = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();

        tag.setName("name");
        tag.setMinLen("100");
        Assert.assertEquals(tag.getMinLen(), "100");

        List<Map<String, String>> newList = maxLenTest.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name").length(), 9);
        Assert.assertEquals(newList.get(1).get("name").length(), 11);
    }

    /**
     * minLen and maxLen
     */
    @Test
    public void minLenAndmaxLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveDecimal maxLenTest = new NegativeBoundHiveDecimal();
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag tag = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();

        tag.setName("name");
        tag.setMinLen("5");
        tag.setMaxLen("9");

        List<Map<String, String>> newList = maxLenTest.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name").length(), 4);
        Assert.assertEquals(newList.get(1).get("name").length(), 10);
    }

    /**
     * no parameters set
     */
    @Test
    public void defaultBehaviorTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveDecimal setTest = new NegativeBoundHiveDecimal();
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag tag = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();
        tag.setName("Name");

        List<Map<String, String>> newList = setTest.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(0).get("Name").length(), 11);
        Assert.assertEquals(newList.get(1).get("Name").length(), 11);
    }

    /**
     * test with minLen and max set
     */
    @Test
    public void minLenAndMaxTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveDecimal defaultBehavior = new NegativeBoundHiveDecimal();
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag tag = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();

        tag.setName("name");
        tag.setMinLen("10");
        tag.setMax("1000");

        List<Map<String, String>> newList = defaultBehavior.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name"), "-100000000");
        Assert.assertEquals(newList.get(1).get("name"), "1001");
    }

    /**
     * test with min and maxLen set
     */
    @Test
    public void minAndMaxLenTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveDecimal defaultBehavior = new NegativeBoundHiveDecimal();
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag tag = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();

        tag.setName("name");
        tag.setMin("10");
        tag.setMaxLen("12");

        List<Map<String, String>> newList = defaultBehavior.pipelinePossibleStates(tag, listOfMaps);

        Assert.assertEquals(newList.get(0).get("name"), "9");
        Assert.assertEquals(newList.get(1).get("name").length(), 11);
    }

    /**
     * check the size of the list of states
     */
    @Test
    public void setTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        variableDomains.put("A", "1");
        variableDomains.put("B", "2");
        variableDomains.put("C", "3");
        listOfMaps.add(variableDomains);

        NegativeBoundHiveDecimal setTest = new NegativeBoundHiveDecimal();
        NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag tag = new NegativeBoundHiveDecimal.NegativeBoundHiveDecimalTag();
        tag.setNullable("true");
        tag.setLength("38,18");
        tag.setName("Name");

        List<Map<String, String>> newList = setTest.pipelinePossibleStates(tag, listOfMaps);
        Assert.assertEquals(newList.get(0).get("A"), "1");
        Assert.assertEquals(newList.get(0).get("B"), "2");
        Assert.assertEquals(newList.get(0).get("C"), "3");

        Assert.assertEquals(newList.get(1).get("A"), "1");
        Assert.assertEquals(newList.get(1).get("B"), "2");
        Assert.assertEquals(newList.get(1).get("C"), "3");

        Assert.assertEquals(newList.size(), 2);
    }
}
