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

package org.finra.datagenerator.engine.scxml.tags;

import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Marshall Peters
 * Date: 12/9/14
 */
public class RangeExtensionTest {

    /**
     * Range tag wraps a 'name' string
     */
    @Test
    public void tagGetSetNameTest() {
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setName("Test");
        Assert.assertEquals("Test", rangeTag.getName());
        rangeTag.setName("Foo");
        Assert.assertEquals("Foo", rangeTag.getName());
    }

    /**
     * Range tag wraps a 'from' string
     */
    @Test
    public void tagGetSetFromTest() {
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setFrom("1.1");
        Assert.assertEquals("1.1", rangeTag.getFrom());
        rangeTag.setFrom("2.2");
        Assert.assertEquals("2.2", rangeTag.getFrom());
    }

    /**
     * Range tag wraps a 'to' string
     */
    @Test
    public void tagGetSetToTest() {
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setTo("1.1");
        Assert.assertEquals("1.1", rangeTag.getTo());
        rangeTag.setTo("2.2");
        Assert.assertEquals("2.2", rangeTag.getTo());
    }

    /**
     * Range tag wraps a 'step' string
     */
    @Test
    public void tagGetSetStepTest() {
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setStep("0.1");
        Assert.assertEquals("0.1", rangeTag.getStep());
        rangeTag.setStep("2.2");
        Assert.assertEquals("2.2", rangeTag.getStep());
    }

    /**
     * Range tag execute does nothing
     */
    @Test
    public void executeDoesNothingTest() {
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setName("Foo");
        rangeTag.setFrom("1.1");
        rangeTag.setTo("11.3");
        rangeTag.setStep("1.2");

        try {
            rangeTag.execute(null, null, null, null, null);
        } catch (SCXMLExpressionException | ModelException e) {
            Assert.fail("Exceptions should never be thrown");
        }

        Assert.assertEquals("Foo", rangeTag.getName());
        Assert.assertEquals("1.1", rangeTag.getFrom());
        Assert.assertEquals("11.3", rangeTag.getTo());
        Assert.assertEquals("1.2", rangeTag.getStep());
    }

    /**
     * The tag is 'range' in the model
     */
    @Test
    public void tagNameTest() {
        RangeExtension rangeExtension = new RangeExtension();
        Assert.assertEquals("range", rangeExtension.getTagName());
    }

    /**
     * The tag uses the datagenerator name space
     */
    @Test
    public void tagNameSpaceTest() {
        RangeExtension rangeExtension = new RangeExtension();
        Assert.assertEquals("org.finra.datagenerator", rangeExtension.getTagNameSpace());
    }

    /**
     * RangeExtension has an inner class called RangeTag used in model parsing
     */
    @Test
    public void actionClassTest() {
        RangeExtension rangeExtension = new RangeExtension();
        Assert.assertEquals(RangeExtension.RangeTag.class, rangeExtension.getTagActionClass());
    }

    /**
     * If step is not provided, then a value of 1 is assumed
     */
    @Test
    public void defaultStepTest() {
        List<Map<String, String>> pipeInputList = new LinkedList<>();
        pipeInputList.add(new HashMap<String, String>());
        RangeExtension rangeExtension = new RangeExtension();
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setName("var_out_test");
        rangeTag.setFrom("1");
        rangeTag.setTo("10");

        List<Map<String, String>> resultList = rangeExtension.pipelinePossibleStates(rangeTag, pipeInputList);
        Assert.assertEquals(10, resultList.size());
    }

    /**
     * Cannot expand an empty list
     */
    @Test
    public void emptyListTest() {
        List<Map<String, String>> emptyList = new LinkedList<>();
        RangeExtension rangeExtension = new RangeExtension();
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setName("var_out_test");
        rangeTag.setFrom("1");
        rangeTag.setTo("5");
        rangeTag.setStep("0.7");

        List<Map<String, String>> resultList = rangeExtension.pipelinePossibleStates(rangeTag, emptyList);
        Assert.assertNotNull(resultList);
        Assert.assertEquals(0, resultList.size());
    }

    /**
     * Only touches the variable given by 'name'
     */
    @Test
    public void noContaminationTest() {
        List<Map<String, String>> pipeInputList = new LinkedList<>();
        HashMap<String, String> nonEmptyPossibleState = new HashMap<>();
        nonEmptyPossibleState.put("var_out_test", "foobar");
        pipeInputList.add(nonEmptyPossibleState);
        RangeExtension rangeExtension = new RangeExtension();
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setName("var_out_test_2");
        rangeTag.setFrom("1");
        rangeTag.setTo("5");
        rangeTag.setStep("0.7");

        List<Map<String, String>> resultList = rangeExtension.pipelinePossibleStates(rangeTag, pipeInputList);
        Assert.assertEquals(6, resultList.size());
        for (int i = 0; i < 6; i++) {
            Assert.assertEquals("foobar", resultList.get(i).get("var_out_test"));
        }
    }

