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

package org.finra.datagenerator.common.SocialNetwork_Example

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

import org.finra.datagenerator.common.Graph.Graph
import org.finra.datagenerator.common.GraphEngine.StructureBuilder

import scala.collection.immutable

/**
 * Builds all combinations of graph structures for friendships with a maximum graph size
 */
object SocialNetworkStructureBuilder extends StructureBuilder[User, UserType.UserType, UserStub, UserTypes] {
  def nodeDataTypes: UserTypes = new UserTypes()

  val systemTempDir = System.getProperty("java.io.tmpdir").replaceAllLiterally("\\", "/")
  val outDir = s"$systemTempDir${if (systemTempDir.endsWith("/")) "" else "/"}SocialNetworkGraphs/"
  new File(outDir).mkdirs()

  val WRITE_STRUCTURES_IN_PARALLEL = false // Having same structure be same ID helps debugging...
  val ALSO_WRITE_AS_PNG = true

  /**
   * Build all combinations of graph structures for generic event stubs of a maximum length
   * @param length Maximum number of nodes in each to generate
   * @return All graph combinations of specified length or less
   */
  override def generateAllNodeDataTypeGraphCombinationsOfMaxLength(length: Int): immutable.Vector[Graph[UserStub]] = {
    val graphs = super.generateAllNodeDataTypeGraphCombinationsOfMaxLength(length)

    if (WRITE_STRUCTURES_IN_PARALLEL) {
      val i = new AtomicInteger(0)
      graphs.par.foreach(graph => {
        graph.graphId = s"S_${i.incrementAndGet()}_${graph.allNodes.size}"
        graph.writeDotFile(s"${outDir}${graph.graphId}.gv", alsoWriteAsPng = ALSO_WRITE_AS_PNG)
      })
      println(s"Wrote ${i.get} graph files in DOT format to $outDir.") // scalastyle:ignore
    } else {
      var i = 0
      graphs.foreach(graph => {
        i += 1
        graph.graphId = s"S_${i}_${graph.allNodes.size}"
        graph.writeDotFile(s"${outDir}${graph.graphId}.gv", alsoWriteAsPng = ALSO_WRITE_AS_PNG)
      })
      println(s"Wrote $i graph files in DOT format to $outDir.") // scalastyle:ignore
    }

    graphs
  }
}
