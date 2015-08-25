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

public class UserType {
    public static final UserTypeVal Admin = new UserTypeVal() {
        public NodeDataType.NodeDataType<User, UserStub, UserTypes, UserTypeVal> dataType() {
            return UserType.Admin;
        }

        @Override
        public String name() {
            return "Admin";
        }

        @Override
        public Seq<UserTypeVal> getAllowableChildTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.Admin);
            list.add(UserType.SocialNetworkEmployee);
            list.add(UserType.PublicUser);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

        @Override
        public Seq<UserTypeVal> getAllowableParentTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.Admin);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

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
        public NodeDataType.NodeDataType<User, UserStub, UserTypes, UserTypeVal> dataType() {
            return UserType.SocialNetworkEmployee;
        }

        @Override
        public String name() {
            return "SocialNetworkEmployee";
        }

        @Override
        public Seq<UserTypeVal> getAllowableChildTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.SocialNetworkEmployee);
            list.add(UserType.PublicUser);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

        @Override
        public Seq<UserTypeVal> getAllowableParentTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.Admin);
            list.add(UserType.SocialNetworkEmployee);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

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
        public NodeDataType.NodeDataType<User, UserStub, UserTypes, UserTypeVal> dataType() {
            return UserType.PublicUser;
        }

        @Override
        public String name() {
            return "PublicUser";
        }

        @Override
        public Seq<UserTypeVal> getAllowableChildTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.PublicUser);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

        @Override
        public Seq<UserTypeVal> getAllowableParentTypes(Node<UserStub> nodeOfThisType) {
            LinkedList<UserTypeVal> list = new LinkedList<>();
            list.add(UserType.Admin);
            list.add(UserType.SocialNetworkEmployee);
            list.add(UserType.PublicUser);
            return ScalaInJavaHelper.linkedListToScalaIterable(list).toSeq();
        }

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
