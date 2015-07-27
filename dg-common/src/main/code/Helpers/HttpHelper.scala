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

import java.io.InputStream
import java.net.URL

import org.apache.commons.codec.binary.Base64
import Helpers.InputStreamHelper._
import scala.io.Source

/**
 * HTTP Helper methods
 */
object HttpHelper {
  /**
   *Download from a URL to a local path, optionally using basic authentication.
   * @param url
   * @param savePath
   * @param user
   * @param passwordOrToken
   * @param requestProperties
   */
  def download(url: String, savePath: String, user: String = null, passwordOrToken: String = null, requestProperties: Map[String, String]= Map("User-Agent"->"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")) : Unit = {
    val inputStream = getInputStreamFromUrl(url, user, passwordOrToken, requestProperties)
    try {
      inputStream.downloadToFile(savePath)
    } finally {
      inputStream.close()
    }
  }

  /**
   * Get content from a URL, optionally using basic authentication, as a string.
   * @param url
   * @param user
   * @param passwordOrToken
   * @param requestProperties
   * @return
   */
  def getPageContentFromUrl(url: String, user: String = null, passwordOrToken: String = null, requestProperties: Map[String, String]= Map("User-Agent"->"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")) : String = {
    val inputStream = getInputStreamFromUrl(url, user, passwordOrToken, requestProperties)
    try {
      Source.fromInputStream(inputStream).getLines().mkString("\n")
    } finally {
      inputStream.close()
    }
  }

  /**
   * Get content from a URL, optionally using basic authentication, as an input stream.
   * @param url
   * @param user
   * @param passwordOrToken
   * @param requestProperties
   * @return
   */
  def getInputStreamFromUrl(url: String, user: String = null, passwordOrToken: String = null, requestProperties: Map[String, String]= Map("User-Agent"->"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")) : InputStream = {
    val connection = new URL(url).openConnection
    requestProperties.foreach({
      case (name, value) => connection.setRequestProperty(name, value)
    })

    if (user != null && passwordOrToken != null) {
      connection.setRequestProperty("Authorization", getHeaderForBasicAuthentication(user, passwordOrToken))
    }

    connection.getInputStream
  }

  /**
   * Base-64 encode the username and password.
   * @param username
   * @param password
   * @return
   */
  def encodeCredentials(username: String, password: String): String = {
    new String(Base64.encodeBase64String((username + ":" + password).getBytes))
  }

  /**
   * Get header for basic authentication.
   * @param username
   * @param password
   * @return
   */
  def getHeaderForBasicAuthentication(username: String, password: String): String = {
    "Basic " + encodeCredentials(username, password)
  }
}