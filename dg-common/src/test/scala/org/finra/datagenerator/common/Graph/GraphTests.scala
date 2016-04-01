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

package org.finra.datagenerator.common.Graph

import java.io.ByteArrayOutputStream

import org.finra.datagenerator.common.SocialNetwork_Example._
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner

/**
* org.finra.datagenerator.common.Graph unit tests
*/
@RunWith(classOf[JUnitRunner])
class GraphTests extends WordSpec {
  "A new graph" when {
    "Adding some nodes" should {
      "Contain all nodes and edges in the DOT output of the copied graph" in {
        val initialNodeValue = UserType.ADMIN.asStub
        val graph = new Graph[UserStub](
          Option(initialNodeValue), isEdgeLinkTrackingOn = true, _graphId = "TestGraph1", appendSharedDisplayIdsWithNumericalSuffix = true)
        assert(graph.rootNodes.size == 1 && graph.allNodes.size == 1 && graph.rootNodes.head == graph.allNodes.head
          && graph.rootNodes.head.data == initialNodeValue)
        val child1 = graph.allNodes.head.addChild(UserType.ADMIN.asStub)
        val child2 = graph.allNodes.head.addChild(UserType.SOCIAL_NETWORK_EMPLOYEE.asStub)
        val child3 = graph.allNodes.head.addChild(UserType.PUBLIC_USER.asStub)
        val grandchild1 = child1.addChild(UserType.PUBLIC_USER.asStub)
        child2.addLinkToExistingChild(grandchild1)
        grandchild1.addLinkToExistingParent(child3)
        child3.addParent(UserType.ADMIN.asStub)
        assert(graph.rootNodes.size == 2 && graph.allNodes.size == 6)
        val outputStream = new ByteArrayOutputStream()
        val copiedGraph = graph.deepCopy
        copiedGraph.writeDotFileToOpenStream(outputStream, isSimplified = true)
        val dotOutput = outputStream.toString
        val expectedDotOutput =
           """|digraph "Graph_TestGraph1" {
              |"Admin_1" [label="Admin_1" shape="record"];
              |"Admin_1"->"Admin_2"
              |"Admin_1"->"SocialNetworkEmployee_1"
              |"Admin_1"->"PublicUser_1"
              |"Admin_2" [label="Admin_2" shape="record"];
              |"Admin_2"->"PublicUser_2"
              |"SocialNetworkEmployee_1" [label="SocialNetworkEmployee_1" shape="record"];
              |"SocialNetworkEmployee_1"->"PublicUser_2"
              |"PublicUser_1" [label="PublicUser_1" shape="record"];
              |"PublicUser_1"->"PublicUser_2"
              |"PublicUser_2" [label="PublicUser_2" shape="record"];
              |"Admin_3" [label="Admin_3" shape="record"];
              |"Admin_3"->"PublicUser_1"
              |}""".stripMargin

        val actualLines = dotOutput.lines.toList
        val expectedLines = expectedDotOutput.lines.toList

        actualLines.foreach(line => {
          assert(expectedLines.contains(line))
        })
        expectedLines.foreach(line => {
          assert(actualLines.contains(line))
        })
      }
    }
  }
}