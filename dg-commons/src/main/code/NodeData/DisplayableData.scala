package NodeData

import Helpers.StringHelper._
import NodeDataType.NodeDataType

/**
 * Trait used for any data with a displayable ID and optional fields, and with around event type.
 * For example, this could be used for an object that can be viewed as an element in a DOT file, but it's not specific to that.
 */
trait DisplayableData {
  private var _overrideDisplayableDataId = ""
  def overrideDisplayableDataId: String = _overrideDisplayableDataId
  def overrideDisplayableDataId_=(value: String): Unit = {
    _overrideDisplayableDataId = value
  }
  def defaultDisplayableDataId: String
  def displayableDataId = { // Anything that uniquely identifies a childNode; e.g., 1, 2, 3, or NW_1, NW_2, RT_1
    if (overrideDisplayableDataId.nonEmpty) overrideDisplayableDataId
    else defaultDisplayableDataId
  }
  def displayableElements: Iterable[String] = Iterable[String](displayableDataId)
  def simplifiedDisplayableElements: Iterable[String] = Iterable[String](displayableDataId)
  def dataType: NodeDataType[_, _, _, _]

  /**
   * Used for testing graph isomorphism.
   * @return
   */
  def getStructuralMD5: String = {
    new String(s"${dataType.name},${displayableElements.mkString(",")}".md5)
  }
}
