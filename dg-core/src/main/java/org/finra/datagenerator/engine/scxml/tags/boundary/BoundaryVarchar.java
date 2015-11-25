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

import org.finra.datagenerator.consumer.EquivalenceClassTransformer;
import org.finra.datagenerator.engine.scxml.tags.CustomTagExtension;
import org.finra.datagenerator.engine.scxml.tags.boundary.action.BoundaryActionVarchar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @param <T> a generic type that represents a BoundaryTag
 */
public abstract class BoundaryVarchar<T extends BoundaryActionVarchar> implements CustomTagExtension<T> {

    /**
     * @param action   an action of the type handled by this class
     * @param positive denotes positive of negative test cases
     * @return a list of boundary cases
     */
    public List<String> buildVarcharData(T action, boolean positive) {

        String length = action.getLength();
        String minLen = action.getMinLen();
        String maxLen = action.getMaxLen();
        boolean nullable = true;

        if (!action.getNullable().equals("true")) {
            nullable = false;
        }

        if (minLen != null) {
            if (Integer.parseInt(minLen) > Integer.parseInt(length)) {
                minLen = length;
            }
            if (maxLen != null) {
                if (Integer.parseInt(maxLen) > Integer.parseInt(length)) {
                    maxLen = length;
                }
            } else {
                maxLen = length;
            }
        } else {
            minLen = "1";
            if (maxLen != null) {
                if (Integer.parseInt(maxLen) > Integer.parseInt(length)) {
                    maxLen = length;
                }
            } else {
                maxLen = length;
            }
        }
        if (positive) {
            return positiveCaseVarchar(nullable, Integer.parseInt(maxLen),
                Integer.parseInt(minLen));
        } else {
            return negativeCaseVarchar(nullable, Integer.parseInt(length));
        }
    }

    /**
     * @param nullable nullable
     * @param lengths  contains either the length or minLen and maxLen, and random
     * @return a list of boundary cases
     */
    public List<String> positiveCaseVarchar(boolean nullable, int... lengths) {
        Method m;
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        StringBuilder varcharMid = new StringBuilder();
        StringBuilder varcharMin = new StringBuilder();
        StringBuilder varcharMax = new StringBuilder();
        List<String> variableValue = new LinkedList<>();

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("alpha",
                StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, varcharMax, lengths[0]);
            m.invoke(eq, varcharMin, lengths[1]);
            m.invoke(eq, varcharMid, (lengths[1] - lengths[0]) / 2 + lengths[0]);

            if (lengths[0] != lengths[1]) { // if they are the same, only need to add one
                variableValue.add(varcharMin.toString());
            }
            variableValue.add(varcharMax.toString());


        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        variableValue.add(varcharMid.toString());

        if (nullable) {
            variableValue.add("");
        }

        return variableValue;
    }

    /**
     * @param nullable nullable
     * @param lengths  contains either the length or minLen and maxLen, and random
     * @return a list of boundary cases
     */
    public List<String> negativeCaseVarchar(boolean nullable, int... lengths) {
        Method m;
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        StringBuilder varcharMin = new StringBuilder();
        StringBuilder varcharMax = new StringBuilder();
        List<String> variableValue = new LinkedList<>();

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("alpha",
                StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, varcharMax, lengths[0] + 1);
            m.invoke(eq, varcharMin, lengths[1] - 1);

            if (lengths[0] != lengths[1]) { // if they are the same, only need to add one
                variableValue.add(varcharMin.toString());
            }
            variableValue.add(varcharMax.toString());


        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (!nullable) {
            variableValue.add("");
        }

        return variableValue;
    }

    /**
     * @param action            an Action of the type handled by this class
     * @param possibleStateList a current list of possible states produced so far from
     *                          expanding a model state
     * @param variableValue     a list storing the values
     * @return a list of Maps containing the states
     */
    public List<Map<String, String>> returnStates(T action,
                                                  List<Map<String, String>> possibleStateList, List<String> variableValue) {
        List<Map<String, String>> states = new LinkedList<>();
        for (Map<String, String> p : possibleStateList) {
            for (String s : variableValue) {
                HashMap<String, String> n = new HashMap<>(p);
                n.put(action.getName(), s);
                states.add(n);
            }
        }
        return states;
    }
}