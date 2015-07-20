package SocialNetwork_Example.java;

import Graph.Node;
import GraphEngine.DataTransitions;
import scala.NotImplementedError;

/**
 * Created by Samer Adra on 7/14/2015.
 */
public class UserTransitions extends DataTransitions<User, UserTypeVal, UserStub, UserTypes> {
    private static UserTransitions ourInstance = new UserTransitions();

    public static UserTransitions getInstance() {
        return ourInstance;
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

        if (childType == UserType.Admin) {
            return parentType == UserType.Admin;
        } else if (childType == UserType.SocialNetworkEmployee) {
            return parentType != UserType.PublicUser;
        } else return true;
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
