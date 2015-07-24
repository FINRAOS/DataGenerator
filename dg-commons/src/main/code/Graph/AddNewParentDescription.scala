package Graph

import NodeData.DisplayableData

import scala.beans.BeanProperty

/**
 * Describes the creation of a new node, linked as a parent of an existing node.
 * @param childNodeIndex
 * @param dataToAdd
 * @tparam T
 */
class AddNewParentDescription[+T <: DisplayableData](@BeanProperty val childNodeIndex: Int, @BeanProperty val dataToAdd: T) extends EdgeCreationDescription[T]