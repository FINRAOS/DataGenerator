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
 * Long implicit methods
 */
object LongHelper {
  implicit class LongImplicits(private val long: Long) {
    private val longDateFormatter = new SimpleDateFormat("yyyyMMddhhmmssSSS")

    /**
     * Converts a Long formatted as yyyyMMddhhmmssSSS to a java.util.Date.
     * @return java.util.Date formed from the Long timestring in yyyyMMddhhmmssSSS format
     */
    def toDateTime: java.util.Date= {
      longDateFormatter.parse(long.toString)
    }
  }
}
