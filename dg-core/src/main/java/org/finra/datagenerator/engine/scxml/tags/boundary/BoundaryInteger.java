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

import org.finra.datagenerator.engine.scxml.tags.CustomTagExtension;
import org.finra.datagenerator.engine.scxml.tags.boundary.action.BoundaryActionNumeric;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @param <T> a generic type that represents a BoundaryTag
 */
public abstract class BoundaryInteger<T extends BoundaryActionNumeric> implements CustomTagExtension<T> {

    /**
     * @param action   an Action of the type handled by this class
     * @param minVALUE a String containing the minimum possible value
     * @param maxVALUE a String containing the maximum possible value
     * @param positive a boolean denoting positive or negative cases
     * @return a List containing the boundary values
     */
    public List<String> buildNumericData(BoundaryActionNumeric action, String minVALUE, String maxVALUE, boolean positive) {

        if (action.getClass().getCanonicalName() != null) {
            System.out.println(action.getClass().getCanonicalName());
        }

        BigInteger min = new BigInteger(action.getMin());
        BigInteger max = new BigInteger(action.getMax());
        List<String> values = new LinkedList<>();
        String nullString = "";

        if (min.compareTo(max) == 1) {
            BigInteger temp = new BigInteger(action.getMin());
            action.setMin(action.getMax());
            action.setMax(temp.toString());
            min = new BigInteger(action.getMin());
            max = new BigInteger(action.getMax());
        }

        if (min.compareTo(new BigInteger(minVALUE)) == -1) {
            action.setMin(minVALUE);
            min = new BigInteger(minVALUE);
        }

        if (max.compareTo(new BigInteger(maxVALUE)) == 1) {
            action.setMax(maxVALUE);
            max = new BigInteger(maxVALUE);
        }

        if (positive) {
            values.add(min.toString());
            positiveCase(min, max, values);
            values.add(max.toString());

            if (action.getNullable().equals("true")) {
                values.add(nullString);
            }

        } else {
            negativeCase(min, max, values);

            if (!action.getNullable().equals("true")) {
                values.add(nullString);
            }
        }

        if (min.compareTo(BigInteger.ZERO) == -1 && max.compareTo(BigInteger.ZERO) == 1
            && min.compareTo(BigInteger.ONE) != 0 && max.compareTo(new BigInteger("-1")) != 0) {
            values.add("0");
        }

        return values;
    }

    /**
     * @param min    a BigInteger containing the min defined by Action
     * @param max    a BigInteger containing the max defined by Action
     * @param values a list of boundary values
     * @return a list of BigIntegers with the boundary values -/+ one
     */
    public List<String> negativeCase(BigInteger min, BigInteger max, List<String> values) {
        BigInteger minMinusOne = min.subtract(BigInteger.ONE);
        BigInteger maxPlusOne = max.add(BigInteger.ONE);
        values.add(minMinusOne.toString());
        values.add(maxPlusOne.toString());
        return values;
    }

    /**
     * @param min    a BigInteger containing the min defined by Action
     * @param max    a BigInteger containing the max defined by Action
     * @param values a list of boundary values
     * @return a list of BigIntegers with the boundary values +/- one
     */
    public List<String> positiveCase(BigInteger min, BigInteger max, List<String> values) {
        BigInteger minPlusOne = min.add(BigInteger.ONE);
        BigInteger maxMinusOne = max.subtract(BigInteger.ONE);
        BigInteger mid = max.subtract(min).divide(new BigInteger("2"));
        values.add(minPlusOne.toString());
        values.add(mid.toString());
        values.add(maxMinusOne.toString());
        return values;
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