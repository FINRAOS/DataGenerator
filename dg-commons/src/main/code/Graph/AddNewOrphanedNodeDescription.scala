package Graph

import NodeData.DisplayableData

/**
 * Describes the creation of an orphaned node, not linked to any other nodes in the graph
 * @param dataToAdd
 * @tparam T
 */
class AddNewOrphanedNodeDescription[+T <: DisplayableData](val dataToAdd: T) extends EdgeCreationDescription[T]