package org.finra.datagenerator.scaffolding.graph

import gremlin.scala.ScalaGraph
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer

/**
  * Created by dkopel on 9/20/16.
  */
trait GraphFactoryService {
    val host: String
    def existingGraph(location: String): GraphService
    def newGraph(location: Option[String]=Option.empty): GraphService
}

trait GraphService {
    val graph: ScalaGraph
    val compute: GraphComputer
}