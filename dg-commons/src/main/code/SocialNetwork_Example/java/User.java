package SocialNetwork_Example.java;

import NodeData.NodeData;
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

    public User(
            UserTypeVal dataType,
            String firstName,
            String lastName,
            Date dateOfBirth, // Assert > 13 years old when creating
            Tuple2<Double, Double> geographicalLocation,
            Boolean isSecret,
            Long socialNetworkId) {
        super();
        this.dataType = dataType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.geographicalLocation = geographicalLocation;
        this.isSecret = isSecret;
        this.socialNetworkId = socialNetworkId;
    }

    public String defaultDisplayableDataId() {
        return socialNetworkId + " (" + dataType.name() + "): " +lastName + ", " + firstName;
    }

    public UserTypeVal dataType() {
        return dataType;
    }
}