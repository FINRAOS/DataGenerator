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
import org.finra.datagenerator.engine.scxml.tags.boundary.action.BoundaryActionDate;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @param <T>
 */
public abstract class BoundaryDate<T extends BoundaryActionDate> implements CustomTagExtension<T> {

    /**
     * @param action   an Action of the type handled by this class
     * @param positive a boolean denoting positive or negative cases
     * @return a list of Strings that contain the states
     */
    public List<String> setParameters(T action, boolean positive) {
        String earliest = action.getEarliest();
        String latest = action.getLatest();

        boolean nullable = true;
        if (!action.getNullable().equals("true")) {
            nullable = false;
        }
        if (earliest == null) {
            earliest = "1970-01-01";
        }
        if (latest == null) {
            Calendar currDate = Calendar.getInstance();
            latest = new SimpleDateFormat("yyyy-MM-dd").format(currDate.getTime());
        }
        if (positive) {
            return positiveCase(nullable, earliest, latest);
        } else {
            return negativeCase(nullable, earliest, latest);
        }
    }

    /**
     *
     * @return
     */
    public static String getNextBusinessDay(String dateString) {
        DateTimeFormatter parser = ISODateTimeFormat.date();
        DateTime date = parser.parseDateTime(dateString);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.toDate());

        if (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7) {
            while (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7) {
                date = date.plusDays(1);
                cal.setTime(date.toDate());
            }
            return parser.print(date);
        } else {
            return parser.print(date.plusDays(1));
        }
    }

    /**
     *
     * @return
     */
    public String getPreviousBusinessDay(String dateString) {
        DateTimeFormatter parser = ISODateTimeFormat.date();
        DateTime date = parser.parseDateTime(dateString);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.toDate());

        if (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7) {
            while (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7) {
                date = date.minusDays(1);
                cal.setTime(date.toDate());
            }
            return parser.print(date);
        } else {
            return parser.print(date.minusDays(1));
        }
    }

    /**
     * @param isNullable isNullable
     * @param earliest   lower boundary date
     * @param latest     upper boundary date
     * @return a list of boundary dates
     */
    public List<String> positiveCase(boolean isNullable, String earliest, String latest) {
        List<String> values = new LinkedList<>();

        if (earliest.equalsIgnoreCase(latest)) {
            values.add(earliest);
            if (isNullable) {
                values.add("");
            }
            return values;
        }
//
        DateTimeFormatter parser = ISODateTimeFormat.date();
        DateTime earlyDate = parser.parseDateTime(earliest);
        DateTime lateDate = parser.parseDateTime(latest);

        String earlyDay = parser.print(earlyDate);
        String nextDay = getNextBusinessDay(earlyDate.toString().substring(0, 10));
        String prevDay = getPreviousBusinessDay(lateDate.toString().substring(0, 10));
        String lateDay = parser.print(lateDate);

        values.add(earlyDay);
        values.add(nextDay);
        values.add(prevDay);
        values.add(lateDay);

        if (isNullable) {
            values.add("");
        }
        return values;
    }

    /**
     * @param isNullable isNullable
     * @param earliest   lower boundary date
     * @param latest     upper boundary date
     * @return a list of boundary dates
     */
    public List<String> negativeCase(boolean isNullable, String earliest, String latest) {
        List<String> values = new LinkedList<>();

        DateTimeFormatter parser = ISODateTimeFormat.date();
        DateTime earlyDate = parser.parseDateTime(earliest);
        DateTime lateDate = parser.parseDateTime(latest);

        String prevDay = parser.print(earlyDate.minusDays(1));
        String nextDay = parser.print(lateDate.plusDays(1));

        values.add(prevDay);
        values.add(nextDay);
        values.add(nextDay.substring(5, 7) + "-" + nextDay.substring(8, 10) + "-" + nextDay.substring(0, 4));
//        values.add(getRandomHoliday(toDate(earliest), toDate(latest)));

        if (!isNullable) {
            values.add("");
        }
        return values;
    }

    /**
     *
     * @param dateString
     * @return Date denoted by dateString
     */
    public static Date toDate(String dateString) {
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = df.parse(dateString);
        } catch (ParseException ex) {
            System.out.println(ex.fillInStackTrace());
        }
        return date;
    }

//    /**
//     *
//     * @return
//     */
//    public String getRandomHoliday(Date earliest, Date latest) {
//        String dateString = "";
//
//        for (String s : EquivalenceClassTransformer.HOLIDAYS) {
//            dateString = convertToReadableDate(s);
//            if (toDate(dateString).after(earliest) && toDate(dateString).before(latest)) {
//                break;
//            }
//        }
//        return dateString;
//    }

//    /**
//     *
//     * @param equivalenceDate
//     * @return
//     */
//    public String convertToReadableDate(String equivalenceDate) {
//        DateTimeFormatter parser = ISODateTimeFormat.date();
//        String year = parser.print(Calendar.getInstance().get(Calendar.YEAR));
//        String[] holiday = StringUtils.substringBetween(equivalenceDate, "(", ")").split(",");
//
//        if (holiday.length == 2) {
//            String month = holiday[0].length() > 1 ? holiday[0] : "0" + holiday[0];
//            String day = holiday[1].length() > 1 ? holiday[1] : "0" + holiday[1];
//            return year + "-" + month + "-" + day;
//        } else if (holiday.length == 3) {
//            return null;
//        } else {
//            throw new InvalidDateException("Invalid Date Format");
//        }
//    }

    /**
     * @param action            an Action of the type handled by this class
     * @param possibleStateList a current list of possible states produced so far from
     *                          expanding a model state
     * @param variableValue     a list storing the values
     * @return a list of Maps containing the cross product of all states
     */
    public List<Map<String, String>> returnStates(T action, List<Map<String, String>> possibleStateList, List<String> variableValue) {
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
