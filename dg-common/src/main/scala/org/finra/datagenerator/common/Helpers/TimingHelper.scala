package org.finra.datagenerator.common.Helpers

import System.nanoTime

/**
 * Timing methods
 */
object TimingHelper {
  /**
   * Runs a block of code and returns the code's return value as well as the nanoseconds taken to run that code block.
   * @param codeBlock Block of code
   * @tparam T Return type of block of code
   * @return Tuple containing code's return value and nanoseconds elapsed
   */
  def time[T](codeBlock: => T): (T, Long) = {
    val initialTimestamp: Long = nanoTime
    (codeBlock, nanoTime - initialTimestamp)
  }
}
