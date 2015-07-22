package SocialNetwork_Example.scala

import Graph.Node
import GraphEngine.DataTransitions

class UserTransitions

object UserTransitions extends DataTransitions[User, UserType.UserType, UserStub, UserTypes] {
  override def nodeDataTypes = new UserTypes()

  override def isStubParentTypeTransitionAllowed(childNode: Node[UserStub], candidateParentNode: Node[UserStub]): Boolean = {
    childNode.data.dataType match {
      case UserType.Admin =>
        candidateParentNode.data.dataType match {
          case UserType.Admin => true
          case _ => false
        }
      case UserType.SocialNetworkEmployee =>
        candidateParentNode.data.dataType match {
          case UserType.PublicUser => false
          case _ => true
        }
      case UserType.PublicUser => true
    }
  }

  // The following methods all deal with the User object. As we do not yet have a generator that deals with User objects,
  // we leave this to be implemented later. (Currently we have the SocialNetworkStructureBuilder, which builds graphs of
  // UserStubs. So for now we only need the transition methods that take in stubs.)
  override def isParentTypeTransitionAllowed(childDataNode: Node[User], candidateParentDataNode: Node[User]): Boolean = ???
  override def addRandomlyGeneratedParentData(childDataNode: Node[User], parentNodeDataType: UserType.UserType): Node[User] = ???
  override def addRandomlyGeneratedParentData(childDataNode: Node[User], parentNodeDataStub: UserStub): Node[User] = ???
  override def addRandomlyGeneratedChildData(parentDataNode: Node[User], childNodeDataType: UserType.UserType): Node[User] = ???
  override def addRandomlyGeneratedChildData(parentDataNode: Node[User], childNodeDataStub: UserStub): Node[User] = ???
  override def linkExistingNodes(parent: Node[User], child: Node[User]): Unit = {
    parent.addLinkToExistingChild(child)
  }
}
