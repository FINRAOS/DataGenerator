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

import org.apache.commons.scxml.model.Assign;
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
public class SingleValueAssignExtensionTest {

    /**
     * SingleValueAssignExtension uses the built in scxml Assign tag class
     */
    @Test
    public void actionClassTest() {
        SingleValueAssignExtension assign = new SingleValueAssignExtension();
        Assert.assertEquals(Assign.class, assign.getTagActionClass());
    }

    /**
     * 'assign' is the name of the tag
     */
    @Test
    public void tagNameTest() {
        SingleValueAssignExtension assign = new SingleValueAssignExtension();
        Assert.assertEquals("assign", assign.getTagName());
    }

    /**
     * The tag uses the default scxml name space
     */
    @Test
    public void tagNameSpaceTest() {
        SingleValueAssignExtension assign = new SingleValueAssignExtension();
        Assert.assertEquals("http://www.w3.org/2005/07/scxml", assign.getTagNameSpace());
    }

    /**
     * SingleValueAssignExtension only assigns values to extant maps of variable value pairs
     */
    @Test
    public void pipelineEmptyListTest() {
        List<Map<String, String>> emptyList = new LinkedList<>();
        SingleValueAssignExtension assign = new SingleValueAssignExtension();

        Assign concreteAssignTag = new Assign();
        concreteAssignTag.setName("var_out_test");
        concreteAssignTag.setExpr("A");

        List<Map<String, String>> processedList = assign.pipelinePossibleStates(concreteAssignTag, emptyList);
        Assert.assertNotNull(processedList);
        Assert.assertEquals(0, processedList.size());
    }

    /**
     * SingleValueAssignExtension will assign to extant but empty maps of variable value pairs
     */
    @Test
    public void pipelineEmptyMapTest() {
        List<Map<String, String>> listWithEmptyMap = new LinkedList<>();
        listWithEmptyMap.add(new HashMap<String, String>());
        SingleValueAssignExtension assign = new SingleValueAssignExtension();

        Assign concreteAssignTag = new Assign();
        concreteAssignTag.setName("var_out_test");
        concreteAssignTag.setExpr("A");

        List<Map<String, String>> processedList = assign.pipelinePossibleStates(concreteAssignTag, listWithEmptyMap);
        Assert.assertEquals(1, processedList.size());
        Map<String, String> resultMap = processedList.get(0);

        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("A", resultMap.get("var_out_test"));
    }

    /**
     * SingleValueAssignExtension assigns the value in expr="foo" to
     * variable given in name="bar" without touching any other variables
     */
    @Test
    public void pipelineNonEmptyMapTest() {
        List<Map<String, String>> listWithNonEmptyMap = new LinkedList<>();
        HashMap<String, String> nonEmptyMap = new HashMap<>();
        nonEmptyMap.put("var_out_test_1", "Lorem");
        nonEmptyMap.put("var_out_test_2", "ipsum");
        nonEmptyMap.put("var_out_test_3", "doler");
        nonEmptyMap.put("var_out_test_4", "sit");
        nonEmptyMap.put("var_out_test_5", "amet");
        listWithNonEmptyMap.add(nonEmptyMap);
        SingleValueAssignExtension assign = new SingleValueAssignExtension();

        Assign concreteAssignTag = new Assign();
        concreteAssignTag.setName("var_out_test_6");
        concreteAssignTag.setExpr("consectetur");

        List<Map<String, String>> processedList = assign.pipelinePossibleStates(concreteAssignTag, listWithNonEmptyMap);
        Assert.assertEquals(1, processedList.size());
        Map<String, String> resultMap = processedList.get(0);

        Assert.assertEquals(6, resultMap.keySet().size());
        Assert.assertEquals("Lorem", resultMap.get("var_out_test_1"));
        Assert.assertEquals("ipsum", resultMap.get("var_out_test_2"));
        Assert.assertEquals("doler", resultMap.get("var_out_test_3"));
        Assert.assertEquals("sit", resultMap.get("var_out_test_4"));
        Assert.assertEquals("amet", resultMap.get("var_out_test_5"));
        Assert.assertEquals("consectetur", resultMap.get("var_out_test_6"));
    }

    /**
     * SingleValueAssignExtension will overwrite the value of a variable it it already had a value
     */
    @Test
    public void pipelineOverwriteTest() {
        List<Map<String, String>> listWithNonEmptyMap = new LinkedList<>();
        HashMap<String, String> nonEmptyMap = new HashMap<>();
        nonEmptyMap.put("var_out_test", "Lorem");
        listWithNonEmptyMap.add(nonEmptyMap);
        SingleValueAssignExtension assign = new SingleValueAssignExtension();

        Assign concreteAssignTag = new Assign();
        concreteAssignTag.setName("var_out_test");
        concreteAssignTag.setExpr("consectetur");

        List<Map<String, String>> processedList = assign.pipelinePossibleStates(concreteAssignTag, listWithNonEmptyMap);
        Assert.assertEquals(1, processedList.size());
        Map<String, String> resultMap = processedList.get(0);

        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("consectetur", resultMap.get("var_out_test"));
    }

    /**
     * Multiple maps of variable value pairs are all modified with the new assignment individually
     */
    @Test
    public void pipelineMultipleMapsTest() {
        List<Map<String, String>> listWithManyMaps = new LinkedList<>();
        HashMap<String, String> nonEmptyMapOne = new HashMap<>();
        nonEmptyMapOne.put("var_out_test_1", "Lorem");
        nonEmptyMapOne.put("var_out_test_2", "ipsum");
        listWithManyMaps.add(nonEmptyMapOne);
        HashMap<String, String> nonEmptyMapTwo = new HashMap<>(nonEmptyMapOne);
        listWithManyMaps.add(nonEmptyMapTwo);
        HashMap<String, String> nonEmptyMapThree = new HashMap<>(nonEmptyMapOne);
        listWithManyMaps.add(nonEmptyMapThree);
        SingleValueAssignExtension assign = new SingleValueAssignExtension();

        Assign concreteAssignTag = new Assign();
        concreteAssignTag.setName("var_out_test_3");
        concreteAssignTag.setExpr("consectetur");

        List<Map<String, String>> processedList = assign.pipelinePossibleStates(concreteAssignTag, listWithManyMaps);
        Assert.assertEquals(3, processedList.size());

        Map<String, String> resultMap = processedList.get(0);
        Assert.assertEquals(3, resultMap.keySet().size());
        Assert.assertEquals("Lorem", resultMap.get("var_out_test_1"));
        Assert.assertEquals("ipsum", resultMap.get("var_out_test_2"));
        Assert.assertEquals("consectetur", resultMap.get("var_out_test_3"));

        resultMap = processedList.get(1);
        Assert.assertEquals(3, resultMap.keySet().size());
        Assert.assertEquals("Lorem", resultMap.get("var_out_test_1"));
        Assert.assertEquals("ipsum", resultMap.get("var_out_test_2"));
        Assert.assertEquals("consectetur", resultMap.get("var_out_test_3"));

        resultMap = processedList.get(2);
        Assert.assertEquals(3, resultMap.keySet().size());
        Assert.assertEquals("Lorem", resultMap.get("var_out_test_1"));
        Assert.assertEquals("ipsum", resultMap.get("var_out_test_2"));
        Assert.assertEquals("consectetur", resultMap.get("var_out_test_3"));
    }
}
