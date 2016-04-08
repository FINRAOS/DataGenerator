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

import org.finra.datagenerator.common.Helpers.{RandomHelper, RetryHelper}
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner

/**
 * Unit tests for RetryHelper
 */
@RunWith(classOf[JUnitRunner])
class RetryHelperTests extends WordSpec {
  "Retrying a method" when {
    "the method always throws an exception that we support retrying" should {
      "retry and eventually fail, and handling code should execute when retrying" in {
        var i = 0
        var flag = false
        var exceptionThrown = false

        try {
          RetryHelper.retry(200, Seq(classOf[IllegalArgumentException]))({
            i += 1
            throw new IllegalArgumentException()
          })(_ => {
            flag = true
          })
        } catch {
          case ex: IllegalArgumentException =>
            assert(flag, "Handling code did not execute properly!")
            exceptionThrown = true
        }
        assert(i == 200, "Retry count was not correct!")
        assert(exceptionThrown, "Expected exception was not thrown!")
      }
    }
    "the method sometimes throws an exception that we support retrying" should {
      "retry and eventually succeed, and handling code should execute when retrying" in {
        var i = 0
        var flag = false

        RetryHelper.retry(200, Seq(classOf[IllegalArgumentException]))({
          i += 1
          val thisTryShouldFail =
            if (i == 1) {
              true
            } else if (i < 200) {
              RandomHelper.evaluateProbability(0.9)
            } else {
              false
            }
          if (thisTryShouldFail) {
            throw new IllegalArgumentException()
          }
        })(_ => flag = true)
        assert(i <= 200, "Retry count was not correct!")
        assert(flag, "Handling code did not execute properly!")
      }
    }
    "the method sometimes throws an exception that we do NOT support retrying" should {
      "fail, with the same exception, and handling code should not execute" in {
        var i = 0
        var flag = false
        var exceptionThrown = false

        try {
          RetryHelper.retry(200, Seq(classOf[IllegalArgumentException]))({
            i += 1
            val thisTryShouldFail =
              if (i == 1) {
                true
              } else if (i < 200) {
                RandomHelper.evaluateProbability(0.9)
              } else {
                false
              }
            if (thisTryShouldFail) {
              throw new NullPointerException()
            }
          })(_ => flag = true)
        } catch {
          case ex: NullPointerException =>
            assert(!flag, "Handling code should not have executed!")
            exceptionThrown = true
        }
        assert(i == 1, "Retry count was not correct!")
        assert(exceptionThrown, "Expected exception was not thrown!")
      }
    }
  }
}
