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

import org.finra.datagenerator.common.GraphEngine.DataTransitions;
import org.finra.datagenerator.common.NodeData.NodeDataTypes;
import scala.collection.immutable.HashSet;

/**
 * User type classifications
 */
public final class UserTypes implements NodeDataTypes<User, UserStub, UserTypeVal, UserTypes> {
    private static final UserTypes INSTANCE = new UserTypes();

    /**
     * Get singleton instance
     * @return UserTypes
     */
    public static UserTypes getInstance() {
        return INSTANCE;
    }

    private UserTypes() {
    }

    private HashSet<UserTypeVal> allDataTypes;

    /**
     * Get all data types
     * @return All data types in a HashSet
     */
    public HashSet<UserTypeVal> allDataTypes() {
        if (allDataTypes == null) {
            // Would be cleaner if we could call scala's apply method here, but it isn't part of a HashSet companion
            // object -- instead it lives in GenericCompanion, and it's unclear now to get to it from Java.
            allDataTypes = new HashSet<>();
            allDataTypes = allDataTypes.$plus(UserType.ADMIN);
            allDataTypes = allDataTypes.$plus(UserType.PUBLIC_USER);
            allDataTypes = allDataTypes.$plus(UserType.SOCIAL_NETWORK_EMPLOYEE);
        }
        return allDataTypes;
    }

    private HashSet<UserTypeVal> allInitialDataTypes;

    /**
     * All data types that can be the first node added to a graph.
     * @return Set of types
     */
    public HashSet<UserTypeVal> allInitialDataTypes() {
        if (allInitialDataTypes == null) {
            // Would be cleaner if we could call scala's apply method here, but it isn't part of a HashSet companion
            // object -- instead it lives in GenericCompanion, and it's unclear now to get to it from Java.
            allInitialDataTypes = new HashSet<>();
            allInitialDataTypes = allInitialDataTypes.$plus(UserType.ADMIN);
        }
        return allInitialDataTypes;
    }

    /**
     * Class used for data transition methods
     * @return DataTransitions instance
     */
    public DataTransitions<User, UserTypeVal, UserStub, UserTypes> dataTransitions() {
        return UserTransitions.getInstance();
    }
}
