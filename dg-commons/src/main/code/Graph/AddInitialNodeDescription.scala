package Graph

import NodeData.DisplayableData

/**
 * Describes the creation of first-added node in a graph
 * @param dataToAdd
 * @tparam T
 */
class AddInitialNodeDescription[+T <: DisplayableData](val dataToAdd: T) extends EdgeCreationDescription[T]

