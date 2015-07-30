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

package Helpers

import java.io.{File, FilenameFilter}

/**
 * File implicit methods and helper methods
 */
object FileHelper {
  /**
   * Creates a directory if it doesn't exist, and if it does exist, deletes everything from it.
   * @param path Path of directory to create or empty
   * @return Path
   */
  def ensureEmptyDirectoryExists(path: String): String = {
    createDirIfNotExists(path)
    new File(path).purgeDirectory()

    path
  }

  /**
   * Creates a directory if it doesn't exist.
   * @param path Path of directory to create if not exists
   * @return Path
   */
  def ensureDirectoryExists(path: String): String = createDirIfNotExists(path)

  /**
   * Creates a directory if it doesn't exist.
   * @param path Path of directory to create if not exists
   * @return Path
   */
  def createDirIfNotExists(path: String): String = {
    val dir = new File(path)
    if (!dir.exists()) dir.mkdirs()

    path
  }

  /**
   * Implicit methods on a java.io.File
   * @param fileOrDirectory
   */
  implicit class FileExtensions(private val fileOrDirectory: File) {
    private final val IS_NOT_A_DIRECTORY = " is not a directory!"

    /**
     * Delete everything from a directory, and then delete the directory itself.
     */
    def deleteNonEmptyDirectory(): Unit = {
      assert(fileOrDirectory.isDirectory)
      fileOrDirectory.listFiles.foreach(subFileOrDirectory => {
        if (subFileOrDirectory.isDirectory) subFileOrDirectory.deleteNonEmptyDirectory()
      })
      fileOrDirectory.delete()
    }

    /**
     * Delete the files, but not the folders, from all subdirectories.
     */
    def deleteFilesRecursively(): Unit = {
      assert(fileOrDirectory.isDirectory)
      fileOrDirectory.listFiles.foreach(subFileOrDirectory => {
        if (subFileOrDirectory.isDirectory) {
          subFileOrDirectory.deleteFilesRecursively()
        } else {
          subFileOrDirectory.delete()
        }
      })
    }

    /**
     * Recursively deletes everything from a directory.
     */
    def purgeDirectory(): Unit = {
      assert(fileOrDirectory.isDirectory)
      fileOrDirectory.listFiles.foreach(subFileOrDirectory => {
        if (subFileOrDirectory.isDirectory) subFileOrDirectory.purgeDirectory()
        subFileOrDirectory.delete()
      })
    }

    /**
     * Returns the first line from a file, else None if not a file.
     * @return First line from the file, or None if not a filee
     */
    def getFirstLine: Option[String] = {
      if (fileOrDirectory.isFile) {
        val source = io.Source.fromFile(fileOrDirectory)
        try {
          source.getLines().find(_ => true)
        } finally {
          source.close()
        }
      } else {
        None
      }
    }

    /**
     * Get the size in bytes of a file or of a directory and all its contents, recursively.
     * @return
     */
    def getSizeRecursively: Long = {
      if (fileOrDirectory.isFile) {
        fileOrDirectory.length()
      } else {
        fileOrDirectory.listFiles.foldLeft(0L)((size, fileOrDir) => size + fileOrDir.getSizeRecursively)
      }
    }

    /**
     * Get all the files in a directory, regardless of nesting level.
     * @return
     */
    def getFilesRecursively: Iterable[File] = {
      if (fileOrDirectory.exists) {
        assert(!fileOrDirectory.isFile, s"$fileOrDirectory $IS_NOT_A_DIRECTORY")
        val files = fileOrDirectory.listFiles
        files ++ files.filter(_.isDirectory).flatMap(_.getFilesRecursively)
      } else {
        new collection.mutable.ArrayBuffer[File]()
      }
    }

    /**
     * Get all the files in a directory, regardless of nesting level, where file name contains a specified string.
     * @param substring
     * @return
     */
    def getFilesRecursivelyContaining(substring: String, ignoreCase: Boolean = true): Iterable[File] = {
      if (fileOrDirectory.exists) {
        assert(!fileOrDirectory.isFile, s"$fileOrDirectory $IS_NOT_A_DIRECTORY")
        val files = fileOrDirectory.listFilesContaining(substring, ignoreCase = ignoreCase)
        files ++ fileOrDirectory.listFiles.filter(_.isDirectory).flatMap(_.getFilesRecursivelyContaining(substring))
      } else {
        new collection.mutable.ArrayBuffer[File]()
      }
    }

    /**
     * Get all the files in a directory, regardless of nesting level, where file name ends with a suffix.
     * @param fileSuffix
     * @return
     */
    def getFilesRecursivelyEndingWith(fileSuffix: String, ignoreCase: Boolean = true): Iterable[File] = {
      if (fileOrDirectory.exists) {
        assert(!fileOrDirectory.isFile, s"$fileOrDirectory $IS_NOT_A_DIRECTORY")
        val files = fileOrDirectory.listFilesEndingWith(fileSuffix, ignoreCase = ignoreCase)
        files ++ fileOrDirectory.listFiles.filter(_.isDirectory).flatMap(_.getFilesRecursivelyEndingWith(fileSuffix))
      } else {
        new collection.mutable.ArrayBuffer[File]()
      }
    }

    /**
     * Get all the files in a directory, but NOT its subdirectories, where file name ends with a suffix.
     * @param fileSuffix
     * @return
     */
    def listFilesEndingWith(fileSuffix: String, ignoreCase: Boolean = true): Seq[File] = {
      if (!fileOrDirectory.isDirectory) {
        Seq[File]()
      } else {
        fileOrDirectory.listFiles(new FilenameFilter {
          override def accept(dir: File, name: String): Boolean = {
            if (ignoreCase) {
              name.toUpperCase.endsWith(fileSuffix.toUpperCase)
            } else {
              name.endsWith(fileSuffix)
            }
          }
        })
      }
    }

    /**
     * Get all the files in a directory, but NOT its subdirectories, where file contains a specified string.
     * @param substring
     * @return
     */
    def listFilesContaining(substring: String, extension: String = "", ignoreCase: Boolean = true): Seq[File] = {
      if (!fileOrDirectory.isDirectory) {
        Seq[File]()
      } else {
        fileOrDirectory.listFiles(new FilenameFilter {
          override def accept(dir: File, name: String): Boolean = {
            if (ignoreCase) {
              name.toUpperCase.contains(substring.toUpperCase) && name.toUpperCase.endsWith(extension.toUpperCase)
            } else {
              name.contains(substring) && name.endsWith(extension)
            }
          }
        })
      }
    }
  }
}
