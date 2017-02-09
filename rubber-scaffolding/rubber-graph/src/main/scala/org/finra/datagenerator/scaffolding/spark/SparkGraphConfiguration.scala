package org.finra.datagenerator.scaffolding.spark

import gremlin.scala._
import org.apache.commons.configuration.BaseConfiguration
import org.apache.tinkerpop.gremlin.hadoop.Constants
import org.apache.tinkerpop.gremlin.hadoop.structure.HadoopGraph
import org.apache.tinkerpop.gremlin.hadoop.structure.io.gryo.GryoInputFormat
import org.apache.tinkerpop.gremlin.spark.structure.Spark
import org.apache.tinkerpop.gremlin.spark.structure.io.PersistedOutputRDD
import org.apache.tinkerpop.gremlin.spark.structure.io.gryo.GryoSerializer
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory

/**
  * Created by dkopel on 11/14/16.
  */
case class SparkGraphConfiguration(
                                         host: String,
                                         inputLocation: Option[String]=Option.empty,
                                         outputLocation: Option[String]=Option.empty
                                     ) {
    val configuration = new BaseConfiguration()
    configuration.setProperty("spark.master", host)

    Spark.create(configuration)

    configuration.setProperty("spark.serializer", classOf[GryoSerializer].getCanonicalName)
    configuration.setProperty(Graph.GRAPH, classOf[HadoopGraph].getName)
    configuration.setProperty(Constants.GREMLIN_HADOOP_GRAPH_READER, classOf[GryoInputFormat].getCanonicalName)
    configuration.setProperty(Constants.GREMLIN_HADOOP_GRAPH_WRITER, classOf[PersistedOutputRDD].getCanonicalName)
    configuration.setProperty(Constants.GREMLIN_HADOOP_JARS_IN_DISTRIBUTED_CACHE, false)

    if(inputLocation.isDefined) {
        configuration.setProperty(Constants.GREMLIN_HADOOP_INPUT_LOCATION, inputLocation.get)
    }

    configuration.setProperty(Constants.GREMLIN_SPARK_PERSIST_CONTEXT, true)
    configuration.setProperty(Constants.GREMLIN_HADOOP_OUTPUT_LOCATION, classOf[PersistedOutputRDD].getCanonicalName)
//    if(outputLocation.isDefined) {
//        configuration.setProperty(Constants.GREMLIN_HADOOP_OUTPUT_LOCATION, outputLocation)
//    }

    lazy val graph = GraphFactory.open(configuration)
    lazy val scalaGraph = graph.asScala
}
