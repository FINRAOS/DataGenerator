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
 * Date: 12/8/14
 */
public class SetAssignExtensionTest {

    /**
     * SetAssignTag tracks two values: the name of a variable and a legal set of values stored as a string
     */
    @Test
    public void getAndSetVariableNameForTagTest() {
        SetAssignExtension.SetAssignTag setAssignTag = new SetAssignExtension.SetAssignTag();

        setAssignTag.setName("var_out_test");
        Assert.assertEquals("var_out_test", setAssignTag.getName());

        setAssignTag.setName("var_out_new");
        Assert.assertEquals("var_out_new", setAssignTag.getName());
    }

    /**
     * SetAssignTag tracks two values: the name of a variable and a legal set of values stored as a string
     */
    @Test
    public void getAndSetValueSetForTagTest() {
        SetAssignExtension.SetAssignTag setAssignTag = new SetAssignExtension.SetAssignTag();

        setAssignTag.setSet("A,B,C,D,E");
        Assert.assertEquals("A,B,C,D,E", setAssignTag.getSet());

        setAssignTag.setSet("F,G");
        Assert.assertEquals("F,G", setAssignTag.getSet());
    }

    /**
     * SetAssignTag has an empty execute method
     */
    @Test
    public void executeDoesNothingTest() {
        SetAssignExtension.SetAssignTag setAssignTag = new SetAssignExtension.SetAssignTag();
        setAssignTag.setName("var_out_test");
        setAssignTag.setSet("A,B,C,D,E");

        try {
            setAssignTag.execute(null, null, null, null, null);
        } catch (SCXMLExpressionException | ModelException e) {
            Assert.fail("Exceptions should never be thrown");
        }

        Assert.assertEquals("var_out_test", setAssignTag.getName());
        Assert.assertEquals("A,B,C,D,E", setAssignTag.getSet());
    }

    /**
     * SetAssignExtension uses a custom Action class called SetAssignTag
     */
    @Test
    public void actionClassTest() {
        SetAssignExtension setAssign = new SetAssignExtension();
        Assert.assertEquals(SetAssignExtension.SetAssignTag.class, setAssign.getTagActionClass());
    }

    /**
     * 'assign' is the name of the tag
     */
    @Test
    public void tagNameTest() {
        SetAssignExtension setAssign = new SetAssignExtension();
        Assert.assertEquals("assign", setAssign.getTagName());
    }

    /**
     * The tag uses the datagenerator namespace
     */
    @Test
    public void tagNameSpaceTest() {
        SetAssignExtension setAssign = new SetAssignExtension();
        Assert.assertEquals("org.finra.datagenerator", setAssign.getTagNameSpace());
    }

    /**
     * SetAssignExtension only assigns values to extant maps of variable value pairs
     */
    @Test
    public void pipelineEmptyListTest() {
        List<Map<String, String>> emptyList = new LinkedList<>();
        SetAssignExtension setAssign = new SetAssignExtension();

        SetAssignExtension.SetAssignTag setAssignTag = new SetAssignExtension.SetAssignTag();
        setAssignTag.setName("var_out_test");
        setAssignTag.setSet("A,B,C,D,E");

        List<Map<String, String>> processedList = setAssign.pipelinePossibleStates(setAssignTag, emptyList);
        Assert.assertNotNull(processedList);
        Assert.assertEquals(0, processedList.size());
    }

    /**
     * An empty set of values counts as a one value set with "" as a value
     */
    @Test
    public void pipelineExpandOnEmptySetTest() {
        List<Map<String, String>> listWithEmptyMap = new LinkedList<>();
        listWithEmptyMap.add(new HashMap<String, String>());
        SetAssignExtension setAssign = new SetAssignExtension();

        SetAssignExtension.SetAssignTag setAssignTag = new SetAssignExtension.SetAssignTag();
        setAssignTag.setName("var_out_test");
        setAssignTag.setSet("");

        List<Map<String, String>> processedList = setAssign.pipelinePossibleStates(setAssignTag, listWithEmptyMap);
        Assert.assertEquals(1, processedList.size());
        Map<String, String> resultMap = processedList.get(0);
        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("", resultMap.get("var_out_test"));
    }

    /**
     * SingleValueAssignExtension will assign to extant but empty maps of variable value pairs
     */
    @Test
    public void pipelineCardinalityTest() {
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(new HashMap<String, String>());
        listOfMaps.add(new HashMap<String, String>());
        listOfMaps.add(new HashMap<String, String>());
        SetAssignExtension setAssign = new SetAssignExtension();

        SetAssignExtension.SetAssignTag setAssignTag = new SetAssignExtension.SetAssignTag();
        setAssignTag.setName("var_out_test");
        setAssignTag.setSet("A,B,C,D,E");

        List<Map<String, String>> processedList = setAssign.pipelinePossibleStates(setAssignTag, listOfMaps);
        Assert.assertEquals(15, processedList.size());
    }