    /**
     * Tests with a positive step
     */
    @Test
    public void positiveStepTest() {
        List<Map<String, String>> pipeInputList = new LinkedList<>();
        pipeInputList.add(new HashMap<String, String>());
        RangeExtension rangeExtension = new RangeExtension();
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setName("var_out_test");
        rangeTag.setFrom("1");
        rangeTag.setTo("5");
        rangeTag.setStep("0.7");

        List<Map<String, String>> resultList = rangeExtension.pipelinePossibleStates(rangeTag, pipeInputList);
        Assert.assertEquals(6, resultList.size());

        Map<String, String> resultMap = resultList.get(0);
        Assert.assertEquals("1", resultMap.get("var_out_test"));
        resultMap = resultList.get(1);
        Assert.assertEquals("1.7", resultMap.get("var_out_test"));
        resultMap = resultList.get(2);
        Assert.assertEquals("2.4", resultMap.get("var_out_test"));
        resultMap = resultList.get(3);
        Assert.assertEquals("3.1", resultMap.get("var_out_test"));
        resultMap = resultList.get(4);
        Assert.assertEquals("3.8", resultMap.get("var_out_test"));
        resultMap = resultList.get(5);
        Assert.assertEquals("4.5", resultMap.get("var_out_test"));
    }
    
    /**
     * Tests with a big numbers
     */
    @Test
    public void bigNumbersTest() {
        List<Map<String, String>> pipeInputList = new LinkedList<>();
        pipeInputList.add(new HashMap<String, String>());
        RangeExtension rangeExtension = new RangeExtension();
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setName("var_out_test");
        rangeTag.setFrom("1");
        rangeTag.setTo("20000");
        rangeTag.setStep("10");

        List<Map<String, String>> resultList = rangeExtension.pipelinePossibleStates(rangeTag, pipeInputList);
        Assert.assertEquals(2000, resultList.size());

        Map<String, String> resultMap = resultList.get(0);
        Assert.assertEquals("1", resultMap.get("var_out_test"));
        resultMap = resultList.get(1);
        Assert.assertEquals("11", resultMap.get("var_out_test"));
        resultMap = resultList.get(100);
        Assert.assertEquals("1001", resultMap.get("var_out_test"));
        resultMap = resultList.get(1000);
        Assert.assertEquals("10001", resultMap.get("var_out_test"));
    }

    /**
     * Tests with a negative step
     */
    @Test
    public void negativeStepTest() {
        List<Map<String, String>> pipeInputList = new LinkedList<>();
        pipeInputList.add(new HashMap<String, String>());
        RangeExtension rangeExtension = new RangeExtension();
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setName("var_out_test");
        rangeTag.setFrom("1");
        rangeTag.setTo("-3");
        rangeTag.setStep("-0.7");

        List<Map<String, String>> resultList = rangeExtension.pipelinePossibleStates(rangeTag, pipeInputList);
        Assert.assertEquals(6, resultList.size());

        Map<String, String> resultMap = resultList.get(0);
        Assert.assertEquals("1", resultMap.get("var_out_test"));
        resultMap = resultList.get(1);
        Assert.assertEquals("0.3", resultMap.get("var_out_test"));
        resultMap = resultList.get(2);
        Assert.assertEquals("-0.4", resultMap.get("var_out_test"));
        resultMap = resultList.get(3);
        Assert.assertEquals("-1.1", resultMap.get("var_out_test"));
        resultMap = resultList.get(4);
        Assert.assertEquals("-1.8", resultMap.get("var_out_test"));
        resultMap = resultList.get(5);
        Assert.assertEquals("-2.5", resultMap.get("var_out_test"));
    }

    /**
     * A step of 0 expands only on the 'from' value
     */
    @Test
    public void zeroStepTest() {
        List<Map<String, String>> pipeInputList = new LinkedList<>();
        pipeInputList.add(new HashMap<String, String>());
        RangeExtension rangeExtension = new RangeExtension();
        RangeExtension.RangeTag rangeTag = new RangeExtension.RangeTag();
        rangeTag.setName("var_out_test");
        rangeTag.setFrom("1");
        rangeTag.setTo("-3");
        rangeTag.setStep("0");

        List<Map<String, String>> resultList = rangeExtension.pipelinePossibleStates(rangeTag, pipeInputList);
        Assert.assertEquals(1, resultList.size());

        Map<String, String> resultMap = resultList.get(0);
        Assert.assertEquals("1", resultMap.get("var_out_test"));
    }

}
