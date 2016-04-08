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

import java.io.{FileOutputStream, OutputStream}

import org.finra.datagenerator.common.Helpers.{RetryHelper, DotHelper, RandomHelper}
import org.finra.datagenerator.common.NodeData.DisplayableData
import org.finra.datagenerator.common.Helpers.CloningHelper.ObjectCloning
import scala.beans.BeanProperty
import scala.collection.mutable

/** *
  * Generic directed acyclic graph. Can have multiple root nodes.
  * Programmers should be careful not to create cycles, because this class does not check for cycles.
  * @tparam T Type of data stored in each graph childNode
  * @param initialNodeValue Create the graph with one childNode containing this supplied value.
  * @param isEdgeLinkTrackingOn Whether or not to maintain history of all events used to create graph.
  * @param _graphId org.finra.datagenerator.common.Graph ID. Can be set later using graphId setter method. If not set, will eventually default to UUID.
  * @param appendSharedDisplayIdsWithNumericalSuffix If true, if multiple nodes with same displayableDataId, appends with _1, _2, etc.
  */
class Graph[T <: DisplayableData](initialNodeValue: Option[T] = None, var isEdgeLinkTrackingOn: Boolean = false,
                                  private var _graphId: String = "", var appendSharedDisplayIdsWithNumericalSuffix: Boolean = true) {
  def this(graphId: String) {
    this(_graphId=graphId, appendSharedDisplayIdsWithNumericalSuffix = true)
  }
  def this(initialNodeValue: T, graphId: String) {
    this(initialNodeValue=Option(initialNodeValue), _graphId=graphId)
  }
  def this(initialNodeValue: T) {
    this(initialNodeValue=Option(initialNodeValue))
  }

  @BeanProperty
  var customSeed: Option[Long] = None

  @BeanProperty
  var customGlobalSeed: Option[Short] = None

  @BeanProperty
  var customBooleanAttributes = mutable.HashMap[String, Boolean]()

  @BeanProperty
  var customStringAttributes = mutable.HashMap[String, String]()

  @BeanProperty
  var userConfiguredGraphId = false

  /**
   * Get the graph ID.
   * @return org.finra.datagenerator.common.Graph ID
   */
  def graphId: String = {
    if (_graphId.isEmpty) _graphId = RandomHelper.randomUuid().replace('-','_')
    _graphId
  }

  /**
   * Set the graph ID.
   * @param value String to set as graph ID
   */
  def graphId_=(value: String): Unit = {
    _graphId = value.replace('-','_').replace(' ','_').replace("\"","")
    userConfiguredGraphId = true
  }

  /**
   * Java-style setter for graphId
   * @param value Value to set org.finra.datagenerator.common.Graph ID to
   */
  def setGraphId(value: String): Unit = {
    graphId = value
  }

  /**
   * Java-style getter for graphId
   * @return org.finra.datagenerator.common.Graph ID
   */
  def getGraphId: String = {
    graphId
  }

  if (_graphId != "") graphId = _graphId

  /**
   * All the root nodes in this graph (nodes with no parents, but excluding orphans)
   */
  @BeanProperty
  val rootNodes = new mutable.ArrayBuffer[Node[T]]()

  /**
   * All the nodes in this graph, in the sequence they were added.
   */
  @BeanProperty
  var allNodes = new mutable.ArrayBuffer[Node[T]]()

  /**
   * Used when appendSharedDisplayIdsWithNumericalSuffix is true, to ensure each node data has a unique displayable ID.
   */
  @BeanProperty
  var nodeIdCounters = collection.immutable.HashMap[String, Int]()

  /**
   * Optional structure that can be used for retracing the steps used to create the graph.
   */
  @BeanProperty
  var edgeLinkTrackingDescriptions = collection.immutable.List[EdgeCreationDescription[T]]()

  if (initialNodeValue != None) {
    addInitialNode(initialNodeValue.get)
  }

  /**
   * Add the first node in this graph.
   * @param newNodeValue Data to add as initial node in graph
   * @return Added node
   */
  def addInitialNode(newNodeValue: T): Node[T] = {
    require(allNodes.size == 0 && rootNodes.size == 0, s"Can't add initial node ${newNodeValue} because the graph is not empty.")

    if (isEdgeLinkTrackingOn) edgeLinkTrackingDescriptions :+= new AddInitialNodeDescription[T](newNodeValue)

    addNewRootNode(newNodeValue, track=false)
  }

  /**
   * Add a new node as a root in this graph.
   * @param newNodeValue Value to add as new root node in graph
   * @param track Whether or not to track this change (leave true by default -- overridden internally).
   * @return Added node
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
   * @param newNodeValue Data to add as new orphaned node
   * @return Orphaned node
   */
  def addNewOrphanedNode(newNodeValue: T): Node[T] = {
    // TODO: This would be better accomplished by having a separate graph with this single childNode.

    val newNode = new Node[T](newNodeValue, this, allNodes.size)
    allNodes += newNode

    if (isEdgeLinkTrackingOn) edgeLinkTrackingDescriptions :+= new AddNewOrphanedNodeDescription[T](newNodeValue)

    newNode
  }

  /**
   * Given a displayableDataId string, returns the (first) node having that ID if found.
   * @param displayableDataId String representing a node's displayableDataId
   * @return Option(Node) if found, else None
   */
  def getNodeByDisplayId(displayableDataId: String): Option[Node[T]] = {
    allNodes.find(node => node.data.displayableDataId.equals(displayableDataId))
  }

  /**
   * Writes this graph to a GraphViz "DOT" file -- there are various clients to read this kind of file, save to image, etc.
   * @param filepathToCreate String representing full local path of new file to create
   * @param isSimplified Whether or not to use simplifiedDisplayableElements instead of displayableElements when outputting
   * @param alsoWriteAsPng Whether or not to call dot.exe to convert the .gv file to .png when done writing
   */
  def writeDotFile(filepathToCreate: String, isSimplified: Boolean = false, alsoWriteAsPng: Boolean = true): Unit = {
    val writer = RetryHelper.retry(10, Seq(classOf[java.io.FileNotFoundException])){
      new FileOutputStream(filepathToCreate)
    }(_ => {
      Thread.sleep(100)
    })
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
   * @param out Open output stream to write to
   * @param isSimplified Whether or not to use simplifiedDisplayableElements instead of displayableElements when outputting
   */
  def writeDotFileToOpenStream(out: OutputStream, isSimplified: Boolean = false): Unit = {
    // Write any graph-specific attributes
    out.write(s"""digraph "Graph_${graphId}" {\r\n""".getBytes("UTF-8"))

    // Write all labels and edges
    allNodes.foreach(_.writeDotFormatGraphVisualizationOfNodeToOpenStream(out, isSimplified = isSimplified))

    // End graph
    out.write("}".getBytes("UTF-8"))
  }

  /**
   * Create a copy of this graph
   * @return Copied graph
   */
  def deepCopy: Graph[T] = {
    val copiedGraph = new Graph[T](
      initialNodeValue = Option(allNodes.head.data.deepClone(Set(allNodes.head.data.dataType.getClass))),
      isEdgeLinkTrackingOn = false,
      _graphId = _graphId,
      appendSharedDisplayIdsWithNumericalSuffix = false
    )
    copiedGraph.edgeLinkTrackingDescriptions = edgeLinkTrackingDescriptions.deepClone()
    copiedGraph.customSeed = customSeed
    copiedGraph.customGlobalSeed = customGlobalSeed
    copiedGraph.nodeIdCounters = nodeIdCounters.deepClone()
    copiedGraph.customBooleanAttributes = customBooleanAttributes.deepClone()
    copiedGraph.customStringAttributes = customStringAttributes.deepClone()
    //copiedGraph.customBooleanAttributes = customBooleanAttributes.deepClone()
    allNodes.tail.foreach(node => copiedGraph.addNewRootNode(node.data.deepClone(Set(node.data.dataType.getClass))))
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
   * @return String uniquely representing this graph's structure
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