    /**
     * The ordering of output maps reflects the ordering of elements in the value set
     */
    @Test
    public void pipelineExpandOrderingTest() {
        List<Map<String, String>> listWithEmptyMap = new LinkedList<>();
        listWithEmptyMap.add(new HashMap<String, String>());
        SetAssignExtension setAssign = new SetAssignExtension();

        SetAssignExtension.SetAssignTag setAssignTag = new SetAssignExtension.SetAssignTag();
        setAssignTag.setName("var_out_test");
        setAssignTag.setSet("A,B,C");

        List<Map<String, String>> processedList = setAssign.pipelinePossibleStates(setAssignTag, listWithEmptyMap);
        Assert.assertEquals(3, processedList.size());

        Map<String, String> resultMap = processedList.get(0);
        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("A", resultMap.get("var_out_test"));

        resultMap = processedList.get(1);
        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("B", resultMap.get("var_out_test"));

        resultMap = processedList.get(2);
        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("C", resultMap.get("var_out_test"));
    }

    /**
     * SetAssignExtension will assign values only to the variable given in name="foo"
     */
    @Test
    public void pipelineExpandWithNonEmptyMapTest() {
        List<Map<String, String>> listWithNonEmptyMap = new LinkedList<>();
        HashMap<String, String> nonEmptyMap = new HashMap<>();
        nonEmptyMap.put("var_out_test_1", "3");
        listWithNonEmptyMap.add(nonEmptyMap);
        SetAssignExtension setAssign = new SetAssignExtension();

        SetAssignExtension.SetAssignTag setAssignTag = new SetAssignExtension.SetAssignTag();
        setAssignTag.setName("var_out_test_2");
        setAssignTag.setSet("A,B,C");

        List<Map<String, String>> processedList = setAssign.pipelinePossibleStates(setAssignTag, listWithNonEmptyMap);
        Assert.assertEquals(3, processedList.size());

        Map<String, String> resultMap = processedList.get(0);
        Assert.assertEquals(2, resultMap.keySet().size());
        Assert.assertEquals("3", resultMap.get("var_out_test_1"));
        Assert.assertEquals("A", resultMap.get("var_out_test_2"));

        resultMap = processedList.get(1);
        Assert.assertEquals(2, resultMap.keySet().size());
        Assert.assertEquals("3", resultMap.get("var_out_test_1"));
        Assert.assertEquals("B", resultMap.get("var_out_test_2"));

        resultMap = processedList.get(2);
        Assert.assertEquals(2, resultMap.keySet().size());
        Assert.assertEquals("3", resultMap.get("var_out_test_1"));
        Assert.assertEquals("C", resultMap.get("var_out_test_2"));
    }

    /**
     * SetAssignExtension will overwrite variables that already have values
     */
    @Test
    public void pipelineOverwriteTest() {
        List<Map<String, String>> listWithNonEmptyMap = new LinkedList<>();
        HashMap<String, String> nonEmptyMap = new HashMap<>();
        nonEmptyMap.put("var_out_test", "3");
        listWithNonEmptyMap.add(nonEmptyMap);
        SetAssignExtension setAssign = new SetAssignExtension();

        SetAssignExtension.SetAssignTag setAssignTag = new SetAssignExtension.SetAssignTag();
        setAssignTag.setName("var_out_test");
        setAssignTag.setSet("A,B,C");

        List<Map<String, String>> processedList = setAssign.pipelinePossibleStates(setAssignTag, listWithNonEmptyMap);
        Assert.assertEquals(3, processedList.size());

        Map<String, String> resultMap = processedList.get(0);
        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("A", resultMap.get("var_out_test"));

        resultMap = processedList.get(1);
        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("B", resultMap.get("var_out_test"));

        resultMap = processedList.get(2);
        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("C", resultMap.get("var_out_test"));
    }
    
    /**
        Tests user-defined variable separator in dg:assign tag
                                                                **/
    @Test
    public void separatorTest() {
        List<Map<String, String>> list = new LinkedList<>();
        list.add(new HashMap<String, String>());
        SetAssignExtension setAssign = new SetAssignExtension();

        SetAssignExtension.SetAssignTag setAssignTag = new SetAssignExtension.SetAssignTag();
        setAssignTag.setName("var_out_test");
        setAssignTag.setSet("(A,B)|C");
        setAssignTag.setSeparator("|");

        List<Map<String, String>> processedList = setAssign.pipelinePossibleStates(setAssignTag, list);
        Assert.assertEquals(2, processedList.size());

        Map<String, String> resultMap = processedList.get(0);
        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("(A,B)", resultMap.get("var_out_test"));
    }
}
