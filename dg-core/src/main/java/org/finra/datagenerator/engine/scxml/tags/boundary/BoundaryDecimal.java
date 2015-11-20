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
import java.math.BigInteger;
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
        BigInteger maxLen, minLen;
        String[] length = action.getLength().split(",");
        String minLength = action.getMinLen();
        String maxLength = action.getMaxLen();
        String minimum = action.getMin();
        String maximum = action.getMax();
        BigInteger min, max;
        boolean minMaxLengthPresent = false;
        boolean nullable = true;
        BigInteger leadingDigits = new BigInteger(length[0]).subtract(new BigInteger(length[1]));
        BigInteger trailingDigits = new BigInteger(length[1]);
        boolean minMaxPresent = false;

        if (minLength != null) {
            minLen = new BigInteger(minLength);
            if (maxLength != null) {
                maxLen = new BigInteger(maxLength);
                minMaxLengthPresent = true;
            } else {
                maxLen = new BigInteger(length[0]);
            }
        } else {
            if (maxLength != null) {
                maxLen = new BigInteger(maxLength);
                minLen = null;
            } else {
                minLen = null;
                maxLen = new BigInteger(length[0]);
            }
        }
        if (!action.getNullable().equals("true")) {
            nullable = false;
        }
        if (minimum != null && maximum != null) {
            minMaxPresent = true;
        }
        if (positive) {
            if (minMaxPresent) {
                min = new BigInteger(action.getMin());
                max = new BigInteger(action.getMax());
                if (minMaxLengthPresent) {
                    return positiveCaseDecimal1(nullable, action.getName(), min, max, minLen, maxLen,
                        trailingDigits);
                } else {
                    return positiveCaseDecimal1(nullable, action.getName(), min, max,
                        new BigInteger(Integer.toString(min.toString().length())),
                        leadingDigits.add(trailingDigits), trailingDigits);
                }
            } else {
                if (minMaxLengthPresent) {
                    return positiveCaseDecimal2(nullable, minLen, maxLen,
                        trailingDigits);
                } else {
                    return positiveCaseDecimal3(nullable, leadingDigits, trailingDigits);
                }
            }
        } else {
            if (minMaxPresent) {
                min = new BigInteger(action.getMin());
                max = new BigInteger(action.getMax());
                if (minMaxLengthPresent) {
                    return negativeCaseDecimal1(nullable, action.getName(), min, max, minLen, maxLen);
                } else {
                    return negativeCaseDecimal1(nullable, action.getName(), min, max,
                        new BigInteger(Integer.toString(min.toString().length())),
                        leadingDigits.add(trailingDigits));
                }
            } else {
                if (minMaxLengthPresent) {
                    return negativeCaseDecimal2(nullable, minLen, maxLen, trailingDigits);
                } else {
                    return negativeCaseDecimal3(nullable, leadingDigits, trailingDigits);
                }
            }
        }
    }

    /**
     * @param nullable       nullable
     * @param name           name of variable
     * @param min            minimum value
     * @param max            maximum value
     * @param minLen         minimum length
     * @param maxLen         maximum length
     * @param numTrailing    number of trailing digits
     * @return a list of Strings that contain the boundary cases
     */
    public List<String> positiveCaseDecimal1(boolean nullable, String name, BigInteger min, BigInteger max,
                                             BigInteger minLen, BigInteger maxLen, BigInteger numTrailing) {
        StringBuilder decimalUpperBound = new StringBuilder();
        StringBuilder decimalLowerBound = new StringBuilder();
        StringBuilder decimalMid = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();

        try {
            if (new BigInteger(Integer.toString(min.toString().length())).compareTo(maxLen) == 1
                || new BigInteger(Integer.toString(max.toString().length())).compareTo(maxLen) == 1) {
                System.err.println("check length parameters for " + name);
                System.exit(0);
            }

            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);
            BigInteger mid = max.subtract(min).divide(new BigInteger("2"));

            if (new BigInteger(Integer.toString(min.add(BigInteger.ONE).toString().length())).compareTo(maxLen) == 0
                || new BigInteger(Integer.toString(min.add(BigInteger.ONE).toString().length())).compareTo(maxLen) == 1) {
                decimalLowerBound.append(min.add(BigInteger.ONE).toString());
                decimalLowerBound.append(".");
                int length = Integer.parseInt(Integer.toString(min.toString().length()));
                int length2 = Integer.parseInt(maxLen.toString());
                m.invoke(eq, decimalUpperBound, length2 - length);
                values.add(decimalLowerBound.toString());
                values.add(decimalLowerBound.toString());
            } else {
                m.invoke(eq, decimalLowerBound, minLen);
                values.add(decimalLowerBound.toString());
            }

            decimalUpperBound.append(new BigInteger(max.subtract(BigInteger.ONE).toString()));
            decimalUpperBound.append(".");
            int length = Integer.parseInt(Integer.toString(max.toString().length()));
            int length2 = Integer.parseInt(maxLen.toString());
            m.invoke(eq, decimalUpperBound, length2 - length);
            values.add(decimalLowerBound.toString());
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
     * @return a list of Strings that contain the boundary cases
     */
    public List<String> positiveCaseDecimal2(boolean nullable, BigInteger minLen, BigInteger maxLen, BigInteger numTrailing) {
        StringBuilder decimalUpperBound = new StringBuilder();
        StringBuilder decimalLowerBound = new StringBuilder();
        StringBuilder decimalMid = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, decimalLowerBound, minLen.subtract(numTrailing).toString());
            decimalLowerBound.append(".");
            m.invoke(eq, decimalLowerBound, numTrailing);
            values.add(decimalLowerBound.toString());

            m.invoke(eq, decimalLowerBound, maxLen.subtract(numTrailing).toString());
            decimalUpperBound.append(".");
            m.invoke(eq, decimalUpperBound, numTrailing);
            values.add(decimalUpperBound.toString());

            m.invoke(eq, decimalLowerBound, maxLen.subtract(minLen).toString());
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
     * @return a list of Strings that contain the boundary cases
     */
    public List<String> positiveCaseDecimal3(boolean nullable, BigInteger numLeading, BigInteger numTrailing) {
        StringBuilder decimalLowerBound = new StringBuilder();
        StringBuilder decimalUpper = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();
        int randomLeading = (int) (1 + Math.random() * Integer.parseInt(numLeading.subtract(BigInteger.ONE).toString()));
        int randomTrailing = (int) (1 + Math.random() * Integer.parseInt(numTrailing.subtract(BigInteger.ONE).toString()));

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, decimalLowerBound, Integer.parseInt(numTrailing.toString()));
            decimalLowerBound.append(".");
            m.invoke(eq, decimalLowerBound, Integer.parseInt(numTrailing.toString()));
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
     * @param name           name of variable
     * @param min            minimum value
     * @param max            maximum value
     * @param minLen         minimum length
     * @param maxLen         maximum length
     * @return a list of Strings that contain the boundary cases
     */
    public List<String> negativeCaseDecimal1(boolean nullable, String name, BigInteger min, BigInteger max, BigInteger minLen, BigInteger maxLen) {
        StringBuilder decimalUpperBound = new StringBuilder();
        StringBuilder decimalLowerBound = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();

        try {
            if (new BigInteger(Integer.toString(min.toString().length())).compareTo(maxLen) == 1
                || new BigInteger(Integer.toString(max.toString().length())).compareTo(maxLen) == 1) {
                System.err.println("check length parameters for " + name);
                System.exit(0);
            }

            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            if (new BigInteger(Integer.toString(min.subtract(BigInteger.ONE).toString().length())).compareTo(maxLen) == 0
                || new BigInteger(Integer.toString(min.subtract(BigInteger.ONE).toString().length())).compareTo(maxLen) == 1) {
                decimalLowerBound.append(min.subtract(BigInteger.ONE).toString());
                decimalLowerBound.append(".");
                int length = Integer.parseInt(Integer.toString(min.toString().length()));
                int length2 = Integer.parseInt(maxLen.toString());
                m.invoke(eq, decimalUpperBound, length2 - length);
                values.add(decimalLowerBound.toString());
            } else {
                m.invoke(eq, decimalLowerBound, Integer.parseInt(minLen.subtract(BigInteger.ONE).toString()));
                values.add(decimalLowerBound.toString());
            }

            decimalUpperBound.append(new BigInteger(max.add(BigInteger.ONE).toString()));
            decimalUpperBound.append(".");
            m.invoke(eq, decimalUpperBound, 1);
            int length = Integer.parseInt(Integer.toString(max.toString().length()));
            int length2 = Integer.parseInt(maxLen.toString());
            m.invoke(eq, decimalUpperBound, length2 - length);

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
     * @param nullable       nullable
     * @param minLen         minimum length
     * @param maxLen         maximum length
     * @param numTrailing    number of trailing digits
     * @return a list of Strings that contain the boundary cases
     */
    public List<String> negativeCaseDecimal2(boolean nullable, BigInteger minLen, BigInteger maxLen, BigInteger numTrailing) {
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

            m.invoke(eq, decimalLowerBound, Integer.parseInt(minLen.subtract(numTrailing).add(BigInteger.ONE).toString()));
            decimalLowerBound.append(".");
            m.invoke(eq, decimalLowerBound, numTrailing);
            values.add(decimalLowerBound.toString());

            m.invoke(eq, decimalLowerBound2, Integer.parseInt(minLen.subtract(numTrailing).toString()));
            decimalLowerBound2.append(".");
            m.invoke(eq, decimalLowerBound2, Integer.parseInt(numTrailing.add(BigInteger.ONE).toString()));
            values.add(decimalLowerBound2.toString());

            m.invoke(eq, decimalUpperBound, Integer.parseInt(maxLen.subtract(numTrailing).add(BigInteger.ONE).toString()));
            decimalUpperBound.append(".");
            m.invoke(eq, decimalUpperBound, Integer.parseInt(numTrailing.toString()));
            values.add(decimalUpperBound.toString());

            m.invoke(eq, decimalUpperBound2, Integer.parseInt(maxLen.subtract(numTrailing).toString()));
            decimalUpperBound2.append(".");
            m.invoke(eq, decimalUpperBound2, Integer.parseInt(numTrailing.add(BigInteger.ONE).toString()));
            values.add(decimalUpperBound2.toString());

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (!nullable) {
            values.add("");
        }
        return values;
    }

    /**
     * @param nullable       nullable
     * @param numLeading     number of leading digits
     * @param numTrailing    number of trailing digits
     * @return a list of Strings that contain the boundary cases
     */
    public List<String> negativeCaseDecimal3(boolean nullable, BigInteger numLeading, BigInteger numTrailing) {
        StringBuilder decimalLowerBound = new StringBuilder();
        StringBuilder decimalUpperBound = new StringBuilder();
        Method m;
        List<String> values = new LinkedList<>();
        EquivalenceClassTransformer eq = new EquivalenceClassTransformer();

        try {
            m = EquivalenceClassTransformer.class.getDeclaredMethod("digitSequence", StringBuilder.class, int.class);
            m.setAccessible(true);

            m.invoke(eq, decimalLowerBound, Integer.parseInt(numLeading.add(BigInteger.ONE).toString()));
            decimalLowerBound.append(".");
            m.invoke(eq, decimalLowerBound, Integer.parseInt(numTrailing.toString()));
            values.add(decimalLowerBound.toString());

            m.invoke(eq, decimalUpperBound, Integer.parseInt(numLeading.toString()));
            decimalUpperBound.append(".");
            m.invoke(eq, decimalUpperBound, Integer.parseInt(numTrailing.add(BigInteger.ONE).toString()));
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