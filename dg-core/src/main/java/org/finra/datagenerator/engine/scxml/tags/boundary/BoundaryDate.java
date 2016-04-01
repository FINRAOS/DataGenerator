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
        boolean onlyBusinessDays = true;

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
        if (!action.getOnlyBusinessDays().equalsIgnoreCase("true")) {
            onlyBusinessDays = false;
        }
        if (positive) {
            return positiveCase(nullable, earliest, latest, onlyBusinessDays);
        } else {
            return negativeCase(nullable, earliest, latest);
        }
    }

    /**
     * Checks if the date is a holiday
     *
     * @param dateString the date
     * @return true if it is a holiday, false otherwise
     */
    public boolean isHoliday(String dateString) {
        boolean isHoliday = false;
        for (Holiday date : EquivalenceClassTransformer.HOLIDAYS) {
            if (convertToReadableDate(date.forYear(Integer.parseInt(dateString.substring(0, 4)))).equals(dateString)) {
                isHoliday = true;
            }
        }
        return isHoliday;
    }

    /**
     * Takes a date, and retrieves the next business day
     *
     * @param dateString the date
     * @param onlyBusinessDays only business days
     * @return a string containing the next business day
     */
    public String getNextDay(String dateString, boolean onlyBusinessDays) {
        DateTimeFormatter parser = ISODateTimeFormat.date();
        DateTime date = parser.parseDateTime(dateString).plusDays(1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.toDate());

        if (onlyBusinessDays) {
            if (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7
                || isHoliday(date.toString().substring(0, 10))) {
                return getNextDay(date.toString().substring(0, 10), true);
            } else {
                return parser.print(date);
            }
        } else {
            return parser.print(date);
        }
    }

    /**
     * Takes a date, and returns the previous business day
     *
     * @param dateString the date
     * @param onlyBusinessDays only business days
     * @return the previous business day
     */
    public String getPreviousDay(String dateString, boolean onlyBusinessDays) {
        DateTimeFormatter parser = ISODateTimeFormat.date();
        DateTime date = parser.parseDateTime(dateString).minusDays(1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.toDate());

        if (onlyBusinessDays) {
            if (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7
                || isHoliday(date.toString().substring(0, 10))) {
                return getPreviousDay(date.toString().substring(0, 10), true);
            } else {
                return parser.print(date);
            }
        } else {
            return parser.print(date);
        }
    }

    /**
     * @param isNullable isNullable
     * @param earliest   lower boundary date
     * @param latest     upper boundary date
     * @param onlyBusinessDays only business days
     * @return a list of boundary dates
     */
    public List<String> positiveCase(boolean isNullable, String earliest, String latest, boolean onlyBusinessDays) {
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
        String nextDay = getNextDay(earlyDate.toString().substring(0, 10), onlyBusinessDays);
        String prevDay = getPreviousDay(lateDate.toString().substring(0, 10), onlyBusinessDays);
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
     * Takes a String and converts it to a Date
     *
     * @param dateString the date
     * @return Date denoted by dateString
     */
    public Date toDate(String dateString) {
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
     * @return a holiday that falls between the dates
     */
    public String getRandomHoliday(String earliest, String latest) {
        String dateString = "";
        DateTimeFormatter parser = ISODateTimeFormat.date();
        DateTime earlyDate = parser.parseDateTime(earliest);
        DateTime lateDate = parser.parseDateTime(latest);
        List<Holiday> holidays = new LinkedList<>();

        int min = Integer.parseInt(earlyDate.toString().substring(0, 4));
        int max = Integer.parseInt(lateDate.toString().substring(0, 4));
        int range = max - min + 1;
        int randomYear = (int) (Math.random() * range) + min;

        for (Holiday s : EquivalenceClassTransformer.HOLIDAYS) {
            holidays.add(s);
        }
        Collections.shuffle(holidays);

        for (Holiday holiday : holidays) {
            dateString = convertToReadableDate(holiday.forYear(randomYear));
            if (toDate(dateString).after(toDate(earliest)) && toDate(dateString).before(toDate(latest))) {
                break;
            }
        }
        return dateString;
    }

    /**
     * Given a year, month, and day, find the number of occurrences of that day in the month
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     * @return the number of occurrences of the day in the month
     */
    public int numOccurrences(int year, int month, int day) {
        DateTimeFormatter parser = ISODateTimeFormat.date();
        DateTime date = parser.parseDateTime(year + "-" + month + "-" + "01");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.toDate());
        GregorianChronology calendar = GregorianChronology.getInstance();
        DateTimeField field = calendar.dayOfMonth();

        int days = 0;
        int count = 0;
        int num = field.getMaximumValue(new LocalDate(year, month, day, calendar));
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

    /**
     * Convert the holiday format from EquivalenceClassTransformer into a date format
     *
     * @param holiday the date
     * @return a date String in the format yyyy-MM-dd
     */
    public String convertToReadableDate(Holiday holiday) {
        DateTimeFormatter parser = ISODateTimeFormat.date();

        if (holiday.isInDateForm()) {
            String month = Integer.toString(holiday.getMonth()).length() < 2
                ? "0" + holiday.getMonth() : Integer.toString(holiday.getMonth());
            String day = Integer.toString(holiday.getDayOfMonth()).length() < 2
                ? "0" + holiday.getDayOfMonth() : Integer.toString(holiday.getDayOfMonth());
            return holiday.getYear() + "-" + month + "-" + day;
        } else {
            /*
             * 5 denotes the final occurrence of the day in the month. Need to find actual
             * number of occurrences
             */
            if (holiday.getOccurrence() == 5) {
                holiday.setOccurrence(numOccurrences(holiday.getYear(), holiday.getMonth(),
                    holiday.getDayOfWeek()));
            }

            DateTime date = parser.parseDateTime(holiday.getYear() + "-"
                + holiday.getMonth() + "-" + "01");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date.toDate());
            int count = 0;

            while (count < holiday.getOccurrence()) {
                if (calendar.get(Calendar.DAY_OF_WEEK) == holiday.getDayOfWeek()) {
                    count++;
                    if (count == holiday.getOccurrence()) {
                        break;
                    }
                }
                date = date.plusDays(1);
                calendar.setTime(date.toDate());
            }
            return date.toString().substring(0, 10);
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
