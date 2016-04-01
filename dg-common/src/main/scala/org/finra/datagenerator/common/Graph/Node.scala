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

package org.finra.datagenerator.common.Graph

// Dev notes:
// So we want mutable covariant types.
// We want to be able to change a Node without copying the entire structure --> mutable
// We want to be able to write code that is generic around Nodes -- e.g., have an engine that takes in a Node of any type.
// So a method that accepts a Node[GenericType] should also accept a Node[ConcreteType] --> covariance
// This is why we have all sorts of @uV (uncheckedVariance) annotations -- because
// covariance and mutability are incompatible because the compiler cannot guarantee type-safety.
// The classic example is the Java Array, where you can cast an Array[Int] to an Array[Object] and add a string to it,
// resulting in a ClassCastException when sometime later tries to get the string value from the Array[Int].
// So we are giving up some type-safety, but we will just be careful not to write code that does this!


import java.io.OutputStream

import org.finra.datagenerator.common.NodeData.DisplayableData
import scala.annotation.unchecked.{uncheckedVariance => uV}
import scala.beans.BeanProperty
import scala.collection.mutable

/**
 * Generic childNode in a directed acyclic graph
 * @param _data Data to be stored in node
 * @param _containingGraph Containing graph
 * @param nodeIndexInContainingGraph 0-based index of node in containing graph's allNodes collection (same as order added)
 * @tparam T_NodeData Type of data stored in node
 */
class Node[+T_NodeData <: DisplayableData](_data: T_NodeData, _containingGraph: Graph[T_NodeData], var nodeIndexInContainingGraph: Int) {
  @BeanProperty
  val containingGraph: Graph[T_NodeData @uV] = _containingGraph

  @BeanProperty
  var data: T_NodeData @uV = _data

  if (containingGraph.appendSharedDisplayIdsWithNumericalSuffix) {
    // Fill in IDs
    if (data.overrideDisplayableDataId.isEmpty) {
      if (!containingGraph.nodeIdCounters.contains(data.defaultDisplayableDataId)) {
        containingGraph.nodeIdCounters += ((data.defaultDisplayableDataId, 1))
        data.overrideDisplayableDataId = s"${data.defaultDisplayableDataId}_1"
      } else {
        containingGraph.nodeIdCounters += ((data.defaultDisplayableDataId, containingGraph.nodeIdCounters(data.defaultDisplayableDataId) + 1))
        data.overrideDisplayableDataId = s"${data.defaultDisplayableDataId}_${containingGraph.nodeIdCounters(data.defaultDisplayableDataId)}"
      }
    } else {
      if (!containingGraph.nodeIdCounters.contains(data.overrideDisplayableDataId)) {
        containingGraph.nodeIdCounters += ((data.overrideDisplayableDataId, 1))
      } else {
        containingGraph.nodeIdCounters += ((data.overrideDisplayableDataId, containingGraph.nodeIdCounters(data.overrideDisplayableDataId) + 1))
        data.overrideDisplayableDataId += s"_${containingGraph.nodeIdCounters(data.overrideDisplayableDataId)}"
      }
    }
    // else if overrideDisplayableDataId is nonEmpty, we assume it's unique and we don't care to check (yet),
    // but we could add this in later. If it's not, the DOT graph output will be wrong.
  }

  @BeanProperty
  val parents: mutable.ArrayBuffer[Node[T_NodeData @uV]] = new mutable.ArrayBuffer[Node[T_NodeData]]()

  @BeanProperty
  val children: mutable.ArrayBuffer[Node[T_NodeData @uV]] = new mutable.ArrayBuffer[Node[T_NodeData]]()

  private lazy val descendantsInternal: mutable.HashSet[Node[T_NodeData @uV]] = new mutable.HashSet[Node[T_NodeData]]()

  /**
   * Remove this node from its containing graph
   */
  def delete(): Unit = {
    if (isRoot) {
      containingGraph.rootNodes -= this
    }
    parents.foreach(parent => {
      parent.children -= this
    })
    parents.clear()
    children.foreach(child => {
      child.parents -= this
      if (child.isRoot) {
        containingGraph.rootNodes += child
      }
    })
    children.clear()
    containingGraph.allNodes.indices.filter(_ > nodeIndexInContainingGraph).foreach(index => {
      containingGraph.allNodes(index).nodeIndexInContainingGraph -= 1
    })
    containingGraph.allNodes -= this
  }

  /**
   * Break all links and remove from rootNodes collection
   */
  def orphan(): Unit = {
    breakAllLinks()
    if (isRoot) {
      containingGraph.rootNodes -= this
    }
  }

  /**
   * Break all links this node particpates in.
   */
  def breakAllLinks(): Unit = {
    this.children.foreach(child => {
      child.parents -= this
      if (child.isRoot) {
        containingGraph.rootNodes += child
      }
    })
    this.parents.foreach(_.children -= this)
    this.children.clear()
    this.parents.clear()
  }

  /**
   * Get all the descendant nodes (children of this node, those children's children, to the bottom).
   * TODO: If we change org.finra.datagenerator.common.Graph to support cycles, then this method needs to change to only
   * traverse a child if it hasn't already been traversed.
   * @return Set of all descendents
   */
  def getDescendants(): mutable.HashSet[Node[T_NodeData @uV]] = {
    descendantsInternal.clear()
    if (children.size > 0) {
      children.foreach(child => {
        descendantsInternal ++= child.getDescendants()
      })
    }
    descendantsInternal
  }

