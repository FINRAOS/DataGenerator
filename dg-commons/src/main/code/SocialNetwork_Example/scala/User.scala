package SocialNetwork_Example.scala

import NodeData.NodeData

/*
Social network user
 */
class User( var dataType: UserType.UserType,
            var firstName: String,
            var lastName: String,
            val dateOfBirth: java.sql.Date, // Assert > 13 years old when creating
            var geographicalLocation: (Double, Double),
            var isSecret: Boolean,
            val socialNetworkId: Long) extends NodeData(None) {
  override def defaultDisplayableDataId: String = s"$socialNetworkId (${dataType.name}): $lastName, $firstName"
}