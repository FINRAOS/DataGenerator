package SocialNetwork_Example.java;

import GraphEngine.DataTransitions;
import NodeData.NodeDataTypes;
import scala.collection.immutable.HashSet;

/**
 * User type classifications
 */
public class UserTypes implements NodeDataTypes<User, UserStub, UserTypeVal, UserTypes> {
    private static final UserTypes ourInstance = new UserTypes();

    public static UserTypes getInstance() {
        return ourInstance;
    }

    private UserTypes() {
    }

    private HashSet<UserTypeVal> allDataTypes;
    public HashSet<UserTypeVal> allDataTypes() {
        if (allDataTypes == null) {
            // Would be cleaner if we could call scala's apply method here, but it isn't part of a HashSet companion
            // object -- instead it lives in GenericCompanion, and it's unclear now to get to it from Java.
            allDataTypes = new HashSet<>();
            allDataTypes = allDataTypes.$plus(UserType.Admin);
            allDataTypes = allDataTypes.$plus(UserType.PublicUser);
            allDataTypes = allDataTypes.$plus(UserType.SocialNetworkEmployee);
        }
        return allDataTypes;
    }

    private HashSet<UserTypeVal> allInitialDataTypes;
    public HashSet<UserTypeVal> allInitialDataTypes() {
        if (allInitialDataTypes == null) {
            // Would be cleaner if we could call scala's apply method here, but it isn't part of a HashSet companion
            // object -- instead it lives in GenericCompanion, and it's unclear now to get to it from Java.
            allInitialDataTypes = new HashSet<>();
            allInitialDataTypes = allInitialDataTypes.$plus(UserType.Admin);
        }
        return allInitialDataTypes;
    }

    public DataTransitions<User, UserTypeVal, UserStub, UserTypes> dataTransitions() {
        return UserTransitions.getInstance();
    }
}
