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

/**
 * Class to represent Holidays
 */
public class Holiday {

    private String name;
    private int year;
    private int month;
    private int dayOfWeek;
    private int occurrence;
    private int dayOfMonth;
    private boolean isInDateForm;

    /**
     * Constructor for Holidays that are defined by an occurrence of a day in a month
     * @param name the name of the holiday
     * @param month the month the holiday is in
     * @param dayOfWeek the day of the week the holiday is on
     * @param occurrence the occurrence of the day of week in the month
     */
    public Holiday(final String name, final int month, final int dayOfWeek, final int occurrence) {
        this.name = name;
        this.month = month;
        this.dayOfWeek = dayOfWeek;
        this.occurrence = occurrence;
    }

    /**
     * Constructor for Holidays that are defined by an actual date in the year
     * @param name the name of the holiday
     * @param month the month the holiday is in
     * @param dayOfMonth the day of the month the holiday is on
     */
    public Holiday(final String name, final int month, final int dayOfMonth) {
        this.name = name;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        isInDateForm = true;
    }

    /**
     * Distinguishes between Holidays that are defined with a specific date, and
     * those that are defined by the occurrence of a specific day in the month
     * @return true if is defined as a date, false otherwise
     */
    public boolean isInDateForm() {
        return isInDateForm;
    }

    /**
     * Defines a year for the Holiday Object
     * @param year the year
     * @return the Holiday Object with year attribute set
     */
    public Holiday forYear(int year) {
        this.year = year;
        return this;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }
}
