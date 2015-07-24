package Helpers

import java.io.{FileOutputStream, InputStream}

/**
 * Input stream helpers
 */
object InputStreamHelper {

  /**
   * Input stream implicit methods
   * @param inputStream
   */
  implicit class InputStreamExtensions(private val inputStream: InputStream) {
    /**
     * Save the input stream to a file.
     * @param path
     */
    def downloadToFile(path: String): Unit = {
      val buffer = new Array[Byte](8 * 1024)

      val outStream = new FileOutputStream(path)
      try {
        var bytesRead = 0
        while ({bytesRead = inputStream.read(buffer); bytesRead != -1}) {
          outStream.write(buffer, 0, bytesRead)
        }
      } finally {
        outStream.close()
      }
    }
  }
}
