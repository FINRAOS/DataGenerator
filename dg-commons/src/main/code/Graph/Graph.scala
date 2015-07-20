package Graph

import java.io.{FileOutputStream, OutputStream}

import Helpers.{DotHelper, RandomHelper}
import NodeData.DisplayableData

import scala.collection.mutable

/** *
  * Generic directed acyclic graph. Can have multiple root nodes.
  * Programmers should be careful not to create cycles, because this class does not check for cycles.
  * @tparam T Type of data stored in each graph childNode
  * @param initialNodeValue Create the graph with one childNode containing this supplied value.
  * @param isEdgeLinkTrackingOn Whether or not to maintain history of all events used to create graph.
  * @param _graphId Graph ID. Can be set later using graphId setter method. If not set, will eventually default to UUID.
  * @param appendSharedDisplayIdsWithNumericalSuffix If true, if multiple nodes with same displayableDataId, appends with _1, _2, etc.
  */
class Graph[T <: DisplayableData](initialNodeValue: Option[T] = None, var isEdgeLinkTrackingOn: Boolean = false, private var _graphId: String = "", var appendSharedDisplayIdsWithNumericalSuffix: Boolean = false) {
  def this(graphId: String) {
    this(_graphId=graphId, appendSharedDisplayIdsWithNumericalSuffix = true)
  }
  def this(initialNodeValue: T, graphId: String) {
    this(initialNodeValue=Some(initialNodeValue), _graphId=graphId)
  }

  var customSeed: Option[Long] = None
  var customGlobalSeed: Option[Short] = None
  val customBooleanAttributes = mutable.HashMap[String, Boolean]()
  var userConfiguredGraphId = false

  /**
   * Get the graph ID.
   * @return
   */
  def graphId: String = {
    if (_graphId.isEmpty) _graphId = RandomHelper.randomUuid().replace('-','_')
    _graphId
  }

  /**
   * Set the graph ID.
   * @param value
   */
  def graphId_=(value: String) = {
    _graphId = value.replace('-','_').replace(' ','_').replace("\"","")
    userConfiguredGraphId = true
  }

  if (_graphId != "") graphId = _graphId

  /**
   * All the root nodes in this graph (nodes with no parents, but excluding orphans)
   */
  val rootNodes = new mutable.ArrayBuffer[Node[T]]()

  /**
   * All the nodes in this graph, in the sequence they were added.
   */
  var allNodes = new mutable.ArrayBuffer[Node[T]]()

  /**
   * Used when appendSharedDisplayIdsWithNumericalSuffix is true, to ensure each node data has a unique displayable ID.
   */
  var nodeIdCounters = collection.immutable.HashMap[String, Int]()

  /**
   * Optional structure that can be used for retracing the steps used to create the graph.
   */
  var edgeLinkTrackingDescriptions = collection.immutable.List[EdgeCreationDescription[T]]()

  if (initialNodeValue != None) {
    addInitialNode(initialNodeValue.get)
  }

  /**
   * Add the first node in this graph.
   * @param newNodeValue
   * @return
   */
  def addInitialNode(newNodeValue: T): Node[T] = {
    assert(allNodes.size == 0 && rootNodes.size == 0)

    if (isEdgeLinkTrackingOn) edgeLinkTrackingDescriptions :+= new AddInitialNodeDescription[T](newNodeValue)

    addNewRootNode(newNodeValue, track=false)
  }

  /**
   * Add a new node as a root in this graph.
   * @param newNodeValue
   * @param track
   * @return
   */
  def addNewRootNode(newNodeValue: T, track: Boolean = true): Node[T] = {
    val newNode = new Node[T](newNodeValue, this, allNodes.size)
    allNodes += newNode
    rootNodes += newNode

    if (isEdgeLinkTrackingOn && track) edgeLinkTrackingDescriptions :+= new AddNewRootNodeDescription[T](newNodeValue)

    newNode
  }

  /**
   * Add a new node to the graph, but don't link it to anything.
   * TODO: This would be better accomplished by having a separate graph with this single childNode.
   * @param newNodeValue
   * @return
   */
  def addNewOrphanedNode(newNodeValue: T): Node[T] = {
    // TODO: This would be better accomplished by having a separate graph with this single childNode.

    val newNode = new Node[T](newNodeValue, this, allNodes.size)
    allNodes += newNode

    if (isEdgeLinkTrackingOn) edgeLinkTrackingDescriptions :+= new AddNewOrphanedNodeDescription[T](newNodeValue)

    newNode
  }

