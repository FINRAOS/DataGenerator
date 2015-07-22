package SocialNetwork_Example.java;

import Graph.Node;
import NodeData.NodeDataType;

/**
 * Abstract type for UserType enumerated values.
 */
public abstract class UserTypeVal extends NodeDataType.NodeDataType<User, UserStub, UserTypes, UserTypeVal> {
    @Override
    public UserTypes nodeDataTypes() {
        return UserTypes.getInstance();
    }

    @Override
    public UserStub asStub() {
        return new UserStub(this);
    }

    // We don't have any engines that use these two methods yet, but it might be useful at some point.
    public void probabilisticallyLinkToExistingParentDataNode(Node<User> dataNode) {
    }
    public void probabilisticallyLinkToExistingParentStubNode(Node<UserStub> dataNode) {
    }
}