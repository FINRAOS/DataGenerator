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
import org.finra.datagenerator.common.Helpers.RandomHelper;
import org.finra.datagenerator.common.Helpers.ScalaInJavaHelper;
import org.finra.datagenerator.common.NodeData.DisplayableData;
import org.finra.datagenerator.common.NodeData.NodeDataType;
import scala.Function1;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.mutable.ListBuffer;
import scala.runtime.AbstractFunction1;

import java.util.LinkedList;

/**
 * User Type
 */
public final class UserType {
    private UserType() {
        // Not called -- utility class
    }

    /**
     * ADMIN
     */
    public static final UserTypeVal ADMIN = new UserTypeVal() {
        /**
         * Get data type
         * @return
         */
        public NodeDataType.NodeDataType<User, UserStub, UserTypes, UserTypeVal> getDataType() {
            return UserType.ADMIN;
        }

        /**
         * Get name of data type
         * @return
         */
        @Override
        public String name() {
            return "Admin";
        }

        /**
         * Get allowable child types
         * @param nodeOfThisType Node of this type
         * @return Sequence of allowable child types
         */
        @Override
        public Seq<UserTypeVal> getAllowableChildTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.ADMIN);
            list.add(UserType.SOCIAL_NETWORK_EMPLOYEE);
            list.add(UserType.PUBLIC_USER);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

