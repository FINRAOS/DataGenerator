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
import org.finra.datagenerator.engine.scxml.tags.boundary.action.BoundaryActionDecimal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @param <T> a generic type that represents a BoundaryTag
 */
public abstract class BoundaryDecimal<T extends BoundaryActionDecimal> implements CustomTagExtension<T> {
    /**
     *
     * @param action            an Action of the type handled by this class
     * @param positive          a boolean denoting positive or negative cases
     * @return a list of Maps containing the states
     */
    public List<String> buildDecimalData(T action, boolean positive) {
        boolean lengthPresent = true;
        int randomNumber;
        String[] length = action.getLength().split(",");
        String minLen = action.getMinLen();
        String maxLen = action.getMaxLen();
        String min = action.getMin();
        String max = action.getMax();
        boolean nullable = true;

        if (!action.getNullable().equals("true")) {
            nullable = false;
        }

        if (minLen != null && maxLen != null) { //minLen and maxLen presence overrides length
            lengthPresent = false;
        }

        int leadingDigits = Integer.parseInt(length[0]) - Integer.parseInt(length[1]);
        int trailingDigits = Integer.parseInt(length[1]);

        if (positive) {
            return positiveCaseDecimal(nullable, leadingDigits, trailingDigits);
        } else {
            return negativeCaseDecimal(nullable, leadingDigits, trailingDigits);
        }
    }

    /**
     *
     * @param leadingDigits         an int with the number of leading digits
     * @param trailingDigits        an int with the number of trailing digits
     * @param nullable              nullable
     * @return                      a list of boundary cases
     */
    public List<String> positiveCaseDecimal(boolean nullable, int leadingDigits, int trailingDigits) {
        StringBuilder decimalBound = new StringBuilder();
        StringBuilder decimalMid = new StringBuilder();
        int randomNumberLeading = (int) (1 + Math.random() * (leadingDigits - 1));
        int randomNumberTrailing = (int) (1 + Math.random() * (trailingDigits - 1));
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, decimalBound, leadingDigits);
            decimalBound.append(".");
            m.invoke(eq, decimalBound, trailingDigits);

            m.invoke(eq, decimalMid, randomNumberLeading);
            decimalMid.append(".");
            m.invoke(eq, decimalMid, randomNumberTrailing);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        values.add(decimalBound.toString());
        values.add(decimalMid.toString());

        if (nullable) {
            values.add("null");
        }

        return values;
    }

    /**
     *
     * @param leadingDigits    an int with the number of leading digits
     * @param trailingDigits   an int with the number of trailing digits
     * @param nullable         nullable
     * @return                 a list of boundary cases
     */
    public List<String> negativeCaseDecimal(boolean nullable, int leadingDigits, int trailingDigits) {
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        StringBuilder decimalOne = new StringBuilder();
        StringBuilder decimalTwo = new StringBuilder();
        StringBuilder string = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence",
                StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, decimalOne, leadingDigits + 1);
            decimalOne.append(".");
            m.invoke(eq, decimalOne, trailingDigits);

            m.invoke(eq, decimalTwo, leadingDigits);
            decimalTwo.append(".");
            m.invoke(eq, decimalTwo, trailingDigits + 1);

            m = EquivalenceClassTransformer.class.getDeclaredMethod("alpha",
                StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, string, leadingDigits + trailingDigits);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        values.add(decimalOne.toString());
        values.add(decimalTwo.toString());
        values.add(string.toString());

        if (!nullable) {
            values.add("null");
        }

        return values;
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