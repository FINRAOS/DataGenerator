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
import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.consumer.DataTransformer;
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
public class InLineTransformerExtensionTest {

    /**
     * TransformTag is a wrapper for one String called 'name'
     */
    @Test
    public void getAndSetNameForTagTest() {
        InLineTransformerExtension.TransformTag transformTag = new InLineTransformerExtension.TransformTag();
        transformTag.setName("foo");
        Assert.assertEquals("foo", transformTag.getName());
        transformTag.setName("bar");
        Assert.assertEquals("bar", transformTag.getName());
    }

    /**
     * execute for TransformTag does nothing
     */
    @Test
    public void executeDoesNothingTest() {
        InLineTransformerExtension.TransformTag transformTag = new InLineTransformerExtension.TransformTag();
        transformTag.setName("test");

        try {
            transformTag.execute(null, null, null, null, null);
        } catch (SCXMLExpressionException | ModelException e) {
            Assert.fail("Exceptions should never be thrown");
        }

        Assert.assertEquals("test", transformTag.getName());
    }

    /**
     * InLineTransformerExtension uses an inner class called TransformTag for model parsing
     */
    @Test
    public void actionClassTest() {
        InLineTransformerExtension transformerExtension =
                new InLineTransformerExtension(new HashMap<String, DataTransformer>());
        Assert.assertEquals(InLineTransformerExtension.TransformTag.class, transformerExtension.getTagActionClass());
    }

    /**
     * The name of the tag is "transform"
     */
    @Test
    public void tagNameTest() {
        InLineTransformerExtension transformerExtension =
                new InLineTransformerExtension(new HashMap<String, DataTransformer>());
        Assert.assertEquals("transform", transformerExtension.getTagName());
    }

    /**
     * The tag uses the datagenerator name space
     */
    @Test
    public void tagNameSpaceTest() {
        InLineTransformerExtension transformerExtension =
                new InLineTransformerExtension(new HashMap<String, DataTransformer>());
        Assert.assertEquals("org.finra.datagenerator", transformerExtension.getTagNameSpace());
    }

    /**
     * A test implementation of DataTransformer that tracks whether the transform method was called
     */
    private static class CallCheckTransformer implements DataTransformer {
        private boolean wasCalled;

        /**
         * Sets wasCalled to true
         *
         * @param cr a reference to DataPipe from which to read the current map
         */
        public void transform(DataPipe cr) {
            wasCalled = true;
        }

        public boolean isWasCalled() {
            return wasCalled;
        }
    }

    /**
     * DataTransformers are added and named through a map to InLineTransformerExtension's constructor.
     * Individual DataTransformers are called by 'name' in the model
     */
    @Test
    public void addingAndCallingTransformersByNameTest() {
        CallCheckTransformer dummyTransformerOne = new CallCheckTransformer();
        CallCheckTransformer dummyTransformerTwo = new CallCheckTransformer();
        CallCheckTransformer dummyTransformerThree = new CallCheckTransformer();
        HashMap<String, DataTransformer> transformerNames = new HashMap<>();
        transformerNames.put("one", dummyTransformerOne);
        transformerNames.put("two", dummyTransformerTwo);
        transformerNames.put("three", dummyTransformerThree);
        InLineTransformerExtension transformerExtension = new InLineTransformerExtension(transformerNames);

        List<Map<String, String>> pipelineInput = new LinkedList<>();
        pipelineInput.add(new HashMap<String, String>());
        InLineTransformerExtension.TransformTag transformTag = new InLineTransformerExtension.TransformTag();
        transformTag.setName("two");

        transformerExtension.pipelinePossibleStates(transformTag, pipelineInput);

        Assert.assertFalse(dummyTransformerOne.isWasCalled());
        Assert.assertTrue(dummyTransformerTwo.isWasCalled());
        Assert.assertFalse(dummyTransformerThree.isWasCalled());
    }

    /**
     * InLineTransformerExtension only transforms extant maps of variable value pairs
     */
    @Test
    public void emptyListTest() {
        CallCheckTransformer dummyTransformer = new CallCheckTransformer();
        HashMap<String, DataTransformer> transformerNames = new HashMap<>();
        transformerNames.put("dummy", dummyTransformer);
        InLineTransformerExtension transformerExtension = new InLineTransformerExtension(transformerNames);

        List<Map<String, String>> emptyList = new LinkedList<>();
        InLineTransformerExtension.TransformTag transformTag = new InLineTransformerExtension.TransformTag();
        transformTag.setName("dummy");

        List<Map<String, String>> resultList = transformerExtension.pipelinePossibleStates(transformTag, emptyList);
        Assert.assertNotNull(resultList);
        Assert.assertEquals(0, resultList.size());
        Assert.assertFalse(dummyTransformer.isWasCalled());
    }

    /**
     * Every map of variable value pairs gets transformed
     */
    @Test
    public void nonEmptyListTest() {
        DataTransformer appendTransformer = new DataTransformer() {
            public void transform(DataPipe cr) {
                cr.getDataMap().put("Test", "Lorum");
            }
        };
        HashMap<String, DataTransformer> transformerNames = new HashMap<>();
        transformerNames.put("append", appendTransformer);
        InLineTransformerExtension transformerExtension = new InLineTransformerExtension(transformerNames);

        List<Map<String, String>> nonEmptyList = new LinkedList<>();
        nonEmptyList.add(new HashMap<String, String>());
        nonEmptyList.add(new HashMap<String, String>());
        InLineTransformerExtension.TransformTag transformTag = new InLineTransformerExtension.TransformTag();
        transformTag.setName("append");

        List<Map<String, String>> resultList = transformerExtension.pipelinePossibleStates(transformTag, nonEmptyList);
        Assert.assertNotNull(resultList);
        Assert.assertEquals(2, resultList.size());
        Map<String, String> resultMap = resultList.get(0);
        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("Lorum", resultMap.get("Test"));
        resultMap = resultList.get(1);
        Assert.assertEquals(1, resultMap.keySet().size());
        Assert.assertEquals("Lorum", resultMap.get("Test"));
    }
}
