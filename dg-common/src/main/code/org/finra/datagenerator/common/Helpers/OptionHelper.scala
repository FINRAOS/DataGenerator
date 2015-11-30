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
object OptionHelper {

  private val formatter = new SimpleDateFormat("yyyyMMdd")

  /**
   * Implicit methods on an Option
   * @param option Option value to use for implicit class
   * @tparam T Type of option
   */
   implicit class OptionSerialization[T](val option: Option[T]) extends AnyVal {
    /**
     * Convert the option value to a string, or if undefined, to ""
     * @return String representing the option's value if a Some, else "" if a None
     */
    def toStringOrEmpty: String = {
      if (option.isEmpty) {
        ""
      } else if (option.get.isInstanceOf[java.sql.Date]) {
        formatter.format(option.get)
      }
      else {
        option.get.toString
      }
    }
  }

  /**
   * Implicit methods on an Option of String
   * @param option Option value to use for implicit class
   */
  implicit class StringOptionSerialization(val option: Option[String]) extends AnyVal {
    /**
     * Get the string, or if undefined, ""
     * @return String of the value if a Some, else "" if a None
     */
    def getOrEmpty: String = {
      option.getOrElse(new java.lang.String(""))
    }
  }
}
