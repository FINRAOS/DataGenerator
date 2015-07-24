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
