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

import org.finra.datagenerator.common.NodeData.NodeData;
import scala.Tuple2;

import java.sql.Date;

/**
 * Social network user
 */
public class User extends NodeData {
    final UserTypeVal dataType;
    final String firstName;
    final String lastName;
    final Date dateOfBirth;
    final Tuple2<Double, Double> geographicalLocation;
    final Boolean isSecret;
    final Long socialNetworkId;

    /**
     * Construct a User
     * @param dataType User type
     * @param firstName First name
     * @param lastName Last name
     * @param dateOfBirth Date of birth
     * @param geographicalLocation Latitude and longitude
     * @param isSecret Is secret user
     * @param socialNetworkId Social network ID of user
     */
    public User(
            final UserTypeVal dataType,
            final String firstName,
            final String lastName,
            final Date dateOfBirth, // Assert > 13 years old when creating
            final Tuple2<Double, Double> geographicalLocation,
            final Boolean isSecret,
            final Long socialNetworkId) {
        super();
        this.dataType = dataType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.geographicalLocation = geographicalLocation;
        this.isSecret = isSecret;
        this.socialNetworkId = socialNetworkId;
    }

    /**
     * Get default ID to display when outputting user, e.g., as part of a node in of DOT file
     * @return ID String
     */
    public String defaultDisplayableDataId() {
        return socialNetworkId + " (" + dataType.name() + "): " + lastName + ", " + firstName;
    }

    /**
     * Get user type
     * @return User type
     */
    public UserTypeVal getDataType() {
        return dataType;
    }
}
