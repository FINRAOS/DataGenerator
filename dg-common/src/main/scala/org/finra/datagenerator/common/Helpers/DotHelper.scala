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

import scala.beans.BooleanBeanProperty

/**
 * Helper methods for GraphViz DOT file operations
 */
object DotHelper {
  @BooleanBeanProperty
  var anyFailures = false

  /**
   * Requires dot.exe to be in PATH -- else will try to run, fail, and log the error and then continue.
   * Preconditions: dot.exe must be installed in PATH (open-source GraphViz installer will do this), else
   * it will log a failure and continue.
   * @param dotFilePath Local path of DOT file to write as PNG.
   */
  def writeDotFileAsPng(dotFilePath: String): Unit = {
    if (!anyFailures) {
      try {
        Runtime.getRuntime.exec( s"""dot.exe -Tpng "${dotFilePath}" -O""")
      } catch {
        case e: java.io.IOException =>
          DotHelper.anyFailures = true
          println(s"Error running dot.exe: ${e.getMessage}") // scalastyle:ignore
      }
    }
  }
}
