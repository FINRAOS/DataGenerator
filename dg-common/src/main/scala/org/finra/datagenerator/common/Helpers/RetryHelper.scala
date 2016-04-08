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

import scala.util.control.Exception._

// Motivation for this object is described in the following blog posts:
//   http://googlyadventures.blogspot.com/2015/05/what-else-i-learned-yesterday.html
//   http://googlyadventures.blogspot.com/2015/06/today-i-learned-doing-things-hard-way.html

/**
 * Helper methods to retry code with configurable retry count and exceptions to handle
 */
object RetryHelper {
  /**
   * Retry any block of code up to a max number of times, optionally specifying the types of exceptions to retry
   * and code to run when handling one of the retryable exception types.
   * @param maxTries Number of times to try before throwing the exception.
   * @param exceptionTypesToRetry Types of exceptions to retry. Defaults to single-element sequence containing classOf[RuntimeException]
   * @param codeToRetry Block of code to try
   * @param handlingCode Block of code to run if there is a catchable exception
   * @tparam T Return type of block of code to try
   * @return Return value of block of code to try (else exception will be thrown if it failed all tries)
   */
  def retry[T](maxTries: Int, exceptionTypesToRetry: Seq[Class[_ <: Throwable]] = Seq(classOf[RuntimeException]))
              (codeToRetry: => T)
              (handlingCode: Throwable => Unit = _ => ()): T = {
    var result: Option[T] = None
    var left = maxTries
    while (!result.isDefined) {
      left = left - 1

      // try/catch{case...} doesn't seem to support dynamic exception types, so using handling block instead.

      handling(exceptionTypesToRetry: _*)
        .by(ex => {
        if (left <= 0) {
          throw ex
        } else {
          handlingCode(ex)
        }
      }).apply({
        result = Option(codeToRetry)
      })
    }
    result.get
  }
}