        /**
         * Get allowable parent types
         * @param nodeOfThisType Node of this type
         * @return Sequence of allowable parent types
         */
        @Override
        public Seq<UserTypeVal> getAllowableParentTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.ADMIN);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

        /**
         * Whether or not to create children
         * @param node Node
         * @param maxToGenerate Max nodes to generate
         * @param probabilityMultiplier As this goes up from 1, we are more linearly more likely to produce children
         * @param <T_DisplayableData> Type param
         * @return Added children
         */
        @Override
        public <T_DisplayableData extends DisplayableData>
         ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>>
         childStateTransitionPredicates(Node<T_DisplayableData> node, int maxToGenerate,
                                        final int probabilityMultiplier) {
            ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>> list =
                    new ListBuffer<>();

            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.ADMIN,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.07);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.SOCIAL_NETWORK_EMPLOYEE,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.1);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.PUBLIC_USER,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.15);
                        }
                    }));
            return list;
        }

        /**
         * Whether or not to create parents
         * @param node Node
         * @param maxToGenerate Max nodes to generate
         * @param probabilityMultiplier As this goes up from 1, we are more linearly more likely to produce children
         * @param <T_DisplayableData> Type param
         * @return Added parents
         */
        @Override
        public <T_DisplayableData extends DisplayableData>
         ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>>
         parentStateTransitionPredicates(Node<T_DisplayableData> node, int maxToGenerate,
                                         final int probabilityMultiplier) {
            ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>> list =
                    new ListBuffer<>();

            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.ADMIN,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.07);
                        }
                    }));

            return list;
        }
    };

    /**
     * Social network employee
     */
    public static final UserTypeVal SOCIAL_NETWORK_EMPLOYEE = new UserTypeVal() {
        /**
         * Get data type
         * @return
         */
        public NodeDataType.NodeDataType<User, UserStub, UserTypes, UserTypeVal> getDataType() {
            return UserType.SOCIAL_NETWORK_EMPLOYEE;
        }

        /**
         * Get name of data type
         * @return
         */
        @Override
        public String name() {
            return "SocialNetworkEmployee";
        }

        /**
         * Get allowable child types
         * @param nodeOfThisType Node of this type
         * @return Sequence of allowable child types
         */
        @Override
        public Seq<UserTypeVal> getAllowableChildTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.SOCIAL_NETWORK_EMPLOYEE);
            list.add(UserType.PUBLIC_USER);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

        /**
         * Get allowable parent types
         * @param nodeOfThisType Node of this type
         * @return Sequence of allowable parent types
         */
        @Override
        public Seq<UserTypeVal> getAllowableParentTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.ADMIN);
            list.add(UserType.SOCIAL_NETWORK_EMPLOYEE);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

        /**
         * Whether or not to create children
         * @param node Node
         * @param maxToGenerate Max nodes to generate
         * @param probabilityMultiplier As this goes up from 1, we are more linearly more likely to produce children
         * @param <T_DisplayableData> Type param
         * @return Added children
         */
        @Override
        public <T_DisplayableData extends DisplayableData>
         ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>>
         childStateTransitionPredicates(Node<T_DisplayableData> node, int maxToGenerate,
                                       final int probabilityMultiplier) {
            ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>> list =
                    new ListBuffer<>();

            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.SOCIAL_NETWORK_EMPLOYEE,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.25);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.PUBLIC_USER,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.30);
                        }
                    }));
            return list;
        }

        /**
         * Whether or not to create parents
         * @param node Node
         * @param maxToGenerate Max nodes to generate
         * @param probabilityMultiplier As this goes up from 1, we are more linearly more likely to produce children
         * @param <T_DisplayableData> Type param
         * @return Added parents
         */
        @Override
        public <T_DisplayableData extends DisplayableData>
         ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>>
         parentStateTransitionPredicates(Node<T_DisplayableData> node, int maxToGenerate,
                                        final int probabilityMultiplier) {
            ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>> list =
                    new ListBuffer<>();

            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.ADMIN,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.03);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.SOCIAL_NETWORK_EMPLOYEE,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.25);
                        }
                    }));

            return list;
        }
    };

    /**
     * Public User
     */
    public static final UserTypeVal PUBLIC_USER = new UserTypeVal() {
        /**
         * Get data type
         * @return
         */
        public NodeDataType.NodeDataType<User, UserStub, UserTypes, UserTypeVal> getDataType() {
            return UserType.PUBLIC_USER;
        }

        /**
         * Get name of data type
         * @return
         */
        @Override
        public String name() {
            return "PublicUser";
        }

        /**
         * Get allowable child types
         * @param nodeOfThisType Node of this type
         * @return Sequence of allowable child types
         */
        @Override
        public Seq<UserTypeVal> getAllowableChildTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.PUBLIC_USER);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

        /**
         * Get allowable parent types
         * @param nodeOfThisType Node of this type
         * @return Sequence of allowable parent types
         */
        @Override
        public Seq<UserTypeVal> getAllowableParentTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.ADMIN);
            list.add(UserType.SOCIAL_NETWORK_EMPLOYEE);
            list.add(UserType.PUBLIC_USER);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

        /**
         * Whether or not to create children
         * @param node Node
         * @param maxToGenerate Max nodes to generate
         * @param probabilityMultiplier As this goes up from 1, we are more linearly more likely to produce children
         * @param <T_DisplayableData> Type param
         * @return Added children
         */
        @Override
        public <T_DisplayableData extends DisplayableData>
         ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>>
         childStateTransitionPredicates(Node<T_DisplayableData> node, int maxToGenerate,
                                       final int probabilityMultiplier) {
            ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>> list =
                    new ListBuffer<>();

            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.PUBLIC_USER,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.35);
                        }
                    }));
            return list;
        }

        /**
         * Whether or not to create parents
         * @param node Node
         * @param maxToGenerate Max nodes to generate
         * @param probabilityMultiplier As this goes up from 1, we are more linearly more likely to produce children
         * @param <T_DisplayableData> Type param
         * @return Added parents
         */
        @Override
        public <T_DisplayableData extends DisplayableData>
        ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>>
        parentStateTransitionPredicates(Node<T_DisplayableData> node, int maxToGenerate,
                                        final int probabilityMultiplier) {
            ListBuffer<Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>> list =
                    new ListBuffer<>();

            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.ADMIN,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.01);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.SOCIAL_NETWORK_EMPLOYEE,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.02);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.PUBLIC_USER,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.35);
                        }
                    }));

            return list;
        }
    };
}
