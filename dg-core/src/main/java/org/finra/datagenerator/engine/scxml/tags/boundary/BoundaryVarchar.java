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
        boolean lengthPresent = true;
        int randomNumber;
        String length = action.getLength();
        String minLen = action.getMinLen();
        String maxLen = action.getMaxLen();
        boolean nullable = true;

        if (!action.getNullable().equals("true")) {
            nullable = false;
        }
        randomNumber = (int) (1 + Math.random() * (Integer.parseInt(length) - 1));

        if (minLen != null && maxLen != null) { //minLen and maxLen presence overrides length
            lengthPresent = false;
            randomNumber = (int) (1 + Math.random() * (Integer.parseInt(maxLen) -
                Integer.parseInt(minLen)));
        }

        if (positive) {
            if (lengthPresent) {
                return positiveCaseVarchar(nullable, lengthPresent, Integer.parseInt(length),
                    randomNumber);
            } else {
                return positiveCaseVarchar(nullable, lengthPresent, Integer.parseInt(maxLen),
                    Integer.parseInt(minLen), randomNumber);
            }
        } else {
            if (lengthPresent) {
                return negativeCaseVarchar(nullable, lengthPresent, Integer.parseInt(length));
            } else {
                return negativeCaseVarchar(nullable, lengthPresent, Integer.parseInt(maxLen),
                    Integer.parseInt(minLen));
            }
        }
    }

    /**
     * @param lengthPresent whether length is present or not
     * @param nullable      nullable
     * @param lengths       contains either the length or minLen and maxLen, and random
     * @return a list of boundary cases
     */
    public List<String> positiveCaseVarchar(boolean nullable, boolean lengthPresent, int... lengths) {
        Method m;
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        StringBuilder varcharBound = new StringBuilder();
        StringBuilder varcharMid = new StringBuilder();
        StringBuilder varcharMin = new StringBuilder();
        StringBuilder varcharMax = new StringBuilder();
        List<String> variableValue = new LinkedList<>();

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("alpha",
                StringBuilder.class, int.class);
            m.setAccessible(true);

            if (lengthPresent) {
                m.invoke(eq, varcharBound, lengths[0]);
                m.invoke(eq, varcharMid, lengths[1]);
                variableValue.add(varcharBound.toString());
            } else {
                m.invoke(eq, varcharMax, lengths[0]);
                m.invoke(eq, varcharMin, lengths[1]);
                m.invoke(eq, varcharMid, lengths[2]);

                if (lengths[0] != lengths[1]) { // if they are the same, only need to add one
                    variableValue.add(varcharMin.toString());
                }
                variableValue.add(varcharMax.toString());
            }

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        variableValue.add(varcharMid.toString());

        if (nullable) {
            variableValue.add("null");
        }

        return variableValue;
    }

    /**
     * @param lengthPresent whether length is present or not
     * @param nullable      nullable
     * @param lengths       contains either the length or minLen and maxLen, and random
     * @return a list of boundary cases
     */
    public List<String> negativeCaseVarchar(boolean nullable, boolean lengthPresent, int... lengths) {
        Method m;
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        StringBuilder varcharBound = new StringBuilder();
        StringBuilder varcharMin = new StringBuilder();
        StringBuilder varcharMax = new StringBuilder();
        List<String> variableValue = new LinkedList<>();

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("alpha",
                StringBuilder.class, int.class);
            m.setAccessible(true);

            if (lengthPresent) {
                m.invoke(eq, varcharBound, lengths[0] + 1);
                variableValue.add(varcharBound.toString());
            } else {
                m.invoke(eq, varcharMax, lengths[0] + 1);
                m.invoke(eq, varcharMin, lengths[1] - 1);

                if (lengths[0] != lengths[1]) { // if they are the same, only need to add one
                    variableValue.add(varcharMin.toString());
                }
                variableValue.add(varcharMax.toString());
            }

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (!nullable) {
            variableValue.add("null");
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