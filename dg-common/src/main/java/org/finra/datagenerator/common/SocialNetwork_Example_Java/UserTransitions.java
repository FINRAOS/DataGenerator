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

package org.finra.datagenerator.common.SocialNetwork_Example_Java;

import org.finra.datagenerator.common.Graph.Node;
import org.finra.datagenerator.common.GraphEngine.DataTransitions;
import scala.NotImplementedError;

/**
 * Methods to link users together
 */
public final class UserTransitions extends DataTransitions<User, UserTypeVal, UserStub, UserTypes> {
    private static final UserTransitions INSTANCE = new UserTransitions();

    public static UserTransitions getInstance() {
        return INSTANCE;
    }

    private UserTransitions() {
    }

    @Override
    public UserTypes nodeDataTypes() {
        return UserTypes.getInstance();
    }

    @Override
    public void linkExistingNodes(Node<User> parent, Node<User> child) {
        parent.addLinkToExistingChild(child);
    }

    @Override
    public boolean isStubParentTypeTransitionAllowed(Node<UserStub> childNode, Node<UserStub> candidateParentNode) {
        UserTypeVal childType = childNode.data().dataType();
        UserTypeVal parentType = candidateParentNode.data().dataType();

        if (childType == UserType.ADMIN) {
            return parentType == UserType.ADMIN;
        } else if (childType == UserType.SOCIAL_NETWORK_EMPLOYEE) {
            return parentType != UserType.PUBLIC_USER;
        } else {
            return true;
        }
    }

    @Override
    public boolean isParentTypeTransitionAllowed(Node<User> childDataNode, Node<User> candidateParentDataNode) {
        throw new NotImplementedError();
    }

    @Override
    public Node<User> addRandomlyGeneratedParentData(Node<User> childDataNode, UserTypeVal parentNodeDataType) {
        throw new NotImplementedError();
    }

    @Override
    public Node<User> addRandomlyGeneratedParentData(Node<User> childDataNode, UserStub parentNodeDataStub) {
        throw new NotImplementedError();
    }

    @Override
    public Node<User> addRandomlyGeneratedChildData(Node<User> parentDataNode, UserTypeVal childNodeDataType) {
        throw new NotImplementedError();
    }

    @Override
    public Node<User> addRandomlyGeneratedChildData(Node<User> parentDataNode, UserStub childNodeDataStub) {
        throw new NotImplementedError();
    }
}
