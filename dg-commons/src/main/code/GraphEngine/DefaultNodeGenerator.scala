package GraphEngine

import NodeData.NodeDataType.NodeDataType
import NodeData.{NodeDataTypes, DisplayableData, NodeData, NodeDataStub}

/**
 * Default node generator
 */
class DefaultNodeGenerator[+T_DisplayableData <: DisplayableData,
                           +T_NodeData <: NodeData with DisplayableData,
                           +T_NodeDataStub <: NodeDataStub[T_NodeDataType, T_NodeData, T_NodeDataTypes, T_NodeDataStub],
                           +T_NodeDataType <: NodeDataType[T_NodeData, T_NodeDataStub, T_NodeDataTypes, T_NodeDataType],
                           +T_NodeDataTypes <: NodeDataTypes[T_NodeData, T_NodeDataStub, T_NodeDataType, T_NodeDataTypes]] extends DataNodeGenerator[T_DisplayableData, T_NodeData, T_NodeDataStub, T_NodeDataType, T_NodeDataTypes]
