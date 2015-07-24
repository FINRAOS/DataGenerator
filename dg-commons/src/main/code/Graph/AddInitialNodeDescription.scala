package Graph

import NodeData.DisplayableData

import scala.beans.BeanProperty

/**
 * Describes the creation of first-added node in a graph
 * @param dataToAdd
 * @tparam T
 */
class AddInitialNodeDescription[+T <: DisplayableData](@BeanProperty val dataToAdd: T) extends EdgeCreationDescription[T]

