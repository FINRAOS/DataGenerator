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

/**
 * Integer implicit methods
 */
object IntHelper {
  implicit class IntImplicits(private val int: Int) {
    /**
     * Converts an Int formatted as yyyyMMdd to a java.sql.Date.
     * @return java.sql.Date formed from the Int in yyyyMMdd format
     */
    def toDate: java.sql.Date= {
      val str = int.toString
      java.sql.Date.valueOf(s"${str.substring(0,4)}-${str.substring(4,6)}-${str.substring(6,8)}")
    }
  }
}
