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

import org.finra.datagenerator.common.GraphEngine.DataTransitions

import scala.annotation.unchecked.{uncheckedVariance => uV}

/**
 * Describes all the possible node data types
 * @tparam T_NodeData Type of data to generate (e.g., could be either real data or could be stubbed data)
 * @tparam T_NodeDataStub Stub type for the data
 * @tparam T_NodeDataType Data type type for this data
 * @tparam T_ThisType This type
 */
trait NodeDataTypes[+T_NodeData <: NodeData,
                    +T_NodeDataStub <: NodeDataStub[T_NodeDataType, T_NodeData, T_ThisType, T_NodeDataStub],
                    +T_NodeDataType <: NodeDataType.NodeDataType[T_NodeData @uV, T_NodeDataStub @uV, T_ThisType @uV, T_NodeDataType],
                    +T_ThisType <: NodeDataTypes[T_NodeData, T_NodeDataStub, T_NodeDataType, T_ThisType]] {
  /**
   * All data types that are part of this domain
   * @return All data types
   */
  def allDataTypes: collection.immutable.HashSet[T_NodeDataType @uV]

  /**
   * All data types that are part of this domain and which are allowed to be the initial (e.g., only) node in a graph.
   * @return Initial data types
   */
  def allInitialDataTypes: collection.immutable.HashSet[T_NodeDataType @uV]

  /**
   * Specifies an object of a class implementing DataTransitions, which define, from each type, how to create a
   * child or parent of each allowable child/parent type.
   */
  def dataTransitions: DataTransitions[T_NodeData, T_NodeDataType, T_NodeDataStub, T_ThisType]
}
