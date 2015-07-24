package Graph

import NodeData.DisplayableData

import scala.beans.BeanProperty

/**
 * Describes the creation of a link between one node and another existing node as its child
 * @param parentNodeIndex
 * @param childNodeIndex
 * @tparam T
 */
class AddChildToExistingNodeDescription[+T <: DisplayableData](@BeanProperty val parentNodeIndex: Int, @BeanProperty val childNodeIndex: Int) extends EdgeCreationDescription[T]