  /**
   * Whether or not this is a root node
   * @return True = root, false = not root
   */
  def isRoot : Boolean = {
    parents.isEmpty
  }

  /**
   * Create a new node and link it as the parent to this node.
   * @param parentData Node to add parent to
   * @return Added parent node
   */
  def addParent(parentData: T_NodeData @uV): Node[T_NodeData @uV] = {
    if (isRoot) {
      containingGraph.rootNodes -= this
    }

    val newParent = new Node[T_NodeData @uV](parentData, containingGraph, containingGraph.allNodes.size)

    newParent.children += this
    parents += newParent

    containingGraph.rootNodes += newParent
    containingGraph.allNodes += newParent

    if (containingGraph.isEdgeLinkTrackingOn) containingGraph.edgeLinkTrackingDescriptions :+=
      new AddNewParentDescription(nodeIndexInContainingGraph, parentData)

    newParent
  }

  /**
   * Add a link between this node and another node in the same graph, with the other node as this node's parent.
   * Won't check for cycles, so don't try to link something as a parent if it's one of this childNode's descendants!!
   * @param nodeToLink Node to link to an existing parent
   */
  def addLinkToExistingParent(nodeToLink: Node[T_NodeData @uV]): Unit = {
    // TODO: Consider making Node an inner class of org.finra.datagenerator.common.Graph. That would prevent passing in a Node from a different graph,
    // because in Scala inner classes are scoped under the containing object, not the class.
    require(containingGraph == nodeToLink.containingGraph, "Trying to link a node from a different graph!")
    if (isRoot) containingGraph.rootNodes -= this
    parents += nodeToLink
    nodeToLink.children += this

    if (containingGraph.isEdgeLinkTrackingOn) containingGraph.edgeLinkTrackingDescriptions :+=
      new AddParentToExistingNodeDescription(nodeIndexInContainingGraph, nodeToLink.nodeIndexInContainingGraph)
  }

  /**
   * Add a link between this node and another node in the same graph, with the other node as this node's child.
   * Won't check for cycles, so don't try to link something as a child if it's one of this childNode's ancestors!!
   * @param nodeToLink Node to link to an existing child
   */
  def addLinkToExistingChild(nodeToLink: Node[T_NodeData @uV]): Unit = {
    // TODO: Consider making Node an inner class of org.finra.datagenerator.common.Graph. That would prevent passing in a Node from a different graph,
    // because in Scala inner classes are scoped under the containing object, not the class.
    require(containingGraph == nodeToLink.containingGraph, "Trying to link a node from a different graph!")
    if (nodeToLink.isRoot) containingGraph.rootNodes -= nodeToLink
    children += nodeToLink
    nodeToLink.parents += this

    if (containingGraph.isEdgeLinkTrackingOn) containingGraph.edgeLinkTrackingDescriptions :+=
      new AddChildToExistingNodeDescription(nodeIndexInContainingGraph, nodeToLink.nodeIndexInContainingGraph)
  }

  /**
   * Create a new node and link it as the child to this node.
   * @param childData Node to add child to
   * @return Added child node
   */
  def addChild(childData: T_NodeData @uV): Node[T_NodeData @uV] = {
    val newChild = new Node[T_NodeData @uV](childData, containingGraph, containingGraph.allNodes.size)
    children += newChild
    newChild.parents += this

    containingGraph.allNodes += newChild

    if (containingGraph.isEdgeLinkTrackingOn) containingGraph.edgeLinkTrackingDescriptions :+=
      new AddNewChildDescription(nodeIndexInContainingGraph, childData)

    newChild
  }


  /**
   * Creates a copy of the graph and adds a new node as a child from the analog of this node in the copied graph.
   * @param childData Data to add in child node
   * @return Added child node
   */
  def addChildToAnalogNodeInCopiedGraph(childData: T_NodeData @uV): Node[T_NodeData @uV] = {
    getAnalogInCopiedGraph.addChild(childData)
  }

  /**
   * Creates a copy of the graph and adds a new node as a parent from the analog of this node in the copied graph.
   * @param parentData Data to add in parent node
   * @return Added parent node
   */
  def addParentToAnalogNodeInCopiedGraph(parentData: T_NodeData @uV): Node[T_NodeData @uV] = {
    getAnalogInCopiedGraph.addParent(parentData)
  }

  // TODO: Need method to link an existing childNode as parent, in copied graph.

  /**
   * Create a copy of the containing graph and get the analogous node in the copied graph.
   * @return Node in copied graph
   */
  def getAnalogInCopiedGraph: Node[T_NodeData @uV] = {
    containingGraph.deepCopy.allNodes(nodeIndexInContainingGraph)
  }

  /**
   * Write this node, and all its edges to its children, in DOT format to an open stream.
   * @param out Output stream to write to
   * @param isSimplified Whether or not to write using simplifiedDisplayableElements instead of displayableElements
   */
  def writeDotFormatGraphVisualizationOfNodeToOpenStream(out: OutputStream, isSimplified: Boolean = false): Unit = {
    val elementsToDisplay = if (isSimplified) data.simplifiedDisplayableElements else data.displayableElements
    if (elementsToDisplay.nonEmpty) {
      out.write( s""""${data.displayableDataId}" [label="${
        elementsToDisplay.map(_.replace("|", "\\|")).mkString("|")
      }" shape="record"];\r\n""".getBytes("UTF-8"))
    }
    children.foreach(child => out.write( s""""${data.displayableDataId}"->"${child.data.displayableDataId}"\r\n""".getBytes("UTF-8")))
  }
}
