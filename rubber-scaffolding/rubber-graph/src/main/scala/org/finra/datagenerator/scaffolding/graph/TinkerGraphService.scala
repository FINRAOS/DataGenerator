package org.finra.datagenerator.scaffolding.graph
import gremlin.scala.ScalaGraph
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.springframework.stereotype.Service

/**
  * Created by dkopel on 11/14/16.
  */
@Service
class TinkerGraphService extends GraphService {
    override val graph: ScalaGraph = TinkerGraph.open
    override val compute: GraphComputer = graph.compute()
}
