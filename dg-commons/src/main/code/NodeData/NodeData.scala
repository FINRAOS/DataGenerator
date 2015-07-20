package NodeData

/**
 * Data that can be stored in a node of a graph
 */
abstract class NodeData(_stubCreatedFromMaybe: Option[NodeDataStub[_, _, _, _]] = None) extends DisplayableData {
  def this() = {
    this(None)
  }

  var customPropertyMap = collection.mutable.HashMap[String, String]()
  def stubCreatedFromMaybe = _stubCreatedFromMaybe
}
