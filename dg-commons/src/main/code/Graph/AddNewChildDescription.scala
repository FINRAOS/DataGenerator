package Graph

import NodeData.DisplayableData

/**
 * Describes the creation of a new node, linked as a child of an existing node.
 * @param parentNodeIndex
 * @param dataToAdd
 * @tparam T
 */
class AddNewChildDescription[+T <: DisplayableData](val parentNodeIndex: Int, val dataToAdd: T) extends EdgeCreationDescription[T]