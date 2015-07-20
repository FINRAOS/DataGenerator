package SocialNetwork_Example.scala

import java.util.concurrent.atomic.AtomicInteger

import Graph.Graph
import GraphEngine.StructureBuilder
import Helpers.FileHelper

import scala.collection.immutable

/**
 * Builds all combinations of graph structures for friendships with a maximum graph size
 */
object SocialNetworkStructureBuilder extends StructureBuilder[User, UserType.UserType, UserStub, UserTypes] {
  def nodeDataTypes = new UserTypes()

  val systemTempDir = System.getProperty("java.io.tmpdir").replaceAllLiterally("\\", "/")
  val outDir = s"$systemTempDir${if (systemTempDir.endsWith("/")) "" else "/"}SocialNetworkGraphs/"
  FileHelper.ensureEmptyDirectoryExists(outDir)

  val WRITE_STRUCTURES_IN_PARALLEL = false // Having same structure be same ID helps debugging...
  val ALSO_WRITE_AS_PNG = true
  override def generateAllNodeDataTypeGraphCombinationsOfLength(length: Int): immutable.Vector[Graph[UserStub]] = {
    val graphs = super.generateAllNodeDataTypeGraphCombinationsOfLength(length)

    if (WRITE_STRUCTURES_IN_PARALLEL) {
      val i = new AtomicInteger(0)
      graphs.par.foreach(graph => {
        graph.graphId = s"S_${i.incrementAndGet()}_${graph.allNodes.size}"
        graph.writeDotFile(s"${outDir}${graph.graphId}.gv", alsoWriteAsPng = ALSO_WRITE_AS_PNG)
      })
      println(s"Wrote ${i.get} graph files in DOT format to $outDir.")
    } else {
      var i = 0
      graphs.foreach(graph => {
        i += 1
        graph.graphId = s"S_${i}_${graph.allNodes.size}"
        graph.writeDotFile(s"${outDir}${graph.graphId}.gv", alsoWriteAsPng = ALSO_WRITE_AS_PNG)
      })
      println(s"Wrote $i graph files in DOT format to $outDir.")
    }

    graphs
  }
}