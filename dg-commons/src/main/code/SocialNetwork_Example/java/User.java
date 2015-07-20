package SocialNetwork_Example.java;

import NodeData.NodeData;
import scala.Tuple2;

/**
 * Social network user
 */
public class User extends NodeData {
    UserTypeVal dataType;
    String firstName;
    String lastName;
    java.sql.Date dateOfBirth;
    Tuple2<Double, Double> geographicalLocation;
    Boolean isSecret;
    Long socialNetworkId;

    public User(
            UserTypeVal dataType,
            String firstName,
            String lastName,
            java.sql.Date dateOfBirth, // Assert > 13 years old when creating
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