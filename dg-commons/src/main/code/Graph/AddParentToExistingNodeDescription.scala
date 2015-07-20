package Graph

import NodeData.DisplayableData

/**
 * Describes the creation of a link between one node and another existing node as its parent
 * @param childNodeIndex
 * @param parentNodeIndex
 * @tparam T
 */
class AddParentToExistingNodeDescription[+T <: DisplayableData](val childNodeIndex: Int, val parentNodeIndex: Int) extends EdgeCreationDescription[T]