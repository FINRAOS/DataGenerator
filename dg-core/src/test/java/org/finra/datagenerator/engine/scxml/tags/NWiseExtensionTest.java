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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Marshall Peters
 * Date: 1/21/15
 */
public class NWiseExtensionTest {

    /**
     * NWiseAction wraps a 'coVariables' value
     */
    @Test
    public void getSetCoVariablesTest() {
        NWiseExtension.NWiseAction nWiseAction = new NWiseExtension.NWiseAction();

        nWiseAction.setCoVariables("foo,bar");
        Assert.assertEquals("foo,bar", nWiseAction.getCoVariables());

        nWiseAction.setCoVariables("lorem,ipsum");
        Assert.assertEquals("lorem,ipsum", nWiseAction.getCoVariables());
    }

    /**
     * NWiseAction wraps an 'n' value
     */
    @Test
    public void getSetNTest() {
        NWiseExtension.NWiseAction nWiseAction = new NWiseExtension.NWiseAction();

        nWiseAction.setN("5");
        Assert.assertEquals("5", nWiseAction.getN());

        nWiseAction.setN("3");
        Assert.assertEquals("3", nWiseAction.getN());
    }

    /**
     * The execute method of NWiseAction does nothing
     */
    @Test
    public void executeDoesNothingTest() {
        NWiseExtension.NWiseAction nWiseAction = new NWiseExtension.NWiseAction();

        nWiseAction.setN("5");
        nWiseAction.setCoVariables("foo,bar");

        try {
            nWiseAction.execute(null, null, null, null, null);
        } catch (SCXMLExpressionException | ModelException e) {
            Assert.fail("Exceptions should never be thrown");
        }

        Assert.assertEquals("foo,bar", nWiseAction.getCoVariables());
        Assert.assertEquals("5", nWiseAction.getN());
    }

    /**
     * The tag is named 'nwise'
     */
    @Test
    public void nameSpaceTest() {
        NWiseExtension nWiseExtension = new NWiseExtension();
        Assert.assertEquals("org.finra.datagenerator", nWiseExtension.getTagNameSpace());
    }

    /**
     * The nwise tag uses the data generator name space
     */
    @Test
    public void tagNameTest() {
        NWiseExtension nWiseExtension = new NWiseExtension();
        Assert.assertEquals("nwise", nWiseExtension.getTagName());
    }

    /**
     * NWiseExtension processes NWiseAction objects
     */
    @Test
    public void correctActionClassTest() {
        NWiseExtension nWiseExtension = new NWiseExtension();
        Assert.assertEquals(NWiseExtension.NWiseAction.class, nWiseExtension.getTagActionClass());
    }

    /**
     * NWiseExtension has utility methods to produce all n tuples from a set of strings/variable names
     */
    @Test
    public void makeNWiseTuplesTest() {
        NWiseExtension nWiseExtension = new NWiseExtension();

        List<Set<String>> tuples = nWiseExtension.makeNWiseTuples(new String[]{"A", "B", "C", "D", "E"}, 3);
        Assert.assertEquals(10, tuples.size());

        tuples = nWiseExtension.makeNWiseTuples(new String[]{"A", "B", "C", "D", "E"}, 2);
        Assert.assertEquals(10, tuples.size());

        tuples = nWiseExtension.makeNWiseTuples(new String[]{"A", "B", "C", "D", "E"}, 1);
        Assert.assertEquals(5, tuples.size());
    }

    /**
     * Given a tuple of variable names and a map of variable names to domain values, produces a list of all assignments
     * of domain values to variables
     */
    @Test
    public void expandTupleIntoTestCasesTest() {
        NWiseExtension nWiseExtension = new NWiseExtension();

        Set<String> tuple = new HashSet<>();
        tuple.add("A");
        tuple.add("B");
        tuple.add("C");

        Map<String, String[]> variableDomains = new HashMap<>();
        variableDomains.put("A", new String[]{"1", "2", "3", "4"});
        variableDomains.put("B", new String[]{"1", "2", "3", "4"});
        variableDomains.put("C", new String[]{"1", "2", "3", "4"});

        List<Map<String, String>> testCases = nWiseExtension.expandTupleIntoTestCases(tuple, variableDomains);
        Assert.assertEquals(64, testCases.size());
    }

    /**
     * Finds all variable assignments within all tuples.
     */
    @Test
    public void produceNWiseTest() {
        NWiseExtension nWiseExtension = new NWiseExtension();

        Map<String, String[]> variableDomains = new HashMap<>();
        variableDomains.put("A", new String[]{"1", "2", "3", "4"});
        variableDomains.put("B", new String[]{"1", "2", "3", "4"});
        variableDomains.put("C", new String[]{"1", "2", "3", "4"});
        variableDomains.put("D", new String[]{"1", "2", "3", "4"});
        variableDomains.put("E", new String[]{"1", "2", "3", "4"});

        List<Map<String, String>> testCases = nWiseExtension.produceNWise(3,
                new String[]{"A", "B", "C", "D", "E"}, variableDomains);
        Assert.assertEquals(640, testCases.size());
    }

    /**
     * Finds all variable assignments within all tuples based on the values in the NWiseAction and the domains given by
     * assignment to variables within the input map, and then expands on all variable assignments within all tuples
     */
    @Test
    public void pipelinePossibleStatesTest() {
        NWiseExtension.NWiseAction nWiseAction = new NWiseExtension.NWiseAction();
        nWiseAction.setCoVariables("A,B,C,D,E");
        nWiseAction.setN("3");

        Map<String, String> inputState = new HashMap<>();
        inputState.put("A", "1,2,3,4");
        inputState.put("B", "1,2,3,4");
        inputState.put("C", "1,2,3,4");
        inputState.put("D", "1,2,3,4");
        inputState.put("E", "1,2,3,4");
        List<Map<String, String>> pipelineStates = new LinkedList<>();
        pipelineStates.add(inputState);

        NWiseExtension nWiseExtension = new NWiseExtension();
        List<Map<String, String>> resultStates = nWiseExtension.pipelinePossibleStates(nWiseAction, pipelineStates);

        Assert.assertEquals(640, resultStates.size());
    }
}
