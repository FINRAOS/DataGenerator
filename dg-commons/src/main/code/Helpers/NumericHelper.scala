package Helpers

/**
 * Numeric helper methods
 */
object NumericHelper {
  /**
   * Take a 48-bit number and a 16-bit number and concatenate them together as a Long.
   * @param num1
   * @param num2
   * @return
   */
  def concatenateTwoNumbers48BitAnd16Bit(num1: Long, num2: Short): Long = {
    assert(num1 >= 0L  && num1 < 281474976710656L, s"num1 is $num1") // Max 2^48 - 1
    val modifiedNum2 =  num2 + 32768 // We'd feel pretty dumb ORing against a negative number, so make this basically an unsigned short (0 to 2^16 - 1)
    num1 << 16 | modifiedNum2
  }
}