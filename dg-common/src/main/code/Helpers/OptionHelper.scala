/*
 * Copyright 2015 DataGenerator Contributors
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

package Helpers

import java.text.SimpleDateFormat

/**
 * Helper methods on Option
 */
object OptionHelper {

  /**
   * Implicit methods on an Option
   * @param option
   * @tparam T
   */
   implicit class OptionSerialization[T](private val option: Option[T]) {
    private val formatter = new SimpleDateFormat("yyyyMMdd")

    /**
     * Convert the option value to a string, or if undefined, to ""
     * @return
     */
    def toStringOrEmpty: String = {
      if (option.isEmpty) ""
      else if (option.get.isInstanceOf[java.sql.Date]) {
        formatter.format(option.get)
      }
      else option.get.toString
    }
  }
}
