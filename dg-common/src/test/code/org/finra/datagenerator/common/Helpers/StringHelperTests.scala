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

import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.finra.datagenerator.common.Helpers.StringHelper.StringImplicits
import org.finra.datagenerator.common.Helpers.CharHelper.CharImplicits

/**
 * Unit tests for RetryHelper
 */
@RunWith(classOf[JUnitRunner])
class StringHelperTests extends WordSpec {
  "Splitting a string by a character" when {
    "the string does not contain the delimiter" should {
      "produce the same string as a single-element collection" in {
        val str = "Hi!"
        val delimiter = ','
        val result = str.splitOnChar(delimiter)
        assert(result.size == 1 && result.head == str)
      }
    }
    "the string contains the delimiter at the end, and not removing empty entries" should {
      "contain an empty entry at the end" in {
        val str = "Hello world "
        val delimiter = ' '
        val result = str.splitOnChar(delimiter, removeEmptyEntries = false)
        assert(result.size == 3 && result.head == "Hello" && result(1) == "world" && result(2).isEmpty)
      }
    }
    "the string contain the delimiter" should {
      "split on the delimiter and contain no empty entries" in {
        val str = "Hello world "
        val delimiter = ' '
        val result = str.splitOnChar(delimiter, removeEmptyEntries = true)
        assert(result.size == 2 && result.head == "Hello" && result(1) == "world")
      }
    }
  }
  "Splitting a string by a substring" when {
    "the string contains the substring before, after, and in middle, and not removing empty entries" should {
      "split on the delimiter and contain an empty entry on each end" in {
        val str = "***Hello***world***this***is***my***string***"
        val delimiter = "***"
        val result = str.splitOnString(delimiter, removeEmptyEntries = false)
        assert(result.size == 8 && result(1) == "Hello" && result(6) == "string" && result.head.isEmpty && result(7).isEmpty)
      }
    }
    "the string contains the substring before, multiple times after, and in middle, and not removing empty entries" should {
      "split on the delimiter and contain an empty entry at beginning and multiple empties at end" in {
        val str = "***Hello***world***this***is***my***string*********"
        val delimiter = "***"
        val result = str.splitOnString(delimiter, removeEmptyEntries = false)
        assert(result.size == 10 && result(1) == "Hello" && result(6) == "string" && result.head.isEmpty && result(7).isEmpty
          && result(8).isEmpty && result(9).isEmpty)
      }
    }
    "the string contains the substring before and after, and multiple times in middle, and not removing empty entries" should {
      "split on the delimiter and contain an empty entry at beginning and multiple empties in middle" in {
        val str = "***Hello***world***this***is***my***string*********!!!"
        val delimiter = "***"
        val result = str.splitOnString(delimiter, removeEmptyEntries = false)
        assert(result.size == 10 && result(1) == "Hello" && result(6) == "string" && result.head.isEmpty && result(7).isEmpty
          && result(8).isEmpty && result(9) == "!!!")
      }
    }
    "the string contains the substring before, after, and in middle" should {
      "split on the delimiter and contain no empty entries" in {
        val str = "***Hello***world***this***is***my***string***"
        val delimiter = "***"
        val result = str.splitOnString(delimiter, removeEmptyEntries = true)
        assert(result.size == 6 && result.head == "Hello" && result(5) == "string")
      }
    }
    "the string contains the substring before, after, and in middle, with extra delimiters at end" should {
      "split on the delimiter and contain no empty entries" in {
        val str = "***Hello***world***this***is***my***string*********"
        val delimiter = "***"
        val result = str.splitOnString(delimiter, removeEmptyEntries = true)
        assert(result.size == 6 && result.head == "Hello" && result(5) == "string")
      }
    }
    "the string contains the substring before, after, and in middle, with extra delimiters in middle" should {
      "split on the delimiter and contain no empty entries" in {
        val str = "***Hello***world***this***is***my***string*********!!!"
        val delimiter = "***"
        val result = str.splitOnString(delimiter, removeEmptyEntries = true)
        assert(result.size == 7 && result.head == "Hello" && result(5) == "string" && result(6) == "!!!")
      }
    }
    "the string contains the substring before, after, and in middle, with extra delimiters in middle plus part of a delimiter" +
      " (delimiter is same char repeated)" should {
      "split on the delimiter and contain no empty entries, and the delimiter should be matched before the parital delimiter" in {
        val str = "***Hello***world***this***is***my***string***********!!!"
        val delimiter = "***"
        val result = str.splitOnString(delimiter, removeEmptyEntries = true)
        assert(result.size == 7 && result.head == "Hello" && result(5) == "string" && result(6) == "**!!!")
      }
    }
  }
  "Splitting a string on whitespace" when {
    "the string contains whitespace after and in middle" should {
      "split as if any mixed whitespace acts as one delimiter, and should contain no empty entries in result" in {
        val str = "Hello           world!\\r           \r"
        val result = str.splitOnWhitespace
        assert(result.size == 2 && result.head == "Hello" && result(1) == "world!\\r")
        assert(result == str.splitOnWhitespaceIncludingFormFeedAndVerticalTab)
      }
    }
    "the string contains whitespace before, after, and in middle" should {
      "split as if any mixed whitespace acts as one delimiter, and should contain no empty entries in result" in {
        val str = "       Hello     \t\n      world!"
        val result = str.splitOnWhitespace
        assert(result.size == 2 && result.head == "Hello" && result(1) == "world!")
        assert(result == str.splitOnWhitespaceIncludingFormFeedAndVerticalTab)
      }
    }
  }
  "Splitting a string on ALL whitespace" should {
    "include form feed and vertical tab characters as allowable whitespace delimiters" in {
      val str = "    \f   Hello     \t\u000b\n      world!"
      val result = str.splitOnWhitespaceIncludingFormFeedAndVerticalTab
      assert(result.size == 2 && result.head == "Hello" && result(1) == "world!")
      assert(result != str.splitOnWhitespace)
    }
  }
  "Checking if a string is numeric" when {
    "the string only contains digits" should {
      "return true" in {
        assert("012345678953625576524545442".isNumeric)
      }
    }
    "the string contains a period" should {
      "return false" in {
        assert(!"012345678953625576524545.442".isNumeric)
      }
      "the string contains alphabetic characters" should {
        "return false" in {
          assert(!"Hello world!".isNumeric)
        }
      }
    }
  }
  "Converting a string to an int" when {
    "The string is alphabetic" should {
      "return None" in {
        assert("Hello world!".toIntMaybe == None)
      }
    }
    "The string is an int" should {
      "return Option(intVal)" in {
        assert("-70000".toIntMaybe == Option(-70000))
      }
    }
    "The string is the max size for an int" should {
      "return Option(intVal)" in {
        assert("2147483647".toIntMaybe == Option(2147483647))
      }
      "The string is a long, but too big for an int" should {
        "return None" in {
          assert("2147483648".toIntMaybe == None)
        }
      }
    }
  }
  "Index of Nth occurrence of a char" when {
    "String is empty" should {
      "Return -1" in {
        assert("".indexOfNthOccurrence('\0', 1) == -1)
        assert("".indexOfNthOccurrence(' ', 1) == -1)
      }
    }
    "String does not contain the char" should {
      "Return -1" in {
        assert("1234567".indexOfNthOccurrence('a', 5) == -1)
      }
    }
    "String only contains the char n-1 times" should {
      "Return -1" in {
        assert("123123123".indexOfNthOccurrence('1', 4) == -1)
      }
    }
    "String contains the char n times" should {
      "Return the position" in {
        assert("abc1231 \n23123".indexOfNthOccurrence('1', 3) == 11)
      }
    }
  }
  "Truncating a string" when {
    "The string is at or under the max length" should {
      "Return the string" in {
        assert("123".truncateTo(3) == "123")
        assert("123".truncateTo(4) == "123")
      }
    }
    "The string exceeds the max length" should {
      "Return the string truncated, plus the default suffix" in {
        assert("Hello world!".truncateTo(10) == "Hello w...")
      }
    }
    "The string exceeds the max length, with a suffix specified" should {
      "Return the string truncated, plus the specified suffix" in {
        assert("Hello world!".truncateTo(10, suffixIfTruncated = "!!!!!") == "Hello!!!!!")
      }
    }
  }
  "Truncating a string from the middle" when {
    "The string is at or under the max length" should {
      "Return the string" in {
        assert("123".truncateFromMiddle(3) == "123")
        assert("123".truncateFromMiddle(4) == "123")
      }
    }
    "The string exceeds the max length" should {
      "Return the string truncated, plus the default split token" in {
        assert("Hello world!".truncateFromMiddle(10) == "Hel...rld!")
      }
    }
    "The string exceeds the max length, with a suffix specified" should {
      "Return the string truncated, plus the specified suffix" in {
        assert("Hello world!".truncateFromMiddle(11, splitToken = "#") == "Hello#orld!")
      }
    }
  }
  "Converting a string to MD5" when {
    "Repeated multiple times" should {
      "Return the same result, in the correct format, unique per string" in {
        val result = "hello world".md5
        assert("hello world".md5 == result)
        assert(result.length == 32)
        assert(result.forall(_.isHexadecimal))
        val result2 = "".md5
        assert("".md5 == result2)
        assert(result != result2)
      }
    }
  }
}
