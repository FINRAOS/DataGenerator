package Graph

import NodeData.DisplayableData

import scala.beans.BeanProperty

/**
 * Describes the creation of a new root node in the graph
 * @param dataToAdd
 * @tparam T
 */
class AddNewRootNodeDescription[+T <: DisplayableData](@BeanProperty val dataToAdd: T) extends EdgeCreationDescription[T]