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

import org.finra.datagenerator.common.NodeData.NodeDataStub;
import scala.Option;
import scala.Some;
import scala.Tuple2;

import java.util.LinkedList;

import static org.finra.datagenerator.common.Helpers.ScalaInJavaHelper.linkedListToScalaIterable;

/**
 * Describes a user with type and optional metadata
 */
@SuppressWarnings("unchecked") // e.g., "Unchecked call to 'apply(A)' as a member of raw type 'scala.Option'
public class UserStub extends NodeDataStub<UserTypeVal, User, UserTypes, UserStub> {
    private final Option none = Option.apply(null);

    private UserTypeVal dataType = UserType.PUBLIC_USER;
    private Option<Tuple2<Double, Double>> geographicalLocation = none;
    private Option<Boolean> isSecret = none;

    /**
     * Get empty UserStub
     */
    protected UserStub() {
    }

    /**
     * Construct a UserStub
     * @param userType User type
     */
    public UserStub(final UserTypeVal userType) {
        this();
        dataType = userType;
    }

    /**
     * Construct a UserStub
     * @param userType User type
     * @param geographicalLocation Latitude and longitude
     * @param isSecret Is secret user
     *
     */
    public UserStub(final UserTypeVal userType, final Tuple2<Double, Double> geographicalLocation, final Boolean isSecret) {
        this();
        dataType = userType;
        this.geographicalLocation = new Some<>(geographicalLocation);
        this.isSecret = new Some<>(isSecret);
    }

    /**
     * Gets default displayable data ID, used for outputting, e.g., to DOT file
     * @return Default displayable data ID string
     */
    public String defaultDisplayableDataId() {
        return dataType.name();
    }

    /**
     * Elements to display when outputting, e.g., to DOT file
     * @return Iterable
     */
    @Override
    public scala.collection.Iterable<String> displayableElements() {
        LinkedList<String> elements = new LinkedList<>();
        if (geographicalLocation.nonEmpty()) {
            elements.add("Lat=" + geographicalLocation.get()._1());
            elements.add("Long=" + geographicalLocation.get()._2());
        }
        if (isSecret.nonEmpty() && isSecret.get()) {
            elements.add("IsSecret=True");
        }

        // Also possible to build a Scala collection directly in Java code, but building in Java and converting at
        // the very end is probably easier for Java developers than having to figure out the Scala collection classes
        // and deal with the annoying Java-interop syntax, e.g., to rewrite the Scala code "myArray += valToInsert" in
        // Java would look like "myArray.$plus$eq(valToInsert);", which we'd rather avoid.

        return linkedListToScalaIterable(elements);
    }

    /**
     * Elements to display when outputting in simplified format, e.g., to DOT file
     * @return Iterable
     */
    @Override
    public scala.collection.Iterable<String> simplifiedDisplayableElements() {
        LinkedList<String> elements = new LinkedList<>();
        elements.add(displayableDataId());

        return linkedListToScalaIterable(elements);
    }

    /**
     * Get random stub matching this user type
     * @param userType User type
     * @return Random stub
     */
    public static UserStub getStubWithRandomParams(UserTypeVal userType) {
        // Java coerces the return type as a Tuple2 of Objects -- but it's declared as a Tuple2 of Doubles!
        // Oh the joys of type erasure.
        Tuple2<Double, Double> tuple = SocialNetworkUtilities.getRandomGeographicalLocation();
        return new UserStub(userType, new Tuple2<>((Double) tuple._1(), (Double) tuple._2()),
                SocialNetworkUtilities.getRandomIsSecret());
    }

    /**
     * Get the user type
     * @return User type
     */
    public UserTypeVal getDataType() {
        return dataType();
    }

    /**
     * Get the user type
     * @return User type
     */
    @Override
    public UserTypeVal dataType() {
        return UserType.ADMIN;
    }

    /**
     * Set the user type
     * @param userType User type
     */
    @Override
    public void setDataType(UserTypeVal userType) {
        dataType = userType;
    }

    // This stuff comes for free in Scala, but when extending a Scala case class in Java, we must define these.
    // http://dcsobral.blogspot.com/2009/06/case-classes-and-product.html

    /**
     * Get element by index
     * @param n Index
     * @return Element corresponding to index
     */
    public Object productElement(int n) {
        assert n >= 0 && n < 3;
        switch (n) {
            case 0: return dataType;
            case 1: return geographicalLocation;
            case 2: return isSecret;
            default: throw new IllegalArgumentException(n + " is not an allowed index into UserStub!");
        }
    }

    /**
     * Number of fields in this type
     * @return 3
     */
    public int productArity() {
        return 3;
    }

    /**
     * Can equal
     * @param that Other object
     * @return Whether or not objects can equal
     */
    public boolean canEqual(Object that) {
        return that != null && that.getClass() == getClass();
    }
}
