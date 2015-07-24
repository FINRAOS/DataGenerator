package Helpers

import java.security.MessageDigest

/**
 * Extensions to String
 */
object StringHelper {

  /**
   * Implicit methods on String
   * @param str
   */
  implicit class StringImplicits(private val str: String) {
    /**
     * Whether or not the string is comprised entirely of digits
     * @return
     */
    def isNumeric: Boolean = {
      str.forall(Character.isDigit)
    }

    /**
     * Convert to an integer.
     * @return If parsable to an Int, return as Some(intVal), else None.
     */
    def toIntMaybe: Option[Int] = {
      try {
        Some(str.toInt)
      } catch {
        case e: NumberFormatException => None
      }
    }

    /**
     * Split into a sequence based on a specified separator character.
     * @param separator
     * @param removeEmptyEntries
     * @return
     */
    def splitOnChar(separator: Char, removeEmptyEntries: Boolean = true): Seq[String] = {
      splitOnChars(collection.immutable.Set[Char](separator), removeEmptyEntries = removeEmptyEntries)
    }

    /**
     * Split into a sequence based on specified separator characters.
     * @param separators
     * @param removeEmptyEntries
     * @return
     */
    def splitOnChars(separators: Set[Char], removeEmptyEntries: Boolean = true): Seq[String] = {
      val splitResult = new collection.mutable.ArrayBuffer[String]()

      var startingIndexToAdd = 0
      var currentIndex = 0
      val stringLength = str.length
      while (currentIndex < stringLength) {
        if (separators.contains(str(currentIndex))) {
          if (startingIndexToAdd == currentIndex) {
            if (!removeEmptyEntries) {
              splitResult += ""
            }
          } else {
            splitResult += str.substring(startingIndexToAdd, currentIndex)
          }
          startingIndexToAdd = currentIndex + 1 // separator of size 1
        }
        currentIndex += 1
      }
      if (startingIndexToAdd < stringLength || !removeEmptyEntries) {
        splitResult += str.substring(startingIndexToAdd, stringLength)
      }
      splitResult
    }

    /**
     * Split into a sequence based on specififed seprator string
     * @param separator
     * @param removeEmptyEntries
     * @return
     */
    def splitOnString(separator: String, removeEmptyEntries: Boolean = true): Seq[String] = {
      val splitResult = new collection.mutable.ArrayBuffer[String]()
      val separatorLength = separator.length
      var startingIndexToAdd = 0
      var currentIndex = 0
      val stringLength = str.length
      while (currentIndex <= stringLength - separatorLength) {
        if (str.substring(currentIndex, currentIndex + separatorLength).equals(separator)) {
          if (startingIndexToAdd == currentIndex) {
            if (!removeEmptyEntries) {
              splitResult += ""
            }
          } else {
            splitResult += str.substring(startingIndexToAdd, currentIndex)
          }
          startingIndexToAdd = currentIndex + separatorLength
          currentIndex += separatorLength
        } else {
          currentIndex += 1
        }
      }
      if (startingIndexToAdd < stringLength || !removeEmptyEntries) {
        splitResult += str.substring(startingIndexToAdd, stringLength)
      }
      splitResult
    }

    /**
     * Split on common whitespace characters.
     * @return
     */
    def splitOnWhitespace: Seq[String] = {
      splitOnChars(collection.immutable.Set[Char](' ', '\t', '\r', '\n'), removeEmptyEntries = true)
    }

    /**
     * Split on all whitespace characters.
     * @return
     */
    def splitOnWhitespaceIncludingFormFeedAndVerticalTab: Seq[String] = {
      // Not including these two chars in the default implementation because it's a minor performance hit for
      // characters that will almost never occur.
      splitOnChars(collection.immutable.Set[Char](' ', '\t', '\r', '\n', '\f', '\u000b'), removeEmptyEntries = true)
    }

    /**
     * Get the index of the Nth occurence of a character in the string.
     * @param char
     * @param n
     * @return
     */
    def indexOfNthOccurrence(char: Char, n: Int): Int = {
      var index = str.indexOf(char)
      var counter = n - 1
      while (counter > 0 && index != -1) {
        index = str.indexOf(char, index + 1)
        counter -= 1
      }
      index
    }

    /**
     * Truncate a string to a maximum length.
     * @param maxLength Maximum length to truncate the string to. This does NOT factor in the length of the suffix.
     * @param suffixIfTruncated Suffix to append to string if it was truncated. Defaults to "..." (3 periods, not the single-char ellipsis, which if you want to pass in, is \u2026).
     * @return "hello world".truncateTo(5, "...") returns "hello..."
     */
    def truncateTo(maxLength: Int, suffixIfTruncated: String = "..."): String = {
      if (str.length <= maxLength) {
        str
      } else {
        s"${str.substring(0, maxLength)}${suffixIfTruncated}"
      }
    }


    /**
     * Truncate a string to a maximum length, cutting from the middle and inserting a split token if necessary.
     * @param maxLength
     * @param splitToken
     * @return
     */
    def truncateFromMiddle(maxLength: Int, splitToken: String = "..."): String = {
      if (str.length <= maxLength) {
        str
      } else {
        assert(splitToken.length < maxLength - 2)
        val remainingLength = maxLength - splitToken.length
        val halfway = remainingLength / 2
        val left = remainingLength -  halfway
        s"${str.substring(0, halfway)}${splitToken}${str.substring(str.length - left, str.length)}"
      }
    }

    /**
     * Convert the string to an MD5.
     * @return
     */
    def md5 = {
      MessageDigest.getInstance("MD5").digest(str.getBytes)
    }
  }
}
