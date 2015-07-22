package Graph

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

import NodeData.DisplayableData
import scala.annotation.unchecked.{uncheckedVariance => uV}
import scala.collection.mutable

/**
 * Generic childNode in a directed acyclic graph
 */
class Node[+T_NodeData <: DisplayableData](_data: T_NodeData, _containingGraph: Graph[T_NodeData], var nodeIndexInContainingGraph: Int) {
  val containingGraph: Graph[T_NodeData @uV] = _containingGraph
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
    // else if overrideDisplayableDataId is nonEmpty, we assume it's unique and we don't care to check (yet), but we could add this in later. If it's not, the DOT graph output will be wrong.
  }

  val parents: mutable.ArrayBuffer[Node[T_NodeData @uV]] = new mutable.ArrayBuffer[Node[T_NodeData]]()
  val children: mutable.ArrayBuffer[Node[T_NodeData @uV]] = new mutable.ArrayBuffer[Node[T_NodeData]]()

  lazy val descendantsInternal: mutable.HashSet[Node[T_NodeData @uV]] = new mutable.HashSet[Node[T_NodeData]]()

  /**
   * Remove this node from its containing graph
   */
  def delete(): Unit = {
    if (isRoot) {
      containingGraph.rootNodes -= this
      children.foreach(child => {
        child.parents -= this
        if (child.isRoot) {
          containingGraph.rootNodes += child
        }
      })
    }
    containingGraph.allNodes.indices.filter(_ > nodeIndexInContainingGraph).foreach(index => {
      containingGraph.allNodes(index).nodeIndexInContainingGraph -= 1
    })
    containingGraph.allNodes -= this
  }

  /**
   * Get all the descendant nodes (children of this node, those children's children, to the bottom).
   * @return
   */
  def getDescendants(): mutable.HashSet[Node[T_NodeData @uV]] = {
    descendantsInternal.clear()
    if (children.size == 0) return descendantsInternal
    children.foreach(child => {
      descendantsInternal ++= child.getDescendants()
    })
    descendantsInternal
  }

  /**
   * Whether or not this is a root node
   * @return
   */
  def isRoot : Boolean = {
    parents.isEmpty
  }

  /**
   * Create a new node and link it as the parent to this node.
   * @param data
   * @return
   */
  def addParent(data: T_NodeData @uV): Node[T_NodeData @uV] = {
    if (isRoot) {
      containingGraph.rootNodes -= this
    }

    val newParent = new Node[T_NodeData @uV](data, containingGraph, containingGraph.allNodes.size)

    newParent.children += this
    parents += newParent

    containingGraph.rootNodes += newParent
    containingGraph.allNodes += newParent

    if (containingGraph.isEdgeLinkTrackingOn) containingGraph.edgeLinkTrackingDescriptions :+= new AddNewParentDescription(nodeIndexInContainingGraph, data)

    newParent
  }

  /**
   * Add a link between this node and another node in the same graph, with the other node as this node's parent.
   * Won't check for cycles, so don't try to link something as a parent if it's one of this childNode's descendants!!
   * @param nodeToLink Node to link to an existing parent
   */
  def addLinkToExistingParent(nodeToLink: Node[T_NodeData @uV]): Unit = {
    // TODO: Consider making Node an inner class of Graph. That would prevent passing in a Node from a different graph,
    // because in Scala inner classes are scoped under the containing object, not the class.
    assert(containingGraph == nodeToLink.containingGraph, "Trying to link a node from a different graph!")
    if (isRoot) containingGraph.rootNodes -= this
    parents += nodeToLink
    nodeToLink.children += this

    if (containingGraph.isEdgeLinkTrackingOn) containingGraph.edgeLinkTrackingDescriptions :+= new AddParentToExistingNodeDescription(nodeIndexInContainingGraph, nodeToLink.nodeIndexInContainingGraph)
  }

  /**
   * Add a link between this node and another node in the same graph, with the other node as this node's child.
   * Won't check for cycles, so don't try to link something as a child if it's one of this childNode's ancestors!!
   * @param nodeToLink Node to link to an existing child
   */
  def addLinkToExistingChild(nodeToLink: Node[T_NodeData @uV]): Unit = {
    // TODO: Consider making Node an inner class of Graph. That would prevent passing in a Node from a different graph,
    // because in Scala inner classes are scoped under the containing object, not the class.
    assert(containingGraph == nodeToLink.containingGraph, "Trying to link a node from a different graph!")
    if (nodeToLink.isRoot) containingGraph.rootNodes -= nodeToLink
    children += nodeToLink
    nodeToLink.parents += this

    if (containingGraph.isEdgeLinkTrackingOn) containingGraph.edgeLinkTrackingDescriptions :+= new AddChildToExistingNodeDescription(nodeIndexInContainingGraph, nodeToLink.nodeIndexInContainingGraph)
  }

  /**
   * Create a new node and link it as the child to this node.
   * @param data
   * @return
   */
  def addChild(data: T_NodeData @uV): Node[T_NodeData @uV] = {
    val newChild = new Node[T_NodeData @uV](data, containingGraph, containingGraph.allNodes.size)
    children += newChild
    newChild.parents += this

    containingGraph.allNodes += newChild

    if (containingGraph.isEdgeLinkTrackingOn) containingGraph.edgeLinkTrackingDescriptions :+= new AddNewChildDescription(nodeIndexInContainingGraph, data)

    newChild
  }


  /**
   * Creates a copy of the graph and adds a new node as a child from the analog of this node in the copied graph.
   * @param childData
   * @return
   */
  def addChildToAnalogNodeInCopiedGraph(childData: T_NodeData @uV): Node[T_NodeData @uV] = {
    getAnalogInCopiedGraph.addChild(childData)
  }

  /**
   * Creates a copy of the graph and adds a new node as a parent from the analog of this node in the copied graph.
   * @param parentData
   * @return
   */
  def addParentToAnalogNodeInCopiedGraph(parentData: T_NodeData @uV): Node[T_NodeData @uV] = {
    getAnalogInCopiedGraph.addParent(parentData)
  }

  // TODO: Need method to link an existing childNode as parent, in copied graph.

  /**
   * Create a copy of the containing graph and get the analogous node in the copied graph.
   * @return
   */
  def getAnalogInCopiedGraph: Node[T_NodeData @uV] = {
    containingGraph.deepCopy.allNodes(nodeIndexInContainingGraph)
  }

  /**
   * Write this node, and all its edges to its children, in DOT format to an open stream.
   * @param out
   * @param isSimplified
   */
  def writeDotFormatGraphVisualizationOfNodeToOpenStream(out: OutputStream, isSimplified: Boolean = false): Unit = {
    val elementsToDisplay = if (isSimplified) data.simplifiedDisplayableElements else data.displayableElements
    out.write( s""""${data.displayableDataId}" [label="${elementsToDisplay.map(_.replace("|", "\\|")).mkString("|")}" shape="record"];\r\n""".getBytes("UTF-8"))
    children.foreach(child => out.write( s""""${data.displayableDataId}"->"${child.data.displayableDataId}"\r\n""".getBytes("UTF-8")))
  }
}
