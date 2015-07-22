package Graph

import NodeData.DisplayableData

/**
 * Describes the creation of a link between one node and another existing node as its child
 * @param parentNodeIndex
 * @param childNodeIndex
 * @tparam T
 */
class AddChildToExistingNodeDescription[+T <: DisplayableData](val parentNodeIndex: Int, val childNodeIndex: Int) extends EdgeCreationDescription[T]