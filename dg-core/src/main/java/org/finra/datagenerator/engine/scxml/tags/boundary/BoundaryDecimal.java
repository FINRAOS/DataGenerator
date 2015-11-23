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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @param <T> a generic type that represents a BoundaryTag
 */
public abstract class BoundaryDecimal<T extends BoundaryActionDecimal> implements CustomTagExtension<T> {

    /**
     * @param action   an Action of the type handled by this class
     * @param positive a boolean denoting positive or negative cases
     * @return a list of Strings that contain the states
     */
    public List<String> buildDecimalData(T action, boolean positive) {

        BigDecimal max, min;
        int minLen;
        int maxLen;
        String[] lengths = new String[2];
        boolean isNullable;
        String nullable = action.getNullable();

        if (nullable.equalsIgnoreCase("true")) {
            isNullable = true;
        } else {
            isNullable = false;
        }

        if (action.getLength() == null) {
            lengths[0] = "10";
            lengths[1] = "0";
        } else  {
            if (action.getLength().contains(",")) {
                lengths = action.getLength().split(",");
            } else {
                lengths[0] = action.getLength();
                if (Integer.parseInt(action.getLength()) > 38) {
                    lengths[0] = "38";
                }
                lengths[1] = "0";
            }
        }

        if (action.getMinLen() != null) {
            minLen = Integer.parseInt(action.getMinLen());
        } else {
            minLen = 0;
        }

        if (action.getMaxLen() != null) {
            maxLen = Integer.parseInt(action.getMaxLen());
        } else {
            maxLen = 0;
        }

        if (action.getMin() != null) {
            min = new BigDecimal(action.getMin());
        } else {
            int exp = Integer.parseInt(lengths[0]);
            min = BigDecimal.TEN.pow(exp);
            min = min.negate();
            min = min.add(BigDecimal.ONE);
        }

        if (action.getMax() != null) {
            max = new BigDecimal(action.getMax());
        } else {
            int exp = Integer.parseInt(lengths[0]);
            max = BigDecimal.TEN.pow(exp).subtract(BigDecimal.ONE);
        }

        if (positive) {
            return positiveCase(isNullable, min, max, minLen, maxLen, lengths);
        } else {
            return negativeCase(isNullable, min, max, minLen, maxLen, lengths);
        }
    }

