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

import java.io.{File, FileInputStream, FileOutputStream}

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.io.IOUtils

/**
 * Compression helper methods
 */
object CompressionHelper {
  /**
   * Compress a file and write using BZip2 format.
   * @param sourcePath Local path to compress
   * @param destinationPath Local destination to save to -- must be different than sourcePath. Overwrites if already exists.
   * @param deleteSourceFile Whether or not to delete the source file after compressing. Defaults to false.
   */
  def writeFileAsBz2(sourcePath: String, destinationPath: String, deleteSourceFile: Boolean = false): Unit = {
    val sourceFile = new File(sourcePath)
    require(sourceFile.exists && sourceFile.isFile, s"Path `${sourceFile.getAbsolutePath}` must be a file that exists to call writeFileAsBz2!")
    require(sourcePath != destinationPath, s"writeFileAsBz2 requires destinationPath (${destinationPath}) different than sourcePath (${sourcePath})!")

    val destinationFile = new File(destinationPath)
    destinationFile.delete()

    val fileOutStream = new FileOutputStream(destinationFile)
    val bzOutStream = new BZip2CompressorOutputStream(fileOutStream)
    val inputStream = new FileInputStream(sourceFile)
    try {
      IOUtils.copy(inputStream, bzOutStream)
    } finally {
      inputStream.close()
      bzOutStream.close()
      fileOutStream.close()
    }
    sourceFile.delete()
  }
}
