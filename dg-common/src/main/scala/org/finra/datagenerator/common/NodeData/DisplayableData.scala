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

package org.finra.datagenerator.common.NodeData

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

import NodeDataType.NodeDataType

/**
 * Trait used for any data with a displayable ID and optional fields, and with around event type.
 * For example, this could be used for an object that can be viewed as an element in a DOT file, but it's not specific to that.
 */
trait DisplayableData {
  private var _overrideDisplayableDataId = ""
  def overrideDisplayableDataId: String = _overrideDisplayableDataId
  def overrideDisplayableDataId_=(value: String): Unit = {
    _overrideDisplayableDataId = value
  }
  def getOverrideDisplayableDataId: String = overrideDisplayableDataId
  def defaultDisplayableDataId: String
  def getDefaultDisplayableDataId: String = defaultDisplayableDataId
  def displayableDataId: String = { // Anything that uniquely identifies a childNode; e.g., 1, 2, 3, or NW_1, NW_2, RT_1
    if (overrideDisplayableDataId.nonEmpty) {
      overrideDisplayableDataId
    }
    else {
      defaultDisplayableDataId
    }
  }
  def getDisplayableDataId: String = displayableDataId
  def displayableElements: Iterable[String] = Iterable[String](displayableDataId)
  def getDisplayableElements: Iterable[String] = displayableElements
  def simplifiedDisplayableElements: Iterable[String] = Iterable[String](displayableDataId)
  def getSimplifiedDisplayableElements: Iterable[String] = simplifiedDisplayableElements
  def dataType: NodeDataType[_, _, _, _] = getDataType
  def getDataType: NodeDataType[_, _, _, _]

  /**
   * Used for testing graph isomorphism.
   * @return String uniquely representing this graph's structure
   */
  def getStructuralMD5: String = {
    val strToMd5 = s"${dataType.name},${displayableElements.mkString(",")}"
    DatatypeConverter.printHexBinary(MessageDigest.getInstance("MD5").digest(strToMd5.getBytes("UTF-8")))
  }
}
