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
   implicit class OptionSerialization[T](val option: Option[T]) {
    /**
     * Convert the option value to a string, or if undefined, to ""
     * @return
     */
    def toStringOrEmpty: String = {
      if (option.isEmpty) ""
      else if (option.get.isInstanceOf[java.sql.Date]) {
        val formatter = new SimpleDateFormat("yyyyMMdd")
        formatter.format(option.get)
      }
      else option.get.toString
    }
  }
}
