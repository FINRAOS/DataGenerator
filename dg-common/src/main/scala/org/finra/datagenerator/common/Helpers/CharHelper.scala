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
 * Helper methods for Char
 */
object CharHelper {
  /**
   * A-Z a-z
   */
  val alphabeticChars = ('a' to 'z') ++ ('A' to 'Z')

  /**
   * A-Z a-z 0-9 - _
   */
  val alphanumericChars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ ('_' to '_')

  /**
   * a-f 0-9
   */
  val hexCharsLowercase = ('a' to 'f') ++ ('0' to '9')

  /**
   * A-F 0-9
   */
  val hexCharsUppercase = ('A' to 'F') ++ ('0' to '9')

  /**
   * a-f A-F 0-9
   */
  val hexCharsAll = ('A' to 'F') ++ ('a' to 'f') ++ ('0' to '9')

  /**
   * 8, 9, a, b
   */
  val hexCharsBetween8AndB = ('8' to '9') ++ ('a' to 'b')

  /**
   * 0-9
   */
  val numericCharacters = ('0' to '9')

  implicit class CharExtensions(val char: Char) extends AnyVal {
    def isHexadecimal: Boolean = {
      hexCharsAll.contains(char)
    }

    def isNumeric: Boolean = {
      numericCharacters.contains(char)
    }
  }
}
