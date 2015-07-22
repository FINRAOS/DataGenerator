package NodeData

import NodeDataType.NodeDataType
import scala.annotation.unchecked.{uncheckedVariance => uV}

/**
 * Stub of data that can be stored in a node of a graph.
 * The idea is the real data we store in a node may be way too complicated,
 * so we can use a stub to specify type and any relevant metadata (e.g., derived from a DOT
 * file, as in one implementation), and we can have an engine later expand a stub node
 * into a full-fledged data node.
 */
abstract case class NodeDataStub[+T_NodeDataType <: NodeDataType[T_NodeData, T_NodeDataStub, T_NodeDataTypes, T_NodeDataType],
                                 +T_NodeData <: NodeData,
                                 +T_NodeDataTypes <: NodeDataTypes[T_NodeData, T_NodeDataStub, T_NodeDataType, T_NodeDataTypes],
                                 +T_NodeDataStub <: NodeDataStub[T_NodeDataType, T_NodeData, T_NodeDataTypes, T_NodeDataStub]] protected() extends DisplayableData {
  var dataType: (T_NodeDataType @uV)
}