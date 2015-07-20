package GraphEngine

import Graph.Node
import NodeData.NodeDataType.NodeDataType
import NodeData.{NodeDataTypes, NodeDataStub, DisplayableData, NodeData}
import Helpers.RandomHelper
import scala.annotation.unchecked.{uncheckedVariance => uV}
import scala.collection.mutable.ListBuffer

/**
 * Responsible for creating new linked nodes from a specified node, using specified predicate and create functions.
 */
trait DataNodeGenerator[+T_NodeData <: DisplayableData,
                        +T_NodeDataTypeData <: NodeData with DisplayableData,
                        // When we try to fill in the type parameters, we get compilation errors. Replacing them with implicit type parameters works.
                        // Not sure the reason, maybe even a compiler bug, because all the types in the comments seem to check out.
                        +T_NodeDataStub <: NodeDataStub[_,_,_,_], //[T_NodeDataType, T_NodeData, T_NodeDataTypes, T_NodeDataStub],
                        +T_NodeDataType <: NodeDataType[_,_,_,_], //[T_NodeData, T_NodeDataStub, T_NodeDataTypes, T_NodeDataType],
                        +T_NodeDataTypes <: NodeDataTypes[_,_,_,_]] { //[T_NodeData, T_NodeDataStub, T_NodeDataType, T_NodeDataTypes]] {
  /**
   *
   * @param eventNode Node from which to generate and link new child or parent nodes
   * @param maxToGenerate Number of nodes left to create
   * @param stateTransitionPredicates List of event types and predicates defining whether or not they should be generated. Predicate will typically be a probability function.
   * @param createFunction Function to create the new linked childNode if the predicate passes
   * @return All newly created nodes
   */
  def generateLinkedNodes(eventNode: Node[T_NodeData @uV],
                          maxToGenerate: Long,
                          stateTransitionPredicates: ListBuffer[(T_NodeDataType @uV, (Node[T_NodeData @uV] => Boolean))],
                          createFunction: (T_NodeDataType @uV) => Node[T_NodeData @uV]
                           ): collection.immutable.Vector[Node[T_NodeData]] = {

    val newNodes = scala.collection.mutable.ArrayBuffer[Node[T_NodeData]]()

    if (stateTransitionPredicates.length == 0) return newNodes.toVector

    var generatedCount = 0

    // For each possible next state (iterated in random order),
    // Generate transition based on probability. Keep generating more until the predicate returns false,
    // Or until we're out of shares, or until we're at max # of states in lifecycle.
    // If predicate returns false, continue same procedure for next states in iteration.
    // If we've exhausted all possible next states and still have some shares left or # of states
    // left in lifecycle, that's ok -- just return.

    var exitGeneration = false

    while (stateTransitionPredicates.nonEmpty && !exitGeneration) {

      // Gen rand int from 0 to list size - 1. Use that to get next event type & prob. Remove from list.
      // Evaluate prob repeatedly until false, then repeat.
      val nextIndexToGet = RandomHelper.randWithConfiguredSeed.nextInt(stateTransitionPredicates.length)

      val nextEventAndEvaluationPredicate = stateTransitionPredicates.remove(nextIndexToGet)
      val nextEventType = nextEventAndEvaluationPredicate._1
      val evaluationPredicate = nextEventAndEvaluationPredicate._2

      // Checking against maxToGenerate will lead to event lifecycles that are not always finished (e.g.,
      // might have a routed event but no new event to link on the routed side.
      // Eventually we should be able to extract errors and warnings from such a graph.
      while (evaluationPredicate(eventNode) && !exitGeneration) {
        if (generatedCount < maxToGenerate) {
          newNodes += createFunction(nextEventType)
          generatedCount += 1
        } else exitGeneration = true
      }
    }
    newNodes.toVector
  }
}
