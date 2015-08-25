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

package org.finra.datagenerator.common.SocialNetwork_Example.java;

import org.finra.datagenerator.common.NodeData.NodeDataStub;
import org.finra.datagenerator.common.SocialNetwork_Example.scala.SocialNetworkUtilities;
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
    final Option None = Option.apply(null);

    private UserTypeVal dataType = UserType.PublicUser;
    private Option<Tuple2<Double, Double>> geographicalLocation = None;
    private Option<Boolean> isSecret = None;

    protected UserStub() {
    }

    public UserStub(UserTypeVal userTypeVal) {
        this();
        dataType = userTypeVal;
    }

    public UserStub(UserTypeVal userType, Tuple2<Double, Double> geographicalLocation, Boolean isSecret) {
        this();
        dataType = userType;
        this.geographicalLocation = new Some<>(geographicalLocation);
        this.isSecret = new Some<>(isSecret);
    }

    public String defaultDisplayableDataId() {
        return dataType.name();
    }

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

    @Override
    public scala.collection.Iterable<String> simplifiedDisplayableElements() {
        LinkedList<String> elements = new LinkedList<>();
        elements.add(displayableDataId());

        return linkedListToScalaIterable(elements);
    }

    public static UserStub getStubWithRandomParams(UserTypeVal userType) {
        // Java coerces the return type as a Tuple2 of Objects -- but it's declared as a Tuple2 of Doubles!
        // Oh the joys of type erasure.
        Tuple2<Object, Object> tuple = SocialNetworkUtilities.getRandomGeographicalLocation();
        return new UserStub(userType, new Tuple2<>((Double)tuple._1(), (Double)tuple._2()),
                SocialNetworkUtilities.getRandomIsSecret());
    }

    // TODO: Investigate changing abstract scala vars to have BeanProperty annotation to make this Java code cleaner.
    public UserTypeVal getDataType() {
        return dataType();
    }
    @Override
    public UserTypeVal dataType() {
        return UserType.Admin;
    }
    public void setDataType(UserTypeVal userType) {
        dataType_$eq(userType);
    }
    @Override
    public void dataType_$eq(UserTypeVal userType) {
        dataType = userType;
    }

    // This stuff comes for free in Scala, but when extending a Scala case class in Java, we must define these.
    // http://dcsobral.blogspot.com/2009/06/case-classes-and-product.html

    public Object productElement(int n) {
        assert n >= 0 && n < 3;
        switch (n) {
            case 0: return dataType;
            case 1: return geographicalLocation;
            case 2: return isSecret;
            default: throw new IllegalArgumentException(n + " is not an allowed index into UserStub!");
        }
    }

    public int productArity() {
        return 3;
    }

    public boolean canEqual(Object that) {
        return (that != null) && (that.getClass() == getClass());
    }
}