    /**
     * @param nullable       nullable
     * @param min            minimum value
     * @param max            maximum value
     * @param minLen         minimum length
     * @param maxLen         maximum length
     * @param lengths        number of leading and trailing digits
     * @return a list of Strings that contain the boundary cases
     */
    public List<String> positiveCase(boolean nullable, BigDecimal min, BigDecimal max,
                                            int minLen, int maxLen, String[] lengths) {

        int exp = Integer.parseInt(lengths[0]);
        BigDecimal defaultMin = BigDecimal.TEN.pow(exp).subtract(BigDecimal.ONE);
        defaultMin = defaultMin.negate();
        BigDecimal defaultMax = BigDecimal.TEN.pow(exp).subtract(BigDecimal.ONE);
        boolean minMaxLenPresent = false;
        Method m;

        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        StringBuilder decimalUpperBound = new StringBuilder();
        StringBuilder decimalLowerBound = new StringBuilder();
        StringBuilder decimalMid = new StringBuilder();

        if (minLen != 0 && maxLen != 0) {
            minMaxLenPresent = true;
        }

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);
            if (minMaxLenPresent) {
                m.invoke(eq, decimalLowerBound, minLen);
                m.invoke(eq, decimalUpperBound, maxLen);
                m.invoke(eq, decimalMid, maxLen - minLen);
            } else {
                if (min.compareTo(defaultMin) == 0 && max.compareTo(defaultMax) == 0) {
                    decimalLowerBound.append(min.toString());
                    decimalUpperBound.append(max.toString());
                    decimalMid.append("0");
                } else {
                    decimalLowerBound.append(min.toString());
                    values.add(decimalLowerBound.toString());
                    decimalLowerBound.append(".");
                    if (min.toString().length() + Integer.parseInt(lengths[1]) <= Integer.parseInt(lengths[0])) {
                        m.invoke(eq, decimalLowerBound, Integer.parseInt(lengths[1]));
                    } else {
                        m.invoke(eq, decimalLowerBound, Integer.parseInt(lengths[0]) - min.toString().length());
                    }
                    decimalUpperBound.append(max.subtract(BigDecimal.ONE).toString());
                    values.add(decimalUpperBound.toString());
                    decimalUpperBound.append(".");
                    BigDecimal mid = max.subtract(min);
                    mid = mid.divide(new BigDecimal("2"));
                    decimalMid.append(mid.toString());
                    decimalMid.append(".");

                    if (max.toString().length() + Integer.parseInt(lengths[1]) <= Integer.parseInt(lengths[0])) {
                        m.invoke(eq, decimalUpperBound, Integer.parseInt(lengths[1]));
                        m.invoke(eq, decimalMid, Integer.parseInt(lengths[1]));
                    } else {
                        m.invoke(eq, decimalUpperBound, Integer.parseInt(lengths[0]) - max.toString().length());
                        m.invoke(eq, decimalMid, Integer.parseInt(lengths[0]) - max.toString().length());
                    }
                }
            }
            values.add(decimalLowerBound.toString());
            values.add(decimalUpperBound.toString());
            values.add(decimalMid.toString());

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (nullable) {
            values.add("");
        }
        return values;
    }

    /**
     * @param nullable       nullable
     * @param min            minimum value
     * @param max            maximum value
     * @param minLen         minimum length
     * @param maxLen         maximum length
     * @param lengths        number of leading and trailing digits
     * @return a list of Strings that contain the boundary cases
     */
    public List<String> negativeCase(boolean nullable, BigDecimal min, BigDecimal max,
                                     int minLen, int maxLen, String[] lengths) {

        int exp = Integer.parseInt(lengths[0]);
        BigDecimal defaultMin = BigDecimal.TEN.pow(exp).subtract(BigDecimal.ONE);
        defaultMin = defaultMin.negate();
        BigDecimal defaultMax = BigDecimal.TEN.pow(exp).subtract(BigDecimal.ONE);
        boolean minMaxLenPresent = false;
        Method m;

        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        StringBuilder decimalUpperBound = new StringBuilder();
        StringBuilder decimalLowerBound = new StringBuilder();

        if (minLen != 0 && maxLen != 0) {
            minMaxLenPresent = true;
        }

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);
            if (minMaxLenPresent) {
                m.invoke(eq, decimalLowerBound, minLen - 1);
                m.invoke(eq, decimalUpperBound, maxLen + 1);
            } else {
                if (min.compareTo(defaultMin) == 0 && max.compareTo(defaultMax) == 0) {
                    decimalLowerBound.append(min.toString());
                    decimalLowerBound.append("9");
                    decimalUpperBound.append(max.toString());
                    decimalUpperBound.append("9");
                } else {
                    decimalLowerBound.append(min.toString());
                    decimalLowerBound.append(".");
                    if (min.toString().length() + Integer.parseInt(lengths[1]) <= Integer.parseInt(lengths[0])) {
                        m.invoke(eq, decimalLowerBound, Integer.parseInt(lengths[1]) + 1);
                    } else {
                        m.invoke(eq, decimalLowerBound, Integer.parseInt(lengths[0]) - min.toString().length());
                    }
                    decimalUpperBound.append(max.toString());
                    decimalUpperBound.append("9");
                    decimalUpperBound.append(".");

                    if (max.toString().length() + Integer.parseInt(lengths[1]) <= Integer.parseInt(lengths[0])) {
                        m.invoke(eq, decimalUpperBound, Integer.parseInt(lengths[1]));
                    } else {
                        m.invoke(eq, decimalUpperBound, Integer.parseInt(lengths[0]) - max.toString().length());
                    }
                }
            }
            values.add(decimalLowerBound.toString());
            values.add(decimalUpperBound.toString());

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (nullable) {
            values.add("");
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
