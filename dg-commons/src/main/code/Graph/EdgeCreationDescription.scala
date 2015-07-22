package Graph

import NodeData.DisplayableData

/**
 * Description of an edge to add to the graph. Can be used to rebuild a graph
 * (for example, perhaps an analogous graph of a different type) in the same order as originally created.
 */
abstract class EdgeCreationDescription[+T <: DisplayableData]