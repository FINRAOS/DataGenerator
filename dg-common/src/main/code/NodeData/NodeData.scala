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

package NodeData

import scala.beans.BeanProperty

/**
 * Data that can be stored in a node of a graph
 */
abstract class NodeData(_stubCreatedFromMaybe: Option[_ <: NodeDataStub[_, _, _, _]] = None) extends DisplayableData {
  def this() = {
    this(None)
  }

  @BeanProperty
  var customPropertyMap = collection.mutable.HashMap[String, String]()

  // TODO: Might need to make NodeData generic around NodeDataStub so can infer the type of stubCreatedFromMaybe without casting.
  def stubCreatedFromMaybe = _stubCreatedFromMaybe
  def getStubCreatedFromMaybe = stubCreatedFromMaybe
}
