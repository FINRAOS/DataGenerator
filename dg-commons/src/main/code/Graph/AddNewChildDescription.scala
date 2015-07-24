package Graph

import NodeData.DisplayableData

import scala.beans.BeanProperty

/**
 * Describes the creation of a new node, linked as a child of an existing node.
 * @param parentNodeIndex
 * @param dataToAdd
 * @tparam T
 */
class AddNewChildDescription[+T <: DisplayableData](@BeanProperty val parentNodeIndex: Int, @BeanProperty val dataToAdd: T) extends EdgeCreationDescription[T]