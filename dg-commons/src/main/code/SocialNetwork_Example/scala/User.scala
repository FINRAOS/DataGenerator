package SocialNetwork_Example.scala

import NodeData.NodeData

import scala.beans.BeanProperty

/*
Social network user
 */
class User( var dataType: UserType.UserType,
            @BeanProperty var firstName: String,
            @BeanProperty var lastName: String,
            @BeanProperty val dateOfBirth: java.sql.Date, // Assert > 13 years old when creating
            @BeanProperty var geographicalLocation: (Double, Double),
            @BeanProperty var isSecret: Boolean,
            @BeanProperty val socialNetworkId: Long) extends NodeData(None) {
  override def defaultDisplayableDataId: String = s"$socialNetworkId (${dataType.name}): $lastName, $firstName"
}