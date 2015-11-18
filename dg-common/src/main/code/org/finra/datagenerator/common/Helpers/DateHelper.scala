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

package org.finra.datagenerator.common.Helpers

import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Helper methods on Option
 */
object DateHelper {

  /**
   * Implicit methods on a java.util.Date
   * @param date Date
   */
   implicit class DateImplicits(private val date: java.util.Date) {

    private val yyyyMM_formatter = new SimpleDateFormat("yyyyMM")
    private val yyyyMMdd_formatter = new SimpleDateFormat("yyyyMMdd")
    private val `yyyy-MM-dd_formatter` = new SimpleDateFormat("yyyy-MM-dd")
    private val timestamp_formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS")
    private val time_formatter = new SimpleDateFormat("hhmmssSS")

    /**
     * Convert to a date with no time information (java.sql.Date)
     * @return SQL Date (date with no time information)
     */
    def toSqlDate: java.sql.Date = {
      new java.sql.Date(date.getTime)
    }

    /**
     * Convert the date value to a string using the yyyyMM format.
     * @return String in yyyyMM format
     */
    def to_yyyyMM: String = {
      yyyyMM_formatter.format(date)
    }

    /**
     * Convert the date value to a string using the yyyyMMdd format.
     * @return String in yyyyMMdd format
     */
    def to_yyyyMMdd: String = {
      yyyyMMdd_formatter.format(date)
    }

    /**
     * Convert the date value to a string using the yyyy-MM-dd format.
     * @return String in yyyy-MM-dd format
     */
    def `to_yyyy-MM-dd`: String = { // scalastyle:ignore
      `yyyy-MM-dd_formatter`.format(date)
    }

    /**
     * Convert the date value to a string using the hhmmssSS format.
     * @return String in hhmmssSS format
     */
    def to_hhmmssSS: String = {
      time_formatter.format(date)
    }

    /**
     * Convert to a 17-digit Long (yyyyMMddhhmmssSSS)
     * @return Long in yyyyMMddhhmmssSSS timestamp format
     */
    def toLong: Long = {
      timestamp_formatter.format(date).toLong
    }

    /**
     * Add specified number of years to the date
     * @param yearsToAdd
     */
    def addYears(yearsToAdd: Int): java.util.Date = {
      val cal = Calendar.getInstance()
      cal.setTime(date)
      cal.add(Calendar.YEAR, yearsToAdd)
      cal.getTime()
    }

    /**
     * Add specified number of months to the date
     * @param monthsToAdd
     * @return
     */
    def addMonths(monthsToAdd: Int): java.util.Date = {
      val cal = Calendar.getInstance()
      cal.setTime(date)
      cal.add(Calendar.MONTH, monthsToAdd)
      cal.getTime()
    }

    /**
     * Add specified number of days to the date
     * @param daysToAdd
     * @return
     */
    def addDays(daysToAdd: Int): java.util.Date = {
      val cal = Calendar.getInstance()
      cal.setTime(date)
      cal.add(Calendar.DATE, daysToAdd)
      cal.getTime()
    }

    /**
     * Add specified number of hours to the date
     * @param hoursToAdd
     */
    def addHours(hoursToAdd: Int): java.util.Date = {
      val cal = Calendar.getInstance()
      cal.setTime(date)
      cal.add(Calendar.HOUR, hoursToAdd)
      cal.getTime()
    }

    /**
     * Adds specified number of minutes to the date
     * @param minutesToAdd
     */
    def addMinutes(minutesToAdd: Int): java.util.Date = {
      val cal = Calendar.getInstance()
      cal.setTime(date)
      cal.add(Calendar.MINUTE, minutesToAdd)
      cal.getTime()
    }

    /**
     * Adds specified number of seconds to the date
     * @param secondsToAdd
     */
    def addSeconds(secondsToAdd: Int): java.util.Date = {
      val cal = Calendar.getInstance()
      cal.setTime(date)
      cal.add(Calendar.SECOND, secondsToAdd)
      cal.getTime()
    }
  }
}

