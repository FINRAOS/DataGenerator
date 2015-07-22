package Helpers

/**
 * Boolean implicit methods
 */
object BooleanHelper {
  implicit class BooleanSerialization(val boolean: Boolean) {
    /**
     * Returns "Y" if boolean is true, else ""
     * @return
     */
    def toY_orEmpty: String = {
      if (!boolean) return ""
      "Y"
    }
  }
}
