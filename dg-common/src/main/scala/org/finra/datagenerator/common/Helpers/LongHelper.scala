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

import StringHelper.StringExtensions

/**
 * Long implicit methods
 */
object LongHelper {
  implicit class LongExtensions(val long: Long) extends AnyVal {
    /**
     * Converts a Long with at least 8 digits (yyyyMMdd), and optionally with [hh[mm[ss[S*]]]] parts, to a java.util.Date.
     * @return java.util.Date formed from the Long timestring
     */
    def toDateTime: java.util.Date = {
      long.toString.toDateTime
    }

    /**
     * Converts a Long with at least 8 digits (yyyyMMdd), and optionally with [hh[mm[ss[S*]]]] parts, to a java.sql.Date.
     * @return java.sql.Date (no time part) formed from the Long timestring
     */
    def toDate: java.sql.Date = {
      long.toString.toDate
    }
  }
}
