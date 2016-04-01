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
 * Numeric helper methods
 */
object NumericHelper {
  /**
   * Take a 48-bit number and a 16-bit number and concatenate them together as a Long.
   * @param num1 48-bit number
   * @param num2 16-bit number
   * @return Long composed of the two passed-in numbers
   */
  def concatenateTwoNumbers48BitAnd16Bit(num1: Long, num2: Short): Long = {
    require(num1 >= 0L  && num1 < 281474976710656L, s"num1 is $num1") // Max 2^48 - 1

    // We'd feel pretty dumb ORing against a negative number,
    // so make this basically an unsigned short (0 to 2^16 - 1)
    val modifiedNum2 =  num2 + 32768

    num1 << 16 | modifiedNum2
  }
}
