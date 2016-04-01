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

import org.apache.commons.lang3.StringUtils;
import org.finra.datagenerator.consumer.EquivalenceClassTransformer;
import org.finra.datagenerator.engine.scxml.tags.CustomTagExtension;
import org.finra.datagenerator.engine.scxml.tags.boundary.action.BoundaryActionDate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.LocalDate;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
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
     * @param dateString
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
     * @param dateString
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
        values.add(getRandomHoliday(earliest, latest));

        if (!isNullable) {
            values.add("");
        }
        return values;
    }

    /**
     * Convert a String to Date
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

    /**
     * Grab random holiday from the equivalence class that falls between the two dates
     *
     * @param earliest the earliest date parameter as defined in the model
     * @param latest   the latest date parameter as defined in the model
     * @return
     */
    public String getRandomHoliday(String earliest, String latest) {
        String dateString = "";
        DateTimeFormatter parser = ISODateTimeFormat.date();
        DateTime earlyDate = parser.parseDateTime(earliest);
        DateTime lateDate = parser.parseDateTime(latest);
        List<String> holidays = new LinkedList<>();

        int min = Integer.parseInt(earlyDate.toString().substring(0, 4));
        int max = Integer.parseInt(lateDate.toString().substring(0, 4));

        int range = (max - min) + 1;
        int randomYear = (int) (Math.random() * range) + min;

        for (String s : EquivalenceClassTransformer.HOLIDAYS) {
            holidays.add(s);
        }
        Collections.shuffle(holidays);

        for (String s : holidays) {
            dateString = convertToReadableDate(s, Integer.toString(randomYear));
            if (toDate(dateString).after(toDate(earliest)) && toDate(dateString).before(toDate(latest))) {
                break;
            }
        }
        return dateString;
    }

    /**
     * Given a year, month, and day, find the number of occurrences of that day in the month
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static int numOccurrences(String year, int month, int day) {
        DateTimeFormatter parser = ISODateTimeFormat.date();
        DateTime date = parser.parseDateTime(year + "-" + month + "-" + "01");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.toDate());
        GregorianChronology calendar = GregorianChronology.getInstance();
        DateTimeField field = calendar.dayOfMonth();

        int days = 0;
        int count = 0;
        int num = field.getMaximumValue(new LocalDate(Integer.parseInt(year), month, day, calendar));
        while (days < num) {
            if (cal.get(Calendar.DAY_OF_WEEK) == day) {
                count++;
            }
            date = date.plusDays(1);
            cal.setTime(date.toDate());

            days++;
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.println(convertToReadableDate("MemorialDay(5,2,5)", "1999"));
    }

    /**
     * Convert the holiday format from EquivalenceClassTransformer into a date format
     *
     * @param equivalenceDate
     * @return a date String in the format yyyy-MM-dd
     */
    public static String convertToReadableDate(String equivalenceDate, String year) {
        DateTimeFormatter parser = ISODateTimeFormat.date();
        String[] params = StringUtils.substringBetween(equivalenceDate, "(", ")").split(",");

        if (params.length == 2) {
            String month = params[0].length() > 1 ? params[0] : "0" + params[0];
            String day = params[1].length() > 1 ? params[1] : "0" + params[1];
            return year + "-" + month + "-" + day;
        } else if (params.length == 3) {
            String month = params[0].length() > 1 ? params[0] : "0" + params[0];
            int dayOfWeek = Integer.parseInt(params[1]);
            int occurrence = Integer.parseInt(params[2]);
            /*
             * 5 denotes the final occurrence of the day in the month. Need to fiind actual
             * number of occurrences
             */
            if (occurrence == 5) {
                occurrence = numOccurrences(year, Integer.parseInt(params[0]), dayOfWeek);
            }
            DateTime date = parser.parseDateTime(year + "-" + month + "-" + "01");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date.toDate());
            int count = 0;

            while (count < occurrence) {
                if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                    count++;
                    if (count == occurrence) {
                        break;
                    }
                }
                date = date.plusDays(1);
                calendar.setTime(date.toDate());
            }
            return date.toString().substring(0, 10);
        } else {
            throw new InvalidDateException("Invalid Date Format");
        }
    }

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
