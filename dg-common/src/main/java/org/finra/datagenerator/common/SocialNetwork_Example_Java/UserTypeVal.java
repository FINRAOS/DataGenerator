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
import org.finra.datagenerator.common.NodeData.NodeDataType;

/**
 * Abstract type for UserType enumerated values.
 */
public abstract class UserTypeVal extends NodeDataType.NodeDataType<User, UserStub, UserTypes, UserTypeVal> {
    /**
     * Instance of UserTypes
     * @return UserTypes
     */
    @Override
    public UserTypes nodeDataTypes() {
        return UserTypes.getInstance();
    }

    /**
     * A stub wrapped around this type
     * @return Stub
     */
    @Override
    public UserStub asStub() {
        return new UserStub(this);
    }

    /**
     * Method to link existing nodes
     * @param dataNode Node to link to parent
     */
    // We don't have any engines that use these two methods yet, but it might be useful at some point.
    public void probabilisticallyLinkToExistingParentDataNode(Node<User> dataNode) {
    }
    /**
     * Method to link existing nodes
     * @param dataNode Node to link to child
     */
    public void probabilisticallyLinkToExistingParentStubNode(Node<UserStub> dataNode) {
    }
}
