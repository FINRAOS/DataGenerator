/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finra.datagenerator.common.SocialNetwork_Example

import org.finra.datagenerator.common.Graph.Node
import org.finra.datagenerator.common.GraphEngine.DataTransitions

class UserTransitions

object UserTransitions extends DataTransitions[User, UserType.UserType, UserStub, UserTypes] {
  override def nodeDataTypes: UserTypes = new UserTypes()

  override def isStubParentTypeTransitionAllowed(childNode: Node[UserStub], candidateParentNode: Node[UserStub]): Boolean = {
    childNode.data.dataType match {
      case UserType.ADMIN =>
        candidateParentNode.data.dataType match {
          case UserType.ADMIN => true
          case _ => false
        }
      case UserType.SOCIAL_NETWORK_EMPLOYEE =>
        candidateParentNode.data.dataType match {
          case UserType.PUBLIC_USER => false
          case _ => true
        }
      case UserType.PUBLIC_USER => true
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
