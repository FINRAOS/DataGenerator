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

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

/**
 * Extensions to String
 */
object StringHelper {

  /**
   * Implicit methods on String
   * @param str Value used for implicit class
   */
  implicit class StringImplicits(private val str: String) {
    /**
     * Whether or not the string is comprised entirely of digits
     * @return Whether or not string is numeric
     */
    def isNumeric: Boolean = {
      str.forall(Character.isDigit)
    }

    /**
     * Convert to an integer.
     * @return If parsable to an Int, return as Option(intVal), else None.
     */
    def toIntMaybe: Option[Int] = {
      try {
        Option(str.toInt)
      } catch {
        case e: NumberFormatException => None
      }
    }

    /**
     * Split into a sequence based on a specified separator character.
     * @param separator Character to split on
     * @param removeEmptyEntries Whether or not to remove empty strings from the result
     * @return Sequence of parts after splitting string
     */
    def splitOnChar(separator: Char, removeEmptyEntries: Boolean = true): Seq[String] = {
      splitOnChars(collection.immutable.Set[Char](separator), removeEmptyEntries = removeEmptyEntries)
    }

    /**
     * Split into a sequence based on specified separator characters.
     * @param separators Set of characters to split on
     * @param removeEmptyEntries Whether or not to remove empty strings from the result
     * @return Sequence of parts after splitting string
     */
    def splitOnChars(separators: Set[Char], removeEmptyEntries: Boolean = true): Seq[String] = {
      val splitResult = new collection.mutable.ArrayBuffer[String]()

      var startingIndexToAdd = 0
      var currentIndex = 0
      val stringLength = str.length
      while (currentIndex < stringLength) {
        if (separators.contains(str(currentIndex))) {
          if (startingIndexToAdd == currentIndex) {
            if (!removeEmptyEntries) {
              splitResult += ""
            }
          } else {
            splitResult += str.substring(startingIndexToAdd, currentIndex)
          }
          startingIndexToAdd = currentIndex + 1 // separator of size 1
        }
        currentIndex += 1
      }
      if (startingIndexToAdd < stringLength || !removeEmptyEntries) {
        splitResult += str.substring(startingIndexToAdd, stringLength)
      }
      splitResult
    }

    /**
     * Split into a sequence based on specififed seprator string
     * @param separator String to split on
     * @param removeEmptyEntries Whether or not to remove empty strings from the result
     * @return Sequence of parts after splitting string
     */
    def splitOnString(separator: String, removeEmptyEntries: Boolean = true): Seq[String] = {
      val splitResult = new collection.mutable.ArrayBuffer[String]()
      val separatorLength = separator.length
      var startingIndexToAdd = 0
      var currentIndex = 0
      val stringLength = str.length
      while (currentIndex <= stringLength - separatorLength) {
        if (str.substring(currentIndex, currentIndex + separatorLength).equals(separator)) {
          if (startingIndexToAdd == currentIndex) {
            if (!removeEmptyEntries) {
              splitResult += ""
            }
          } else {
            splitResult += str.substring(startingIndexToAdd, currentIndex)
          }
          startingIndexToAdd = currentIndex + separatorLength
          currentIndex += separatorLength
        } else {
          currentIndex += 1
        }
      }
      if (startingIndexToAdd < stringLength || !removeEmptyEntries) {
        splitResult += str.substring(startingIndexToAdd, stringLength)
      }
      splitResult
    }

    /**
     * Split on common whitespace characters.
     * @return Sequence of parts after splitting string
     */
    def splitOnWhitespace: Seq[String] = {
      splitOnChars(collection.immutable.Set[Char](' ', '\t', '\r', '\n'), removeEmptyEntries = true)
    }

    /**
     * Split on all whitespace characters.
     * @return Sequence of parts after splitting string
     */
    def splitOnWhitespaceIncludingFormFeedAndVerticalTab: Seq[String] = {
      // Not including these two chars in the default implementation because it's a minor performance hit for
      // characters that will almost never occur.
      splitOnChars(collection.immutable.Set[Char](' ', '\t', '\r', '\n', '\f', '\u000b'), removeEmptyEntries = true)
    }

    /**
     * Get the index of the Nth occurrence of a character in the string.
     * @param char Char to search for
     * @param n Number of occurrence, 1-based
     * @return Index of Nth occurrence of character
     */
    def indexOfNthOccurrence(char: Char, n: Int): Int = {
      var index = str.indexOf(char)
      var counter = n - 1
      while (counter > 0 && index != -1) {
        index = str.indexOf(char, index + 1)
        counter -= 1
      }
      index
    }

    /**
     * Truncate a string to a maximum length.
     * @param maxLength Maximum length to truncate the string to. This factors in the length of the suffix.
     * @param suffixIfTruncated Suffix to append to string if it was truncated.
     *                          Defaults to "..." (3 periods, not the single-char ellipsis, which if you want to pass in, is \u2026).
     * @return "hello world".truncateTo(5, "...") returns "he..."
     */
    def truncateTo(maxLength: Int, suffixIfTruncated: String = "..."): String = {
      if (str.length <= maxLength) {
        str
      } else {
        require(maxLength > suffixIfTruncated.length, "Suffix must not be longer than max length!")
        s"${str.substring(0, maxLength - suffixIfTruncated.length)}${suffixIfTruncated}"
      }
    }


    /**
     * Truncate a string to a maximum length, cutting from the middle and inserting a split token if necessary.
     * @param maxLength Maximum length string is allowed to be. If separator is nonempty, then string is truncated to maxLength - separator length.
     * @param splitToken Token to insert in middle if string is cut
     * @return String truncated from middle, with optional separator in middle where it was cut
     */
    def truncateFromMiddle(maxLength: Int, splitToken: String = "..."): String = {
      if (str.length <= maxLength) {
        str
      } else {
        require(splitToken.length < maxLength - 2, "Split token is too long (max maxLength - 2)!")
        val remainingLength = maxLength - splitToken.length
        val halfway = remainingLength / 2
        val left = remainingLength -  halfway
        s"${str.substring(0, halfway)}${splitToken}${str.substring(str.length - left, str.length)}"
      }
    }

    /**
     * Convert the string to an MD5.
     * @return MD5 string
     */
    def md5: String = {
      DatatypeConverter.printHexBinary(MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8")))
    }
  }
}
