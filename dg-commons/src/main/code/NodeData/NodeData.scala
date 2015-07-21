package NodeData

/**
 * Data that can be stored in a node of a graph
 */
abstract class NodeData(_stubCreatedFromMaybe: Option[_ <: NodeDataStub[_, _, _, _]] = None) extends DisplayableData {
  def this() = {
    this(None)
  }

  var customPropertyMap = collection.mutable.HashMap[String, String]()

  // TODO: Might need to make NodeData generic around NodeDataStub so can infer the type of stubCreatedFromMaybe without casting.
  def stubCreatedFromMaybe = _stubCreatedFromMaybe
}
