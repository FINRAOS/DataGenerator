package NodeData

import GraphEngine.DataTransitions

import scala.annotation.unchecked.{uncheckedVariance => uV}

/**
 * Describes all the possible node data types
 * @tparam T Type extending NodeData
 */
trait NodeDataTypes[+T_NodeData <: NodeData,
                    +T_NodeDataStub <: NodeDataStub[T_NodeDataType, T_NodeData, T_ThisType, T_NodeDataStub],
                    +T_NodeDataType <: NodeDataType.NodeDataType[T_NodeData @uV, T_NodeDataStub @uV, T_ThisType @uV, T_NodeDataType],
                    +T_ThisType <: NodeDataTypes[T_NodeData, T_NodeDataStub, T_NodeDataType, T_ThisType]] {
  /**
   * All data types that are part of this domain
   * @return
   */
  def allDataTypes: collection.immutable.HashSet[T_NodeDataType @uV]

  /**
   * All data types that are part of this domain and which are allowed to be the initial (e.g., only) node in a graph.
   * @return
   */
  def allInitialDataTypes: collection.immutable.HashSet[T_NodeDataType @uV]

  /**
   * Specifies an object of a class implementing DataTransitions, which define, from each type, how to create a
   * child or parent of each allowable child/parent type.
   */
  def dataTransitions: DataTransitions[T_NodeData, T_NodeDataType, T_NodeDataStub, T_ThisType]
}