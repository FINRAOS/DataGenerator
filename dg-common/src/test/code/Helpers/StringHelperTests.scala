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

package Helpers

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

import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import Helpers.StringHelper.StringImplicits

/*
  println()
  "Hello world ".splitOnChar(' ', removeEmptyEntries=false).foreach(x => println(s"`$x`"))
  println()
  "***Hello***world***this***is***my***string***".splitOnString("***", removeEmptyEntries=false).foreach(x => println(s"`$x`"))
  println()
  "***Hello***world***this***is***my***string*********".splitOnString("***", removeEmptyEntries=false).foreach(x => println(s"`$x`"))
  println()
  "***Hello***world***this***is***my***string*********!!!".splitOnString("***", removeEmptyEntries=false).foreach(x => println(s"`$x`"))
 */

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
        assert(result.size == 1 && result(0) == str)
      }
    }
    "the string contain the delimiter, and removing empty entries" should {
      "split on the delimiter and contain no empty entries" in {
        val str = "Hello world "
        val delimiter = ' '
        val result = str.splitOnChar(delimiter, removeEmptyEntries=true)
        assert(result.size == 2 && result(0) == "Hello" && result(1) == "world")
      }
    }
  }
  "Splitting a string by a substring" when {
    "the string contains the substring before, after, and in middle, and removing empty entries" should {
      "split on the delimiter and contain no empty entries" in {
        val str = "***Hello***world***this***is***my***string***"
        val delimiter = "***"
        val result = str.splitOnString(delimiter, removeEmptyEntries=true)
        assert(result.size == 6 && result(0) == "Hello" && result(5) == "string")
      }
    }
    "the string contains the substring before, after, and in middle, with extra delimiters at end, and removing empty entries" should {
      "split on the delimiter and contain no empty entries" in {
        val str = "***Hello***world***this***is***my***string*********"
        val delimiter = "***"
        val result = str.splitOnString(delimiter, removeEmptyEntries=true)
        assert(result.size == 6 && result(0) == "Hello" && result(5) == "string")
      }
    }
    "the string contains the substring before, after, and in middle, with extra delimiters in middle, and removing empty entries" should {
      "split on the delimiter and contain no empty entries" in {
        val str = "***Hello***world***this***is***my***string*********!!!"
        val delimiter = "***"
        val result = str.splitOnString(delimiter, removeEmptyEntries=true)
        assert(result.size == 7 && result(0) == "Hello" && result(5) == "string" && result(6) == "!!!")
      }
    }
    "the string contains the substring before, after, and in middle, with extra delimiters in middle plus part of a delimiter (delimiter is same char repeated), and removing empty entries" should {
      "split on the delimiter and contain no empty entries" in {
        val str = "***Hello***world***this***is***my***string***********!!!"
        val delimiter = "***"
        val result = str.splitOnString(delimiter, removeEmptyEntries=true)
        assert(result.size == 8 && result(0) == "Hello" && result(5) == "string" && result(6) == "**" && result(7) == "!!!")
      }
    }
  }
  "Splitting a string on whitespace" when {
    "the string contains whitespace after and in middle" should {
      "split as if any mixed whitespace acts as one delimiter, and should contain no empty entries in result" in {
        val str = "Hello           world!           \\r"
        val result = str.splitOnWhitespace
        assert(result.size == 2 && result(0) == "Hello" && result(1) == "world!")
      }
    }
    "the string contains whitespace before, after, and in middle" should {
      "split as if any mixed whitespace acts as one delimiter, and should contain no empty entries in result" in {
        val str = "       Hello     \t\n      world!"
        val result = str.splitOnWhitespace
        assert(result.size == 2 && result(0) == "Hello" && result(1) == "world!")
      }
    }
  }
}