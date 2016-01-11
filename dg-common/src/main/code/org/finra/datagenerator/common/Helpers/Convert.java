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
    public static java.sql.Date toDate(String str) {
        return new StringExtensions(str).toDate();
    }

    public static java.sql.Date toDate(int dateInt) {
        return new IntExtensions(dateInt).toDate();
    }

    public static java.sql.Date toDate(java.util.Date dateTime) {
        return new DateExtensions(dateTime).toSqlDate();
    }

    public static java.sql.Date toDate(long dateLong) {
        return new LongExtensions(dateLong).toDate();
    }

    public static java.util.Date toDateTime(String str) {
        return new StringExtensions(str).toDateTime();
    }

    public static java.util.Date toDateTime(long dateTimeLong) {
        return new LongExtensions(dateTimeLong).toDateTime();
    }

    public static long toLong(java.util.Date dateTime) {
        return new DateExtensions(dateTime).toLong();
    }
}
