//CHECKSTYLE:OFF
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
package org.finra.datagenerator.common.Helpers;

import org.finra.datagenerator.common.Helpers.DateHelper.DateExtensions;
import org.finra.datagenerator.common.Helpers.IntHelper.IntExtensions;
import org.finra.datagenerator.common.Helpers.LongHelper.LongExtensions;
import org.finra.datagenerator.common.Helpers.StringHelper.StringExtensions;

/**
 * Static conversion utilities, inspired by .NET's Convert class.
 * Intended to make the Scala implicit methods easier when calling from Java.
 * (Maybe in the future we should put the logic here and have the Scala implicit methods use these?)
 */
public class Convert {
    /**
     * Convert string to date
     * @param str string
     * @return Date
     */
    public static java.sql.Date toDate(String str) {
        return new StringExtensions(str).toDate();
    }

    /**
     * Convert integer to date
     * @param dateInt int
     * @return Date
     */
    public static java.sql.Date toDate(int dateInt) {
        return new IntExtensions(dateInt).toDate();
    }

    /**
     * Convert java.util.Date to java.sql.Date
     * @param dateTime java.util.Date
     * @return java.sql.Date
     */
    public static java.sql.Date toDate(java.util.Date dateTime) {
        return new DateExtensions(dateTime).toSqlDate();
    }

    /**
     * Convert Long to java.sql.Date
     * @param dateLong Long
     * @return java.sql.Date
     */
    public static java.sql.Date toDate(long dateLong) {
        return new LongExtensions(dateLong).toDate();
    }

    /**
     * Convert string to java.util.Date
     * @param str String
     * @return java.util.Date
     */
    public static java.util.Date toDateTime(String str) {
        return new StringExtensions(str).toDateTime();
    }

    /**
     * Convert Long to java.util.Date
     * @param dateTimeLong Long
     * @return java.util.Date
     */
    public static java.util.Date toDateTime(long dateTimeLong) {
        return new LongExtensions(dateTimeLong).toDateTime();
    }

    /**
     * Convert java.util.Date to Long
     * @param dateTime java.util.Date
     * @return Long
     */
    public static long toLong(java.util.Date dateTime) {
        return new DateExtensions(dateTime).toLong();
    }
}
