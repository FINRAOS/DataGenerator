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

package org.finra.datagenerator.engine.scxml;

import org.junit.Assert;
import org.apache.commons.scxml.model.TransitionTarget;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Marshall Peters
 * Date: 12/10/14
 */
public class PossibleStateTest {

    /**
     * Possible state is a value object tracking a TransitionTarget and a Map of variable/value pairs
     */
    @Test
    public void constructorTest() {
        TransitionTarget dummyState = new TransitionTarget() {
        };
        dummyState.setId("Dummy State");
        Map<String, String> variables = new HashMap<>();
        PossibleState testState = new PossibleState(dummyState, variables);

        Assert.assertSame(dummyState, testState.nextState);
        Assert.assertSame(variables, testState.variables);
    }

    /**
     * Custom toString() method
     */
    @Test
    public void toStringTest() {
        TransitionTarget dummyState = new TransitionTarget() {
        };
        dummyState.setId("Dummy State");
        Map<String, String> variables = new HashMap<>();
        PossibleState testState = new PossibleState(dummyState, variables);

        Assert.assertEquals("<Dummy State;{}>", testState.toString());
    }
}
