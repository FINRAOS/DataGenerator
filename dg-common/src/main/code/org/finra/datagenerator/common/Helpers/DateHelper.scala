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

/**
 * Helper methods on Option
 */
object DateHelper {

  /**
   * Implicit methods on a java.util.Date
   * @param date Date
   */
   implicit class DateImplicits(private val date: java.util.Date) {

    private val yyyymmdd_formatter = new SimpleDateFormat("yyyyMMdd")
    private val timestamp_formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS")

    /**
     * Convert the date value to a string using the YYYYmmDD format.
     * @return String in YYYYmmDD format
     */
    def toYYYYmmDD: String = {
      yyyymmdd_formatter.format(date)
    }

    /**
     * Convert to a 17-digit Long (yyyyMMddhhmmssSSS)
     * @return Long in yyyyMMddhhmmssSSS timestamp format
     */
    def toLong: Long = {
      timestamp_formatter.format(date).toLong
    }
  }
}
