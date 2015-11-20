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
     * @param action   an Action of the type handled by this class
     * @param positive a boolean denoting positive or negative cases
     * @return a list of Strings that contain the states
     */
    public List<String> buildDecimalData(T action, boolean positive) {

        String[] length = action.getLength().split(",");
        String minLen = action.getMinLen();
        String maxLen = action.getMaxLen();
        String min = action.getMin();
        String max = action.getMax();
        boolean minMaxLengthPresent = true;
        boolean nullable = true;
        int leadingDigits;
        int trailingDigits;
        boolean minMaxPresent = false;

        if (!action.getNullable().equals("true")) {
            nullable = false;
        }

        leadingDigits = Integer.parseInt(length[0]);
        trailingDigits = Integer.parseInt(length[1]);

        if (minLen != null && maxLen != null) { // minLen and maxLen presence overrides length
            minMaxLengthPresent = false;
        }

        if (min != null && max != null) {
            minMaxPresent = true;
        }

        if (positive) {
            if (minMaxPresent) {
                if (minMaxLengthPresent) {
                    return positiveCaseDecimal1(nullable, Integer.parseInt(min), Integer.parseInt(max), Integer.parseInt(minLen), Integer.parseInt(maxLen), trailingDigits);
                } else {
                    return positiveCaseDecimal1(nullable, Integer.parseInt(min), Integer.parseInt(max), 1, leadingDigits + trailingDigits, trailingDigits);
                }

            } else {
                if (minMaxLengthPresent) {
                    return positiveCaseDecimal2(nullable, Integer.parseInt(minLen), Integer.parseInt(maxLen), trailingDigits);
                } else {
                    return positiveCaseDecimal3(nullable, leadingDigits, trailingDigits);
                }
            }
        } else {
            if (minMaxPresent) {
                if (minMaxLengthPresent) {
                    return negativeCaseDecimal1(nullable, Integer.parseInt(min), Integer.parseInt(max), Integer.parseInt(minLen), Integer.parseInt(maxLen));
                } else {
                    return negativeCaseDecimal1(nullable, Integer.parseInt(min), Integer.parseInt(max), 1, leadingDigits + trailingDigits);
                }

            } else {
                if (minMaxLengthPresent) {
                    return negativeCaseDecimal2(nullable, Integer.parseInt(minLen), Integer.parseInt(maxLen), trailingDigits);
                } else {
                    return negativeCaseDecimal3(nullable, leadingDigits, trailingDigits);
                }
            }
        }
    }

    /**
     * @param nullable       nullable
     * @param min            minimum value
     * @param max            maximum value
     * @param minLen         minimum length
     * @param maxLen         maximum length
     * @param numTrailing    number of trailing digits
     * @return a list of Strings that contain the states
     */
    public List<String> positiveCaseDecimal1(boolean nullable, int min, int max, int minLen, int maxLen, int numTrailing) {
        StringBuilder decimalUpperBound = new StringBuilder();
        StringBuilder decimalLowerBound = new StringBuilder();
        StringBuilder decimalMid = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();

        try {
            if (Integer.toString(min).length() > maxLen || Integer.toString(max).length() > maxLen) {
                System.err.println("check length parameters");
                System.exit(0);
            }

            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            int mid = (max - min) / 2;

            if (Integer.toString(min + 1).length() >= maxLen) {
                decimalLowerBound.append(Integer.toString(min + 1));
                decimalLowerBound.append(".");
                while (decimalLowerBound.toString().length() < minLen) {
                    m.invoke(eq, decimalLowerBound, 1);
                }
                values.add(decimalLowerBound.toString());
            } else {
                m.invoke(eq, decimalLowerBound, minLen);
                values.add(decimalLowerBound.toString());
            }

            decimalUpperBound.append(Integer.toString(max - 1));
            decimalUpperBound.append(".");
            while (decimalLowerBound.toString().length() < minLen) {
                m.invoke(eq, decimalUpperBound, 1);
            }
            values.add(decimalUpperBound.toString());

            decimalMid.append(mid);
            decimalMid.append(".");
            m.invoke(eq, decimalMid, numTrailing);
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
     * @param minLen         minimum length
     * @param maxLen         maximum length
     * @param numTrailing    number of trailing digits
     * @return a list of Strings that contain the states
     */
    public List<String> positiveCaseDecimal2(boolean nullable, int minLen, int maxLen, int numTrailing) {
        StringBuilder decimalUpperBound = new StringBuilder();
        StringBuilder decimalLowerBound = new StringBuilder();
        StringBuilder decimalMid = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, decimalLowerBound, minLen - numTrailing);
            decimalLowerBound.append(".");
            m.invoke(eq, decimalLowerBound, numTrailing);
            values.add(decimalLowerBound.toString());

            m.invoke(eq, decimalUpperBound, maxLen - numTrailing);
            decimalUpperBound.append(".");
            m.invoke(eq, decimalUpperBound, numTrailing);
            values.add(decimalUpperBound.toString());

            m.invoke(eq, decimalMid, ((maxLen - minLen) / 2) - numTrailing);
            decimalMid.append(".");
            m.invoke(eq, decimalMid, numTrailing);
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
     * @param numLeading     number of leading digits
     * @param numTrailing    number of trailing digits
     * @return a list of Strings that contain the states
     */
    public List<String> positiveCaseDecimal3(boolean nullable, int numLeading, int numTrailing) {
        StringBuilder decimalLowerBound = new StringBuilder();
        StringBuilder decimalUpper = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        int randomLeading = (int) (1 + Math.random() * (numLeading - 1));
        int randomTrailing = (int) (1 + Math.random() * (numTrailing - 1));

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, decimalLowerBound, numTrailing);
            decimalLowerBound.append(".");
            m.invoke(eq, decimalLowerBound, numTrailing);
            values.add(decimalLowerBound.toString());

            m.invoke(eq, decimalUpper, randomLeading);
            decimalUpper.append(".");
            m.invoke(eq, decimalUpper, randomTrailing);
            values.add(decimalUpper.toString());

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
     * @return a list of Strings that contain the states
     */
    public List<String> negativeCaseDecimal1(boolean nullable, int min, int max, int minLen, int maxLen) {
        StringBuilder decimalUpperBound = new StringBuilder();
        StringBuilder decimalLowerBound = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();

        try {
            if (Integer.toString(min).length() > maxLen || Integer.toString(max).length() > maxLen) {
                System.err.println("check length parameters");
                System.exit(0);
            }

            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            if (Integer.toString(min - 1).length() >= maxLen) {
                decimalLowerBound.append(Integer.toString(min + 1));
                decimalLowerBound.append(".");
                while (decimalLowerBound.toString().length() < minLen) {
                    m.invoke(eq, decimalLowerBound, 1);
                }
                values.add(decimalLowerBound.toString());
            } else {
                m.invoke(eq, decimalLowerBound, minLen);
                values.add(decimalLowerBound.toString());
            }

            decimalUpperBound.append(Integer.toString(max + 1));
            decimalUpperBound.append(".");
            while (decimalLowerBound.toString().length() < minLen) {
                m.invoke(eq, decimalUpperBound, 1);
            }
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
     * @param nullable       nullable
     * @param minLen         minimum length
     * @param maxLen         maximum length
     * @param numTrailing    number of trailing digits
     * @return a list of Strings that contain the states
     */
    public List<String> negativeCaseDecimal2(boolean nullable, int minLen, int maxLen, int numTrailing) {
        StringBuilder decimalUpperBound = new StringBuilder();
        StringBuilder decimalUpperBound2 = new StringBuilder();
        StringBuilder decimalLowerBound = new StringBuilder();
        StringBuilder decimalLowerBound2 = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, decimalLowerBound, minLen - numTrailing + 1);
            decimalLowerBound.append(".");
            m.invoke(eq, decimalLowerBound, numTrailing);
            values.add(decimalLowerBound.toString());

            m.invoke(eq, decimalLowerBound2, minLen - numTrailing);
            decimalLowerBound2.append(".");
            m.invoke(eq, decimalLowerBound2, numTrailing + 1);
            values.add(decimalLowerBound2.toString());

            m.invoke(eq, decimalUpperBound, maxLen - numTrailing + 1);
            decimalUpperBound.append(".");
            m.invoke(eq, decimalUpperBound, numTrailing);
            values.add(decimalUpperBound.toString());

            m.invoke(eq, decimalUpperBound2, maxLen - numTrailing);
            decimalUpperBound2.append(".");
            m.invoke(eq, decimalUpperBound2, numTrailing + 1);
            values.add(decimalUpperBound2.toString());

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
     * @param numLeading     number of leading digits
     * @param numTrailing    number of trailing digits
     * @return a list of Strings that contain the states
     */
    public List<String> negativeCaseDecimal3(boolean nullable, int numLeading, int numTrailing) {
        StringBuilder decimalLowerBound = new StringBuilder();
        StringBuilder decimalUpperBound = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, decimalLowerBound, numLeading + 1);
            decimalLowerBound.append(".");
            m.invoke(eq, decimalLowerBound, numTrailing);
            values.add(decimalLowerBound.toString());

            m.invoke(eq, decimalUpperBound, numLeading);
            decimalUpperBound.append(".");
            m.invoke(eq, decimalUpperBound, numTrailing + 1);
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