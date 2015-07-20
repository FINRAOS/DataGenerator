package Graph

import NodeData.DisplayableData

/**
 * Describes the creation of a new root node in the graph
 * @param dataToAdd
 * @tparam T
 */
class AddNewRootNodeDescription[+T <: DisplayableData](val dataToAdd: T) extends EdgeCreationDescription[T]