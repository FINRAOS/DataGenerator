/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finra.datagenerator.common.Helpers

import java.io.{FileOutputStream, InputStream}

/**
 * Input stream helpers
 */
object InputStreamHelper {

  /**
   * Input stream implicit methods
   * @param inputStream Input stream
   */
  implicit class InputStreamExtensions(private val inputStream: InputStream) {
    /**
     * Save the input stream to a file.
     * @param path Path to save input stream to
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
