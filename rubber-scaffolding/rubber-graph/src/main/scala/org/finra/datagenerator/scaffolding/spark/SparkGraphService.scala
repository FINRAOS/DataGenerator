package org.finra.datagenerator.scaffolding.spark

import gremlin.scala._
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer
import org.finra.datagenerator.scaffolding.graph.{GraphFactoryService, GraphService}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

/**
  * Created by dkopel on 9/20/16.
  */
trait SparkGraphService extends GraphService {
    lazy override val compute: GraphComputer = graph.compute(classOf[SparkGraphComputer])
}

@Service
class SparkGraphFactoryService @Autowired()(@Value("${spark.master:local[4]}") override val host: String) extends GraphFactoryService {

    override def existingGraph(location: String): GraphService = {
        new SparkGraphService {
            override val graph: ScalaGraph = SparkGraphConfiguration(host, Option.empty, Option(location)).graph
        }
    }

    override def newGraph(location: Option[String]=Option.empty): GraphService = {
        new SparkGraphService {
            override val graph: ScalaGraph = SparkGraphConfiguration(host, location).graph
        }
    }
}