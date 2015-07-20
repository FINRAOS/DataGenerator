package Helpers

import scala.util.control.Exception._

/**
 * Helper methods to retry code with configurable retry count and exceptions to handle
 */
object RetryHelper {
  /**
   * Retry any block of code up to a max number of times, optionally specifying the type of exception to retry.
   * @param maxTries Number of times to try before throwing the exception.
   * @param exceptionTypesToRetry Types of exception to retry. Defaults to single-element sequence containing classOf[RuntimeException]
   * @param codeToRetry Block of code to try
   * @tparam T Return type of block of code to try
   * @return Return value of block of code to try (else exception will be thrown if it failed all tries)
   */
  def retry[T](maxTries: Int, exceptionTypesToRetry: Seq[Class[_ <: Throwable]] = Seq(classOf[RuntimeException]))(codeToRetry: => T)(handlingCode: => Unit = () => ()): T = {
    var result: Option[T] = None
    var left = maxTries
    while(!result.isDefined) {
      left = left - 1

      // try/catch{case...} doesn't seem to support dynamic exception types, so using handling block instead.

      handling(exceptionTypesToRetry:_*)
        .by(ex => if (left <= 0) throw ex else handlingCode).apply({
          result = Some(codeToRetry)
        })
    }
    result.get
  }
}
