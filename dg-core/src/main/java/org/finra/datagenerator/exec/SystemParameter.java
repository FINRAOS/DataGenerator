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
package org.finra.datagenerator.exec;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * Reads system parameters converting human readable suffixes like M or G to six
 * or nine zeroes.
 *
 */
public final class SystemParameter {

    private SystemParameter() {

    }

    /**
     * Trims the string and expands right number suffixes, if the right suffix
     * is expandable, otherwise returns the original without expansion or
     * trimming.<br/>
     * <ul>
     * <li>k: kilo, adds 3 zeroes
     * <li>m or M: mega, adds 6 zeroes
     * <li>g or G: gega, adds 9 zeroes
     * <li>t or T: tera, adds 12 zeroes
     * </ul>
     *
     * @param param
     * @return a String containing the expanded form.
     */
    static String expandSuffixes(String param) {
        param = param.trim();
        if (param.endsWith("m") | param.endsWith("M")) {
            return param.substring(0, param.length() - 1) + Strings.repeat("0", 6);
        }
        if (param.endsWith("g") | param.endsWith("G")) {
            return param.substring(0, param.length() - 1) + Strings.repeat("0", 9);
        }
        if (param.endsWith("t") | param.endsWith("T")) {
            return param.substring(0, param.length() - 1) + Strings.repeat("0", 12);
        }
        return param;
    }

    /**
     * Converts a String parameter to an integer resolving any human readable
     * suffixes or returns the default value in case it was null.
     *
     * @param paramName the name of the parameter to get
     * @param defaultValue the default value of the parameter
     * @return an int containing the parameter value
     */
    public static int getInt(String paramName, int defaultValue) {
        String param = System.getProperty(paramName);
        if (param == null) {
            return defaultValue;
        }
        param = expandSuffixes(param);
        if (Ints.tryParse(param) != null) {
            return Integer.parseInt(param);
        }
        return defaultValue;
    }

    /**
     * Converts a String parameter to an long resolving any human readable
     * suffixes or returns the default value in case it was null.
     *
     * @param paramName the name of the parameter to get
     * @param defaultValue the default value of the parameter
     * @return a long containing the parameter value
     */
    public static long getLong(String paramName, long defaultValue) {
        String param = System.getProperty(paramName);
        if (param == null) {
            return defaultValue;
        }
        param = expandSuffixes(param);
        if (Longs.tryParse(param) != null) {
            return Long.parseLong(param);
        }
        return defaultValue;
    }

    /**
     * Converts a String parameter to an integer resolving any human readable
     * suffixes or returns the default value in case it was null. The default
     * value is a string and so can contain human redable suffixes as well.
     *
     * @param paramName the name of the parameter to get
     * @param defaultValue the default value of the parameter
     * @return a long containing the parameter value
     */
    public static long getLong(String paramName, String defaultValue) {
        String param = System.getProperty(paramName);
        if (param == null) {
            param = defaultValue;
        }
        param = expandSuffixes(param);
        if (Longs.tryParse(param) != null) {
            return Long.parseLong(param);
        } else {
            throw new RuntimeException("The parameter " + paramName + " value " + param + " is not parseable digital");
        }
    }

    /**
     * Gets a String parameter or returns the default value in case it was null.
     *
     * @param paramName the name of the parameter to get
     * @param defaultValue the default value of the parameter
     * @return a string containing the parameter value
     */
    public static String getString(String paramName, String defaultValue) {
        String param = System.getProperty(paramName);
        if (param == null) {
            return defaultValue;
        } else {
            return param;
        }
    }
}
