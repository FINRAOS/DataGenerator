//CHECKSTYLE:OFF
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
public class UserType {
    /**
     * Admin
     */
    public static final UserTypeVal Admin = new UserTypeVal() {
        /**
         * Get data type
         * @return
         */
        public NodeDataType.NodeDataType<User, UserStub, UserTypes, UserTypeVal> dataType() {
            return UserType.Admin;
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
            list.add(UserType.Admin);
            list.add(UserType.SocialNetworkEmployee);
            list.add(UserType.PublicUser);
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
            list.add(UserType.Admin);
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
                    UserType.Admin,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.07);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.SocialNetworkEmployee,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.1);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.PublicUser,
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
                    UserType.Admin,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.07);
                        }
                    }));

            return list;
        }
    };

    public static final UserTypeVal SocialNetworkEmployee = new UserTypeVal() {
        /**
         * Get data type
         * @return
         */
        public NodeDataType.NodeDataType<User, UserStub, UserTypes, UserTypeVal> dataType() {
            return UserType.SocialNetworkEmployee;
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
            list.add(UserType.SocialNetworkEmployee);
            list.add(UserType.PublicUser);
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
            list.add(UserType.Admin);
            list.add(UserType.SocialNetworkEmployee);
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
                    UserType.SocialNetworkEmployee,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.25);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.PublicUser,
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
                    UserType.Admin,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.03);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.SocialNetworkEmployee,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.25);
                        }
                    }));

            return list;
        }
    };

    public static final UserTypeVal PublicUser = new UserTypeVal() {
        /**
         * Get data type
         * @return
         */
        public NodeDataType.NodeDataType<User, UserStub, UserTypes, UserTypeVal> dataType() {
            return UserType.PublicUser;
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
            list.add(UserType.PublicUser);
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
            list.add(UserType.Admin);
            list.add(UserType.SocialNetworkEmployee);
            list.add(UserType.PublicUser);
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
                    UserType.PublicUser,
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
                    UserType.Admin,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.01);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.SocialNetworkEmployee,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.02);
                        }
                    }));
            list.$plus$eq(new Tuple2<UserTypeVal, Function1<Node<T_DisplayableData>, Object>>(
                    UserType.PublicUser,
                    new AbstractFunction1<Node<T_DisplayableData>, Object>() {
                        public Object apply(Node<T_DisplayableData> sourceEventNode) {
                            return RandomHelper.evaluateProbability(probabilityMultiplier * 0.35);
                        }
                    }));

            return list;
        }
    };
}
