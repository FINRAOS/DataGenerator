package NodeData

import Graph.Node
import GraphEngine.{DefaultNodeGenerator, DataNodeGenerator}
import scala.annotation.unchecked.{uncheckedVariance => uV}
import scala.collection.mutable.ListBuffer

/**
 * Type & metadata for data stored in node of a graph
 */
object NodeDataType {
  /**
   * Each object extending this trait can be thought of as a value in an enumeration of all node data types.
   * @tparam T_NodeData Data that uses this type
   */
  abstract class NodeDataType[+T_NodeData <: NodeData,
                     +T_NodeDataStub <: NodeDataStub[T_ThisType, T_NodeData, T_NodeDataTypes, T_NodeDataStub],
                     +T_NodeDataTypes <: NodeDataTypes[T_NodeData @uV, T_NodeDataStub @uV, T_ThisType @uV, T_NodeDataTypes @uV],
                     +T_ThisType <: NodeDataType[T_NodeData, T_NodeDataStub, T_NodeDataTypes, T_ThisType]] extends DisplayableData {
    /**
     * Specifies what generator to use when trying to create a new child or parent from a specified data node
     */
    lazy val dataNodeGenerator: DataNodeGenerator[T_NodeData, T_NodeData, T_NodeDataStub, T_ThisType, T_NodeDataTypes] = new DefaultNodeGenerator[T_NodeData, T_NodeData, T_NodeDataStub, T_ThisType, T_NodeDataTypes]()

    /**
     * Specifies what generator to use when trying to create a new child or parent from a specified stub node
     */
    lazy val dataStubNodeGenerator: DataNodeGenerator[T_NodeDataStub, T_NodeData, T_NodeDataStub, T_ThisType, T_NodeDataTypes] = new DefaultNodeGenerator[T_NodeDataStub, T_NodeData, T_NodeDataStub, T_ThisType, T_NodeDataTypes]()

    /**
     * Wrapper that specifies all possible node data types in this domain, and possible metadata/groupings thereof.
     */
    def nodeDataTypes: T_NodeDataTypes

    /**
     * Specifies an object of a class implementing DataTransitions, which define, from each type, how to create a
     * child or parent of each allowable child/parent type.
     */
    lazy val dataTransitions = nodeDataTypes.dataTransitions//.asInstanceOf[DataTransitions[T_NodeData, T_ThisType, T_NodeDataStub]]

    /**
     * Creates a stub wrapper around this type
     * @return
     */
    def asStub: T_NodeDataStub

    /**
     * Name that uniquely identifies this type
     */
    val name: String

    /**
     * Name that uniquely identifies this type, to be used for display
     * @return
     */
    override def defaultDisplayableDataId = name

    /**
     * Types are considered the same based on their unique names
     * @param that
     * @return
     */
    override def equals(that: Any) = that match {
      case that: this.type => true
      case _ => false
    }
    /**
     * Hash code is simply the name
     * @return
     */
    override def hashCode = name.hashCode

    /**
     * Gets a sequence of all the node data types that may be created as a child from the current node
     * @param nodeOfThisType
     * @return Sequence of NodeDataType
     */
    def getAllowableChildTypes(nodeOfThisType: Node[T_NodeDataStub @uV]): Seq[T_ThisType]

    /**
     * Gets a sequence of all the node data types that may be created as a parent from the current node
     * @param nodeOfThisType
     * @return Sequence of NodeDataType
     */
    def getAllowableParentTypes(nodeOfThisType: Node[T_NodeDataStub @uV]): Seq[T_ThisType]

    /**
     * Gets a sequence of predicates used by the child-generator methods. These predicate functions determine, for each allowable child type,
     * whether or not, when deciding to add a child, if the generator will choose to add a child of that type.
     * @param node Node from which we may wish to add a child
     * @param maxToGenerate Maximum number of nodes in graph
     * @param probabilityMultiplier Example usage: Predicate function might determine true/false based on a random function. Multiplier will make that function more probable to return true.
     * @tparam T_DisplayableData Type of data. In this case it will be either a Data or a Stub.
     * @return Mutable list of each possible child data type mapped to a predicate function used to determine whether or not to add a child of that type 
     */
    def childStateTransitionPredicates[T_DisplayableData <: DisplayableData](node: Node[T_DisplayableData @uV], maxToGenerate:Int, probabilityMultiplier: Int): ListBuffer[(T_ThisType @uV, (Node[T_DisplayableData @uV] => Boolean))]

    /**
     * Gets a sequence of predicates used by the parent-generator methods. These predicate functions determine, for each allowable parent type,
     * whether or not, when deciding to add a parent, if the generator will choose to add a parent of that type.
     * @param node Node from which we may wish to add a parent
     * @param maxToGenerate Maximum number of nodes in graph
     * @param probabilityMultiplier Example usage: Predicate function might determine true/false based on a random function. Multiplier will make that function more probable to return true.
     * @tparam T_DisplayableData Type of data. In this case it will be either a Data or a Stub.
     * @return Mutable list of each possible parent data type mapped to a predicate function used to determine whether or not to add a parent of that type 
     */
    def parentStateTransitionPredicates[T_DisplayableData <: DisplayableData](node: Node[T_DisplayableData @uV], maxToGenerate:Int, probabilityMultiplier: Int): ListBuffer[(T_ThisType @uV, (Node[T_DisplayableData @uV] => Boolean))]

    /**
     * Probabilistically link this node to another existing node such that the other node should be a parent of this node.
     * Does nothing by default, but may be overridden. Also not yet called from anywhere, but eventually we may have some generation engine that uses this.
     * @param dataNode Node to be linked as a child of another node that already exists in the same graph
     */
    def probabilisticallyLinkToExistingParentDataNode(dataNode: Node[T_NodeData @uV]): Unit

    /**
     * Probabilistically link this node to another existing node such that the other node should be a parent of this node.
     * Does nothing by default, but may be overridden. Also not yet called from anywhere, but eventually we may have some generation engine that uses this.
     * @param stubNode Node to be linked as a child of another node that already exists in the same graph
     */
    def probabilisticallyLinkToExistingParentStubNode(stubNode: Node[T_NodeDataStub @uV]): Unit

    /**
     * Given a stub node, creates new child stub nodes based on the allowable child types and defined predicates specifying whether or not to add a child of a certain type
     * @param stubNode Node of a data stub
     * @param maxToGenerate Maximum number of nodes in graph
     * @param probabilityMultiplier Example usage: Predicate function might determine true/false based on a random function. Multiplier will make that function more probable to return true.
     * @return
     */
    def generateAndAddChildStubs(stubNode: Node[T_NodeDataStub @uV], maxToGenerate: Int, probabilityMultiplier: Int): Vector[Node[T_NodeDataStub @uV]] = {
      dataStubNodeGenerator.generateLinkedNodes(stubNode, maxToGenerate, childStateTransitionPredicates[T_NodeDataStub @uV](stubNode, maxToGenerate, probabilityMultiplier),
        (nextEventType: T_ThisType) => stubNode.addChild(nextEventType.asStub)
      )
    }

    /**
     * Given a stub node, creates new parent stub nodes based on the allowable child types and defined predicates specifying whether or not to add a parent of a certain type
     * @param stubNode Node of a data stub
     * @param maxToGenerate Maximum number of nodes in graph
     * @param probabilityMultiplier Example usage: Predicate function might determine true/false based on a random function. Multiplier will make that function more probable to return true.
     * @return
     */
    def generateAndAddParentStubs(stubNode: Node[T_NodeDataStub @uV], maxToGenerate: Int, probabilityMultiplier: Int): Vector[Node[T_NodeDataStub @uV]] = {
      dataStubNodeGenerator.generateLinkedNodes(stubNode, maxToGenerate, parentStateTransitionPredicates[T_NodeDataStub @uV](stubNode, maxToGenerate, probabilityMultiplier),
        (nextEventType: T_ThisType) => stubNode.addParent(nextEventType.asStub)
      )
    }

  /**
   * Given a data node, creates new child data nodes based on the allowable child types and defined predicates specifying whether or not to add a child of a certain type
   * @param dataNode Node of a data stub
   * @param maxToGenerate Maximum number of nodes in graph
   * @param probabilityMultiplier Example usage: Predicate function might determine true/false based on a random function. Multiplier will make that function more probable to return true.
   * @return
   */
    def generateAndAddChildNodes(dataNode: Node[T_NodeData @uV], maxToGenerate: Int, probabilityMultiplier: Int): Vector[Node[T_NodeData @uV]] = {
      dataNodeGenerator.generateLinkedNodes(dataNode, maxToGenerate, childStateTransitionPredicates[T_NodeData @uV](dataNode, maxToGenerate, probabilityMultiplier),
        (nextEventType: T_ThisType) => dataTransitions.addRandomlyGeneratedChildData(dataNode, nextEventType))
    }

    /**
     * Given a data node, creates new parent data nodes based on the allowable child types and defined predicates specifying whether or not to add a parent of a certain type
     * @param dataNode Node of a data stub
     * @param maxToGenerate Maximum number of nodes in graph
     * @param probabilityMultiplier Example usage: Predicate function might determine true/false based on a random function. Multiplier will make that function more probable to return true.
     * @return
     */
    def generateAndAddParentNodes(dataNode: Node[T_NodeData @uV], maxToGenerate: Int, probabilityMultiplier: Int): Vector[Node[T_NodeData @uV]] = {
      dataNodeGenerator.generateLinkedNodes(dataNode, maxToGenerate, parentStateTransitionPredicates[T_NodeData @uV](dataNode, maxToGenerate, probabilityMultiplier),
        (nextEventType: T_ThisType) => dataTransitions.addRandomlyGeneratedParentData(dataNode, nextEventType))
    }
  }
}
