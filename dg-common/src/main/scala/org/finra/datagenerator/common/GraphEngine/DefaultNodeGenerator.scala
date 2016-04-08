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

package org.finra.datagenerator.common.GraphEngine

import org.finra.datagenerator.common.NodeData.NodeDataType.NodeDataType
import org.finra.datagenerator.common.NodeData.{NodeDataTypes, DisplayableData, NodeData, NodeDataStub}

/**
 * Default node generator
 * @tparam T_NodeData Type of data to generate (e.g., could be either real data or could be stubbed data)
 * @tparam T_NodeDataTypeData Concrete type of data underlying T_NodeData (e.g., if T_NodeData is a stub, then this is the data that stub abstracts)
 * @tparam T_NodeDataStub Stub type for the data
 * @tparam T_NodeDataType Data type type for this data
 * @tparam T_NodeDataTypes Data types type for this data
 */
class DefaultNodeGenerator[+T_NodeDataTypeData <: DisplayableData,
                           +T_NodeData <: NodeData with DisplayableData,
                           +T_NodeDataStub <: NodeDataStub[T_NodeDataType, T_NodeData, T_NodeDataTypes, T_NodeDataStub],
                           +T_NodeDataType <: NodeDataType[T_NodeData, T_NodeDataStub, T_NodeDataTypes, T_NodeDataType],
                           +T_NodeDataTypes <: NodeDataTypes[T_NodeData, T_NodeDataStub, T_NodeDataType, T_NodeDataTypes]]
  extends DataNodeGenerator[T_NodeDataTypeData, T_NodeData, T_NodeDataStub, T_NodeDataType, T_NodeDataTypes]
