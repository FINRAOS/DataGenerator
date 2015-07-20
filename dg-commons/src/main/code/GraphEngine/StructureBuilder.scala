package GraphEngine

import Graph.{Graph, Node}
import NodeData.NodeDataType.NodeDataType
import NodeData.{NodeDataTypes, NodeData, NodeDataStub}
import scala.annotation.unchecked.{uncheckedVariance => uV}
import scala.collection.immutable

/**
 * Builds graph structures
 */
abstract class StructureBuilder[+T_NodeData <: NodeData,
                       +T_NodeDataType <: NodeDataType[T_NodeData, T_NodeDataStub, T_NodeDataTypes @uV, T_NodeDataType],
                       +T_NodeDataStub <: NodeDataStub[T_NodeDataType, T_NodeData, T_NodeDataTypes, T_NodeDataStub],
                       +T_NodeDataTypes <: NodeDataTypes[T_NodeData, T_NodeDataStub, T_NodeDataType, T_NodeDataTypes]]{
  /**
   * Provides info about all possible types and the subset of all initial types
   * @return
   */
  protected def nodeDataTypes: T_NodeDataTypes

  /**
   * Defines how to create parents and children of specified types from a specified node
   */
  lazy val dataTransitions = nodeDataTypes.dataTransitions

  /**
   * Build all combinations of graph structures for generic event stubs of a maximum length
   * @param length
   * @return
   */
  def generateAllNodeDataTypeGraphCombinationsOfLength(length: Int): immutable.Vector[Graph[T_NodeDataStub @uV]] = {
    if (length < 0) throw new IllegalArgumentException(s"Length (passed in as $length) must be positive.")

    var graphs =
      if (length == 0) {
        immutable.Vector[Graph[T_NodeDataStub @uV]]()
      } else {
        getAllSingleNodeGraphs
      }

    var remainingLength = length - 1

    // TODO: Need to remove duplicates.
    // E.g., consider a node type "SomeType," which is an initial node type and can have another "SomeType" as
    // a parent and/or child.
    // One 2-node graph: Start with "SomeType" and add child "SomeType"
    // Another 2-node graph: Start with "SomeType" and add parent "SomeType"
    // These graphs are structurally equivalent and the duplicates should be removed.
    // This should be doable if we add a method to Graph to get hash code of the structural parts of the graph
    // -- i.e., ignore graph ID and ignore node-creation order. Then, instead of a sequential data structure,
    // we can add the graphs to a HashMap and use this "hash code" as the key, thus ensuring we don't add the
    // same graph twice.

    while (remainingLength > 0) {
      graphs ++= getAllGraphsHavingOneAdditionalLink(graphs)

      remainingLength -= 1
    }

    graphs
  }

  /**
   * Get all allowable graphs containing only one node
   * @return
   */
  def getAllSingleNodeGraphs: collection.immutable.Vector[Graph[T_NodeDataStub @uV]] = {
    var graphs = collection.immutable.Vector[Graph[T_NodeDataStub]]()

    nodeDataTypes.allInitialDataTypes.foreach(initialType => {
      // TODO: Can this be done without the cast? For some reason the compiler is unable to coerce the type even though we know it's correct.
      graphs :+= new Graph(Some(initialType.asStub.asInstanceOf[T_NodeDataStub]), isEdgeLinkTrackingOn = false, appendSharedDisplayIdsWithNumericalSuffix = true)
    })
    graphs
  }

  def getCandidateExistingParentNodes(node: Node[T_NodeDataStub @uV]): collection.immutable.Vector[Node[T_NodeDataStub @uV]] = {
    (for (nodeInGraph <- node.containingGraph.allNodes if /*nodeDataTypes.*/dataTransitions.isStubLinkToExistingParentAllowed(node, nodeInGraph)) yield nodeInGraph).toVector
  }

  // TODO: Would be nice to reduce the duplicate code of the following two methods.

  /**
   * Get all the graphs based on the graph the passed-in node belongs to, and adding one of each possible child to the
   * specified node, plus for each graph having a new possible child, generating all possible graphs with new links
   * between existing nodes.
   * @param node
   * @param nodeDataStub
   * @return
   */
  def getCopiedGraphsFromAddingChildToAnalogNodeAndLinkingAllPossibleExistingParents(node: Node[T_NodeDataStub @uV], nodeDataStub: (T_NodeDataStub @uV)): immutable.Vector[Graph[T_NodeDataStub @uV]] = {
    val initialGraph = node.addChildToAnalogNodeInCopiedGraph(nodeDataStub).containingGraph
    var graphs = immutable.Vector[Graph[T_NodeDataStub @uV]](initialGraph)

    val candidateParents = getCandidateExistingParentNodes(node).toSet

    // Get all edge combinations from this childNode to candidates subsets, and add each as a new graph.
    candidateParents.subsets(candidateParents.size).foreach(candidateParentsSet => {
      val newGraph = initialGraph.deepCopy

      candidateParentsSet.foreach(parentToAdd => {
        newGraph.allNodes(newGraph.allNodes.size - 1).addLinkToExistingParent(newGraph.allNodes(parentToAdd.nodeIndexInContainingGraph))
      })
      graphs :+= newGraph
    })

    graphs
  }

  /**
   * Get all the graphs based on the graph the passed-in node belongs to, and adding one of each possible parent to the
   * specified node, plus for each graph having a new possible parent, generating all possible graphs with new links
   * between existing nodes.
   * @param node
   * @param nodeDataStub
   * @return
   */
  def getCopiedGraphsFromAddingParentToAnalogNodeAndLinkingAllPossibleExistingParents(node: Node[T_NodeDataStub @uV], nodeDataStub: (T_NodeDataStub @uV)): immutable.Vector[Graph[T_NodeDataStub @uV]] = {
    val initialGraph = node.addChildToAnalogNodeInCopiedGraph(nodeDataStub).containingGraph
    var graphs = immutable.Vector[Graph[T_NodeDataStub @uV]](initialGraph)

    val candidateParents = getCandidateExistingParentNodes(node).toSet

    // Get all edge combinations from this childNode to candidates subsets, and add each as a new graph.
    candidateParents.subsets(candidateParents.size).foreach(candidateParentsSet => {
      val newGraph = initialGraph.deepCopy

      candidateParentsSet.foreach(parentToAdd => {
        newGraph.allNodes(newGraph.allNodes.size - 1).addLinkToExistingParent(newGraph.allNodes(parentToAdd.nodeIndexInContainingGraph))
      })
      graphs :+= newGraph
    })

    graphs
  }

  /**
   * Get all the graphs based on the graph the passed-in node belongs to, and adding one of each possible child to the
   * specified node.
   * @param node
   * @return
   */
  def getAllGraphsHavingOneAdditionalChildFromNode(node: Node[T_NodeDataStub @uV]): Seq[Graph[T_NodeDataStub @uV]] = {
    var newGraphs = collection.immutable.Vector[Graph[T_NodeDataStub]]()
    node.data.dataType.getAllowableChildTypes(node).foreach(allowableChild => {
      newGraphs ++= getCopiedGraphsFromAddingChildToAnalogNodeAndLinkingAllPossibleExistingParents(node, allowableChild.asStub.asInstanceOf[T_NodeDataStub])
    })
    newGraphs
  }

  /**
   * Get all the graphs based on the graph the passed-in node belongs to, and adding one of each possible parent to the
   * specified node.
   * @param node
   * @return
   */
  def getAllGraphsHavingOneAdditionalParentFromNode(node: Node[T_NodeDataStub @uV]): Seq[Graph[T_NodeDataStub @uV]] = {
    var newGraphs = collection.immutable.Vector[Graph[T_NodeDataStub]]()
    node.data.dataType.getAllowableParentTypes(node).foreach(allowableChild => {
      newGraphs ++= getCopiedGraphsFromAddingParentToAnalogNodeAndLinkingAllPossibleExistingParents(node, allowableChild.asStub.asInstanceOf[T_NodeDataStub])
    })
    newGraphs
  }

  /**
   * Get all the graphs having one additional node.
   * @param graph
   * @return
   */
  def getAllGraphsHavingOneAdditionalLink(graph: Graph[T_NodeDataStub @uV]): immutable.Vector[Graph[T_NodeDataStub @uV]] = {
    var newGraphs = immutable.Vector[Graph[T_NodeDataStub @uV]]()

    graph.allNodes.foreach(node => {
      newGraphs ++= getAllGraphsHavingOneAdditionalChildFromNode(node)
      newGraphs ++= getAllGraphsHavingOneAdditionalParentFromNode(node)

      // Every time you add a new childNode, whether parent or child, also add all structure combinations with the same
      // nodes but additional edges from the added childNode.
      // e.g., The graph after adding the childNode.
      // The graph after adding the childNode + linking to 1 existing parent.
      // The graph after adding the childNode + linking to 2 existing parents.
      // For all possible links having the newly added childNode as a child.
    })

    newGraphs
  }

  /**
   * For each graph, get all the graphs having one additional node.
   * @param graphs
   * @return
   */
  def getAllGraphsHavingOneAdditionalLink(graphs: immutable.Vector[Graph[T_NodeDataStub @uV]]): immutable.Vector[Graph[T_NodeDataStub @uV]] = {
    var newGraphs = immutable.Vector[Graph[T_NodeDataStub @uV]]()

    graphs.foreach(graph => newGraphs ++= getAllGraphsHavingOneAdditionalLink(graph))

    newGraphs
  }
}