package Helpers

import java.text.SimpleDateFormat

/**
 * Helper methods on Option
 */
object DateHelper {

  /**
   * Implicit methods on a java.util.Date
   * @param date
   */
   implicit class DateImplicits(val date: java.util.Date) {

    val yyyymmdd_formatter = new SimpleDateFormat("yyyyMMdd")
    val timestamp_formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS")

    /**
     * Convert the date value to a string using the YYYYmmDD format.
     * @return
     */
    def toYYYYmmDD: String = {
      yyyymmdd_formatter.format(date)
    }

    /**
     * Convert to a 17-digit Long (yyyyMMddhhmmssSSS)
     * @return
     */
    def toLong: Long = {
      timestamp_formatter.format(date).toLong
    }
  }
}
