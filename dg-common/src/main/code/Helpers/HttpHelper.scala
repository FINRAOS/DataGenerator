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
import Helpers.InputStreamHelper.InputStreamExtensions
import scala.io.Source

/**
 * HTTP Helper methods
 */
object HttpHelper {
  private final val DEFAULT_PROPERTIES = "User-Agent"->"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)"

  /**
   *Download from a URL to a local path, optionally using basic authentication.
   * @param url URL to download from
   * @param savePath Local path to save to
   * @param userMaybe Some(User String) or None
   * @param passwordOrTokenMaybe Some(Password or Token String) or None
   * @param requestProperties Request properties map, or if not set, uses default
   */
  def download(url: String, savePath: String, userMaybe: Option[String] = None, passwordOrTokenMaybe: Option[String] = None
    , requestProperties: Map[String, String] = Map(DEFAULT_PROPERTIES)) : Unit = {

    val inputStream = getInputStreamFromUrl(url, userMaybe, passwordOrTokenMaybe, requestProperties)
    try {
      inputStream.downloadToFile(savePath)
    } finally {
      inputStream.close()
    }
  }

  /**
   * Get content from a URL, optionally using basic authentication, as a string.
   * @param url URL to download from
   * @param userMaybe Some(User String) or None
   * @param passwordOrTokenMaybe Some(Password or Token String) or None
   * @param requestProperties Request properties map, or if not set, uses default   * @return
   * @return Page content as string
   */
  def getPageContentFromUrl(url: String, userMaybe: Option[String] = None, passwordOrTokenMaybe: Option[String] = None
    , requestProperties: Map[String, String] = Map(DEFAULT_PROPERTIES)) : String = {

    val inputStream = getInputStreamFromUrl(url, userMaybe, passwordOrTokenMaybe, requestProperties)
    try {
      Source.fromInputStream(inputStream).getLines().mkString("\n")
    } finally {
      inputStream.close()
    }
  }

  /**
   * Get content from a URL, optionally using basic authentication, as an input stream.
   * @param url URL to get content from
   * @param userMaybe Some(User String) or None
   * @param passwordOrTokenMaybe Some(Password or Token String) or None
   * @param requestProperties Request properties as a map, with defaults used if not specified
   * @return InputStream with URL content
   */
  def getInputStreamFromUrl(url: String, userMaybe: Option[String] = None, passwordOrTokenMaybe: Option[String] = None
    , requestProperties: Map[String, String] = Map(DEFAULT_PROPERTIES)) : InputStream = {
    assert(userMaybe != Some(null) && passwordOrTokenMaybe != Some(null))

    val connection = new URL(url).openConnection
    requestProperties.foreach({
      case (name, value) => connection.setRequestProperty(name, value)
    })

    if (userMaybe.nonEmpty && passwordOrTokenMaybe.nonEmpty) {
      connection.setRequestProperty("Authorization", getHeaderForBasicAuthentication(userMaybe.get, passwordOrTokenMaybe.get))
    }

    connection.getInputStream
  }

  /**
   * Base-64 encode the username and password.
   * @param username Username to encode
   * @param password Password to encode
   * @return Base-64-encoded username and password string
   */
  def encodeCredentials(username: String, password: String): String = {
    new String(Base64.encodeBase64String((username + ":" + password).getBytes))
  }

  /**
   * Get header for basic authentication.
   * @param username Username
   * @param password Password
   * @return Basic authentication header
   */
  def getHeaderForBasicAuthentication(username: String, password: String): String = {
    "Basic " + encodeCredentials(username, password)
  }
}
