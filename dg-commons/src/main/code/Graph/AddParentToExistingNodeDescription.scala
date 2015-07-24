package Graph

import NodeData.DisplayableData

import scala.beans.BeanProperty

/**
 * Describes the creation of a link between one node and another existing node as its parent
 * @param childNodeIndex
 * @param parentNodeIndex
 * @tparam T
 */
class AddParentToExistingNodeDescription[+T <: DisplayableData](@BeanProperty val childNodeIndex: Int, @BeanProperty val parentNodeIndex: Int) extends EdgeCreationDescription[T]