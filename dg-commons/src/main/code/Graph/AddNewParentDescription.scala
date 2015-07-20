package Graph

import NodeData.DisplayableData

/**
 * Describes the creation of a new node, linked as a parent of an existing node.
 * @param childNodeIndex
 * @param dataToAdd
 * @tparam T
 */
class AddNewParentDescription[+T <: DisplayableData](val childNodeIndex: Int, val dataToAdd: T) extends EdgeCreationDescription[T]