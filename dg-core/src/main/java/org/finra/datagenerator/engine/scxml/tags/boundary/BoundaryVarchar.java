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
        boolean allCaps = false;
        boolean nullable = true;

        if (action.getAllCaps().equals("true")) {
            allCaps = true;
        }

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
                Integer.parseInt(minLen), allCaps);
        } else {
            return negativeCaseVarchar(nullable, Integer.parseInt(maxLen),
                Integer.parseInt(minLen), allCaps);
        }
    }

    /**
     * @param nullable nullable
     * @param maxLen   contains max length for varchar
     * @param minLen   contains min legnth for varchar
     * @param allCaps  flag for generating all uppercase characters
     * @return a list of boundary cases
     */
    public List<String> positiveCaseVarchar(boolean nullable, int maxLen, int minLen, boolean allCaps) {
        Method m;
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        StringBuilder varcharMid = new StringBuilder();
        StringBuilder varcharMin = new StringBuilder();
        StringBuilder varcharMax = new StringBuilder();
        List<String> variableValue = new LinkedList<>();
        String val;

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("alpha",
                StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, varcharMax, maxLen);
            m.invoke(eq, varcharMin, minLen);
            m.invoke(eq, varcharMid, (maxLen - minLen) / 2 + minLen);

            val = allCaps ? varcharMin.toString().toUpperCase() : varcharMin.toString();

            if (maxLen != minLen) { // if they are the same, only need to add one
                variableValue.add(val);
            }
            val = allCaps ? varcharMax.toString().toUpperCase() : varcharMax.toString();
            variableValue.add(val);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        val = allCaps ? varcharMid.toString().toUpperCase() : varcharMid.toString();
        variableValue.add(val);

        if (nullable) {
            variableValue.add("");
        }
        return variableValue;
    }

    /**
     * @param nullable nullable
     * @param maxLen   contains the max length for this varchar
     * @param minLen   contains the min length for this varchar
     * @param allCaps  flag for generating all uppercase characters
     * @return a list of boundary cases
     */
    public List<String> negativeCaseVarchar(boolean nullable, int maxLen, int minLen, boolean allCaps) {
        Method m;
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        StringBuilder varcharMin = new StringBuilder();
        StringBuilder varcharMax = new StringBuilder();
        List<String> variableValue = new LinkedList<>();
        String val;

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("alpha",
                StringBuilder.class, int.class);
            m.setAccessible(true);

            if (minLen == 1) {
                if (!nullable) {
                    minLen--;
                }
            } else {
                minLen--;
            }

            m.invoke(eq, varcharMax, maxLen + 1);
            m.invoke(eq, varcharMin, minLen);

            val = allCaps ? varcharMin.toString().toUpperCase() : varcharMin.toString();

            if (maxLen != minLen) { // if they are the same, only need to add one
                variableValue.add(val);
            }
            val = allCaps ? varcharMax.toString().toUpperCase() : varcharMax.toString();
            variableValue.add(val);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (!nullable && !variableValue.contains("")) {
            variableValue.add("");
        }
        return variableValue;
    }

    /**
     * @param action            an Action of the type handled by this class
     * @param possibleStateList a current list of possible states produced so far from
     *                          expanding a model state
     * @param variableValue     a list storing the values
     * @return a list of Maps containing the cross product of all states
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