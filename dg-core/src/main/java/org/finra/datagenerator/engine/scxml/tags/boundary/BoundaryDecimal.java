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
    public List<String> setParameters(T action, boolean positive) {

        BigDecimal max, min;
        int minLen;
        int maxLen;
        int[] lengths = new int[2];
        boolean isNullable = true;
        String nullable = action.getNullable();

        if (!nullable.equalsIgnoreCase("true")) {
            isNullable = false;
        }

        if (action.getLength() == null) {
            lengths[0] = Integer.parseInt("10");
            lengths[1] = Integer.parseInt("0");
        } else {
            if (action.getLength().contains(",")) {
                lengths[0] = Integer.parseInt(action.getLength().split(",")[0]);
                lengths[1] = Integer.parseInt(action.getLength().split(",")[1]);
            } else {
                lengths[0] = Integer.parseInt(action.getLength());
                if (Integer.parseInt(action.getLength()) > 38) {
                    lengths[0] = Integer.parseInt("38");
                }
                lengths[1] = Integer.parseInt("0");
            }
        }
        if (action.getMinLen() != null) {
            if (Integer.parseInt(action.getMinLen()) <= lengths[0]) {
                minLen = Integer.parseInt(action.getMinLen());
            } else {
                minLen = lengths[0];
            }
        } else {
            minLen = -1;
        }
        if (action.getMaxLen() != null) {
            if (Integer.parseInt(action.getMaxLen()) <= lengths[0]) {
                maxLen = Integer.parseInt(action.getMaxLen());
            } else {
                maxLen = lengths[0];
            }
        } else {
            maxLen = -1;
        }
        int exp = lengths[0];
        if (action.getMin() != null) {
            min = new BigDecimal(action.getMin());
        } else {
            min = BigDecimal.TEN.pow(exp);
            min = min.negate();
            min = min.add(BigDecimal.ONE);
        }

        if (action.getMax() != null) {
            max = new BigDecimal(action.getMax());
        } else {
            max = BigDecimal.TEN.pow(exp);
            max = max.subtract(BigDecimal.ONE);
        }

        if (positive) {
            return positive(isNullable, min, max, minLen, maxLen, lengths);
        } else {
            return negative(isNullable, min, max, minLen, maxLen, lengths);
        }
    }

    /**
     * @param nullable nullable
     * @param min      minimum value
     * @param max      maximum value
     * @param minLen   minimum length
     * @param maxLen   maximum length
     * @param lengths  number of leading and trailing digits
     * @return a list of Strings that contain the boundary cases
     */
    public List<String> negative(boolean nullable, BigDecimal min, BigDecimal max,
                                 int minLen, int maxLen, int[] lengths) {
        boolean minMaxLenPresent = true;
        boolean minMaxPresent = true;
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        List<String> values = new LinkedList<>();
        Method m;
        StringBuilder decimalUpperBound = new StringBuilder();
        StringBuilder decimalLowerBound = new StringBuilder();

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            if (minLen == -1 && maxLen == -1) {
                minMaxLenPresent = false;
            }
            if (minLen == -1) {
                minLen = min.toString().length();
            }
            if (maxLen == -1) {
                maxLen = lengths[0];
            }
            int exp = lengths[0];
            BigDecimal defaultMin = BigDecimal.TEN.pow(exp);
            defaultMin = defaultMin.negate();
            defaultMin = defaultMin.add(BigDecimal.ONE);

            BigDecimal defaultMax = BigDecimal.TEN.pow(exp);
            defaultMax = defaultMax.subtract(BigDecimal.ONE);

            if (min.compareTo(defaultMin) == 0 && max.compareTo(defaultMax) == 0) {
                minMaxPresent = false;
            }
            if (minMaxLenPresent && !minMaxPresent) {
                m.invoke(eq, decimalLowerBound, minLen - 1);
                m.invoke(eq, decimalUpperBound, maxLen + 1);

            } else {
                decimalLowerBound.append(constructData(min.subtract(BigDecimal.ONE), minLen - 1, maxLen + 1, lengths, false));
                decimalUpperBound.append(constructData(max.add(BigDecimal.ONE), minLen - 1, maxLen + 1, lengths, true));
            }
            values.add(decimalLowerBound.toString());
            values.add(decimalUpperBound.toString());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (!nullable) {
            values.add("");
        }
        return values;
    }

    /**
     * @param nullable nullable
     * @param min      minimum value
     * @param max      maximum value
     * @param minLen   minimum length
     * @param maxLen   maximum length
     * @param lengths  number of leading and trailing digits
     * @return a list of Strings that contain the boundary cases
     */
    public List<String> positive(boolean nullable, BigDecimal min, BigDecimal max,
                                 int minLen, int maxLen, int[] lengths) {
        boolean minMaxLenPresent = true;
        boolean minMaxPresent = true;
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        List<String> values = new LinkedList<>();
        Method m;
        StringBuilder decimalUpperBound = new StringBuilder();
        StringBuilder decimalLowerBound = new StringBuilder();
        StringBuilder decimalMid = new StringBuilder();
        BigDecimal mid;

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            if (minLen == -1 && maxLen == -1) {
                minMaxLenPresent = false;
            }
            if (minLen == -1) {
                minLen = min.toString().length();
                if (Character.toString(min.toString().charAt(0)).equals("-")) {
                    minLen = min.toString().length() - 1;
                }
                if (maxLen != -1) {
                    minMaxLenPresent = false;
                }
            }
            if (maxLen == -1) {
                maxLen = lengths[0];
                if (minLen != -1) {
                    minMaxLenPresent = false;
                }
            }
            int exp = lengths[0];

            BigDecimal defaultMin = BigDecimal.TEN.pow(exp);
            defaultMin = defaultMin.negate();
            defaultMin = defaultMin.add(BigDecimal.ONE);

            BigDecimal defaultMax = BigDecimal.TEN.pow(exp);
            defaultMax = defaultMax.subtract(BigDecimal.ONE);

            if (min.compareTo(defaultMin) == 0 && max.compareTo(defaultMax) == 0) {
                minMaxPresent = false;
            }
            if (minMaxLenPresent && !minMaxPresent) {
                m.invoke(eq, decimalLowerBound, minLen);
                m.invoke(eq, decimalUpperBound, maxLen);
                m.invoke(eq, decimalMid, (maxLen - minLen) / 2 + minLen);

            } else {
                mid = max.subtract(min);
                mid = mid.divide(new BigDecimal("2"));
                mid = mid.add(min);

                decimalLowerBound.append(constructData(min, minLen, maxLen, lengths, false));
                decimalUpperBound.append(constructData(max, minLen, maxLen, lengths, true));
                decimalMid.append(constructData(mid, minLen, maxLen, lengths, false));

                if (min.compareTo(BigDecimal.ZERO) == -1 && max.compareTo(BigDecimal.ZERO) == 1
                    && !values.contains("0")) {
                    values.add("0");
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
     * @param boundary      the min or max value
     * @param minLen        the min Length
     * @param maxLen        the max Length
     * @param lengths       the length parameters for decimal field
     * @param upperBound    boolean for upperBound bound
     * @return the number of trailing digits to append to the value
     */
    public String constructData(BigDecimal boundary, int minLen, int maxLen, int[] lengths, boolean upperBound) {
        Method m;
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        StringBuilder value = new StringBuilder();
        int trailing;
        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            if (upperBound) {
                if (boundary.toString().length() > maxLen) {
                    value.append(boundary.toString().substring(0, maxLen - 1));
                    return value.toString();
                }

                if (boundary.toString().length() + lengths[1] <= maxLen) {
                    trailing = lengths[1];
                } else {
                    if (maxLen - boundary.toString().length() <= lengths[1]) {
                        trailing = maxLen - boundary.toString().length();
                    } else {
                        trailing = lengths[1] - boundary.toString().length();
                    }
                }
                if (trailing > 0) {
                    value.append(boundary.subtract(BigDecimal.ONE));
                } else {
                    value.append(boundary);
                }

            } else {
                if (boundary.toString().length() > minLen) {
                    if (Character.toString(boundary.toString().charAt(0)).equals("-")) {
                        value.append(boundary.toString().substring(0, minLen + 1));
                    } else {
                        value.append(boundary.toString().substring(0, minLen));
                    }
                    return value.toString();
                }

                if (boundary.toString().length() + lengths[1] <= minLen) {
                    trailing = lengths[1];
                } else {
                    if (minLen - boundary.toString().length() <= lengths[1]) {
                        trailing = minLen - boundary.toString().length();
                    } else {
                        trailing = lengths[1] - boundary.toString().length();
                    }
                }
                value.append(boundary);
            }
            if (trailing > 0) {
                value.append(".");
            }
            m.invoke(eq, value, trailing);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return value.toString();
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
