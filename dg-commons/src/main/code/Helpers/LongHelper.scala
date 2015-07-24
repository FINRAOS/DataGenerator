package Helpers

import java.text.SimpleDateFormat

/**
 * Boolean implicit methods
 */
object LongHelper {
  implicit class LongImplicits(private val long: Long) {
    private val longDateFormatter = new SimpleDateFormat("yyyyMMddhhmmssSSS")

    /**
     * Converts a Long formatted as yyyyMMddhhmmssSSS to a java.util.Date.
     * @return
     */
    def toDateTime: java.util.Date= {
      longDateFormatter.parse(long.toString)
    }
  }
}
