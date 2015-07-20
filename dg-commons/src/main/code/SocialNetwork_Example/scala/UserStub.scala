package SocialNetwork_Example.scala

import Helpers.StringHelper._
import NodeData.NodeDataStub
import SocialNetwork_Example.scala.UserType.UserType

import scala.collection.mutable

/**
 * A user stub is input used to create a user, and comprises of user type plus optional metadata
 * representing parameters of that user that we want to override with specified values.
 * Any non-specified parameters are expected to be populated with default or random values.
 */
class UserStub protected() extends NodeDataStub[UserType, User, UserTypes, UserStub]() {
  var dataType: UserType = UserType.PublicUser
  var geographicalLocation: Option[(Double, Double)] = None
  var isSecret: Option[Boolean] = None

  def this(userType: UserType.UserType, geographicalLocation: (Double, Double), isSecret: Boolean) = {
    this()
    this.dataType = userType
    this.geographicalLocation = Some(geographicalLocation)
    this.isSecret = Some(isSecret)
  }

  def this(userType: UserType.UserType) = {
    this()
    this.dataType = userType
  }

  override def displayableElements = {
    val elements = mutable.ArrayBuffer[String](displayableDataId)
    if (geographicalLocation.nonEmpty) {
      elements += s"Lat=${geographicalLocation.get._1}"
      elements += s"Long=${geographicalLocation.get._2}"
    }
    if (isSecret.nonEmpty && isSecret.get) elements += s"IsSecret=True"
    elements
  }

  override def simplifiedDisplayableElements = {
    Iterable[String](displayableDataId)
  }

  /**
   * Used for testing graph isomorphism.
   * @return
   */
  override def getStructuralMD5: String = {
    new String(s"${dataType.name},${displayableElements.mkString(",")}".md5)
  }

  override def defaultDisplayableDataId: String = dataType.name
}

object UserStub {
  def getStubWithRandomParams(userType: UserType.UserType): UserStub = {
    new UserStub(userType, SocialNetworkUtilities.getRandomGeographicalLocation, SocialNetworkUtilities.getRandomIsSecret)
  }
}