package Helpers

/**
 * Helper methods for GraphViz DOT file operations
 */
object DotHelper {
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
          println(s"Error running dot.exe: ${e.getMessage}")
      }
    }
  }
}
