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

import org.finra.datagenerator.common.Graph.Node
import org.finra.datagenerator.common.NodeData.NodeDataType.NodeDataType
import org.finra.datagenerator.common.NodeData.{NodeDataTypes, DisplayableData, NodeData, NodeDataStub}

import scala.annotation.unchecked.{uncheckedVariance => uV}

/**
 * Defines what to do when adding a child or parent, with specified type, from a source node of specified type,
 * as well as how to link existing events.
 * Must be overridden with an implementation to define how to perform these transitions.
 * @tparam T_NodeData Type of data to generate (e.g., could be either real data or could be stubbed data)
 * @tparam T_NodeDataStub Stub type for the data
 * @tparam T_NodeDataType Data type type for this data
 * @tparam T_NodeDataTypes Data types type for this data
 */
abstract class DataTransitions[+T_NodeData <: NodeData with DisplayableData,
                      +T_NodeDataType <: NodeDataType[T_NodeData, T_NodeDataStub, T_NodeDataTypes, T_NodeDataType],
                      +T_NodeDataStub <: NodeDataStub[T_NodeDataType, T_NodeData, T_NodeDataTypes, T_NodeDataStub],
                      +T_NodeDataTypes <: NodeDataTypes[T_NodeData, T_NodeDataStub, T_NodeDataType, T_NodeDataTypes]] {
  /**
   * Information about all data types in this domain
   * @return NodeDataTypes object
   */
  def nodeDataTypes: T_NodeDataTypes

  /**
   * Predicate that returns true if a candidate node is already linked as a parent of another node.
   * @param childNode Child node to check
   * @param candidateParentNode Parent to check
   * @return True - already linked; false - not already linked
   */
  def isExistingParentAlreadyLinked(childNode: Node[T_NodeData @uV], candidateParentNode: Node[T_NodeData @uV]): Boolean = {
    childNode.parents.contains(candidateParentNode)
  }

  /**
   * Link two existing nodes together.
   * @param parent Node to link as parent
   * @param child Node to link as child
   */
  protected def linkExistingNodes(parent: Node[T_NodeData @uV], child: Node[T_NodeData @uV]): Unit

  /**
   * Link two existing nodes together if not already linked.
   * @param parent Node to link as parent
   * @param child Node to link as child
   */
  def linkExistingNodesIfNotAlreadyLinked(parent: Node[T_NodeData @uV], child: Node[T_NodeData @uV]): Unit = {
    if (!isExistingParentAlreadyLinked(child, parent)) {
      linkExistingNodes(parent, child)
    }
  }

  /**
   * Predicate that returns true if a data node is allowed to be linked to another existing node as the child of the existing node.
   * @param childNode Child to check
   * @param candidateParentNode Node to check if it can be parent of child
   * @return True - link allowed; false - link not allowed
   */
  def isLinkToExistingParentAllowed(childNode: Node[T_NodeData @uV], candidateParentNode: Node[T_NodeData @uV]): Boolean = {
    // A candidate parent is any childNode that matches the following criteria:
    //  - Not already a direct parent of the childNode
    //  - Not a child/grandchild/etc. of the childNode (but parents of descendants are ok -- think of them as nephews)
    //  - Transition between parent and child is allowed

    childNode != candidateParentNode &&
      !childNode.getDescendants().contains(candidateParentNode) &&
      isParentTypeTransitionAllowed(childNode, candidateParentNode)
  }

  /**
   * Predicate that returns true if a stub node is allowed to be linked to another existing node as the child of the existing node.
   * @param childNode Child we're trying to link existing node from
   * @param candidateParentNode Existing parent we're trying to link to
   * @return True - link allowed; false - link not allowed
   */
  def isStubLinkToExistingParentAllowed(childNode: Node[T_NodeDataStub @uV], candidateParentNode: Node[T_NodeDataStub @uV]): Boolean = {
    // A candidate parent is any childNode that matches the following criteria:
    //  - Not already a direct parent of the childNode
    //  - Not a child/grandchild/etc. of the childNode (but parents of descendants are ok -- think of them as nephews)
    //  - Transition between parent and child is allowed

    childNode != candidateParentNode &&
      !childNode.getDescendants().contains(candidateParentNode) &&
      !childNode.parents.contains(candidateParentNode) &&
      isStubParentTypeTransitionAllowed(childNode, candidateParentNode)
  }

  /**
   * Predicate that returns true if a stub node is allowed to be linked to a not-yet-existing parent node.
   * @param childNode Child to link from
   * @param candidateParentNode Parent to link to
   * @return True if link allowed
   */
  def isStubParentTypeTransitionAllowed(childNode: Node[T_NodeDataStub @uV], candidateParentNode: Node[T_NodeDataStub @uV]): Boolean

  /**
   * Predicate that returns true if a data node is allowed to be linked to a not-yet-existing parent node.
   * @param childDataNode Child to link from
   * @param candidateParentDataNode Parent to link to
   * @return True if link allowed
   */
  def isParentTypeTransitionAllowed(childDataNode: Node[T_NodeData @uV], candidateParentDataNode: Node[T_NodeData @uV]): Boolean

  /**
   * Add a new node, having data of the specified type, as a parent to the existing node.
   * @param childDataNode Child to add from
   * @param parentNodeDataType Type of parent node to add
   * @return Added parent node
   */
  def addRandomlyGeneratedParentData(childDataNode: Node[T_NodeData @uV], parentNodeDataType: (T_NodeDataType @uV)): Node[T_NodeData @uV]/* = {
    addRandomlyGeneratedParentEvent(childDataNode, new NodeDataStub(parentNodeDataType))
  }*/

  /**
   * Add a new node, having data satisfying the specified stub, as a parent to the existing node.
   * @param childDataNode Child to add from
   * @param parentNodeDataStub Stub describing parent node to add
   * @return Added parent node
   */
  def addRandomlyGeneratedParentData(childDataNode: Node[T_NodeData @uV], parentNodeDataStub: (T_NodeDataStub @uV)): Node[T_NodeData @uV]

  /**
   * Add a new node, having data of the specified type, as a child to the existing node.
   * @param parentDataNode Parent to add from
   * @param childNodeDataType Type of child to add
   * @return Added child node
   */
  def addRandomlyGeneratedChildData(parentDataNode: Node[T_NodeData @uV], childNodeDataType: (T_NodeDataType @uV)): Node[T_NodeData @uV]/* = {
    addRandomlyGeneratedChildData(parentDataNode, new NodeDataStub(childNodeDataType))
  }*/

  /**
   * Add a new node, having data satisfying the specified stub, as a child to the existing node.
   * @param parentDataNode Parent to add from
   * @param childNodeDataStub Stub describing child node to add
   * @return Added child node
   */
  def addRandomlyGeneratedChildData(parentDataNode: Node[T_NodeData @uV], childNodeDataStub: (T_NodeDataStub @uV)): Node[T_NodeData @uV]
}