  def getNodeByDisplayId(displayableDataId: String): Option[Node[T]] = {
    allNodes.find(node => node.data.displayableDataId.equals(displayableDataId))
  }

  /**
   * Writes this graph to a GraphViz "DOT" file -- there are various clients to read this kind of file, save to image, etc.
   * @param filepathToCreate
   * @param isSimplified
   * @param alsoWriteAsPng
   */
  def writeDotFile(filepathToCreate: String, isSimplified: Boolean = false, alsoWriteAsPng: Boolean = true) = {
    val writer = new FileOutputStream(filepathToCreate)
    try {
      writeDotFileToOpenStream(writer, isSimplified = isSimplified)
    }
    finally {
      writer.close()
    }

    if (alsoWriteAsPng) {
      DotHelper.writeDotFileAsPng(filepathToCreate)
    }
  }

  /**
   * Writes this graph to a GraphViz "DOT" file stream -- there are various clients to read this kind of file, save to image, etc.
   * @param out
   * @param isSimplified
   */
  def writeDotFileToOpenStream(out: OutputStream, isSimplified: Boolean = false): Unit = {
    // Write any graph-specific attributes
    out.write(s"digraph Graph_${graphId} {\r\n".getBytes("UTF-8"))

    // Write all labels and edges
    allNodes.foreach(_.writeDotFormatGraphVisualizationOfNodeToOpenStream(out, isSimplified = isSimplified))

    // End graph
    out.write("}".getBytes("UTF-8"))
  }

  /**
   * Create a copy of this graph
   * @return
   */
  def deepCopy: Graph[T] = {
    //this.deepClone // Has performance issues (uses reflection)... so let's implement it ourselves instead.
    val copiedGraph = new Graph[T](initialNodeValue = Some(allNodes(0).data), isEdgeLinkTrackingOn = false, _graphId = _graphId, appendSharedDisplayIdsWithNumericalSuffix = false)
    copiedGraph.edgeLinkTrackingDescriptions = edgeLinkTrackingDescriptions
    copiedGraph.customSeed = customSeed
    copiedGraph.customGlobalSeed = customGlobalSeed
    copiedGraph.nodeIdCounters = nodeIdCounters
    //copiedGraph.customBooleanAttributes = customBooleanAttributes
    allNodes.tail.foreach(node => copiedGraph.addNewRootNode(node.data))
    allNodes.foreach(node => {
      val nodeInNewGraph = copiedGraph.allNodes(node.nodeIndexInContainingGraph)
      node.parents.foreach(parent => {
        val parentInNewGraph = copiedGraph.allNodes(parent.nodeIndexInContainingGraph)
        if (!nodeInNewGraph.parents.contains(parentInNewGraph)) {
          nodeInNewGraph.addLinkToExistingParent(parentInNewGraph)
        }
      })
    })

    copiedGraph.isEdgeLinkTrackingOn = isEdgeLinkTrackingOn
    copiedGraph.appendSharedDisplayIdsWithNumericalSuffix = appendSharedDisplayIdsWithNumericalSuffix
    copiedGraph
  }

  /*
  /**
   * Used for testing graph isomorphism.
   * @return
   */
  def getStructuralMD5: String = {
    // TODO: Implement. This is a graph isomorphism problem, which is not yet known if possible in polynomial time.
    // Look into using nauty - http://stackoverflow.com/questions/14532164/hash-value-for-directed-acyclic-graph

    // Done by ignoring graph ID, node order, and node data.
    // Each node's data is represented using DisplayableData.getStructuralMD5. This is not guaranteed to be in a form
    // usable for comparing isomorphism, because some nodes may have IDs that perhaps should be ignored for
    // comparison isomorphism... But if we need to compare isomorphism for any data type, we should make sure that
    // type's data overrides getStructuralMD5 in such a way that two nodes return the same value if considered equal
    // for purposes of isomorphism.


    ???
  }
  */
}