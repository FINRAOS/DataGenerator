package org.finra.datagenerator.scaffolding.transformer.graph

import java.io.InputStream

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import gremlin.scala._
import org.apache.tinkerpop.gremlin.process.traversal.{Order, P}
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.finra.datagenerator.scaffolding.graph.GraphFactoryService
import org.finra.datagenerator.scaffolding.transformer.graph.generator.GraphScenarioGenerator
import org.finra.datagenerator.scaffolding.transformer.graph.nodes.NodeRepository
import org.finra.datagenerator.scaffolding.transformer.graph.reader.{GraphMLNode, GraphReaderOpts, GraphScenarioReader}
import org.finra.datagenerator.scaffolding.transformer.graph.rules.RuleConditionOptions
import org.finra.datagenerator.scaffolding.transformer.graph.traverser.{BreadthFirstGraphTraverser, GraphTraverser}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.io.ResourceLoader

import scala.xml.Elem

/**
  * Created by dkopel on 10/14/16.
  */
object GraphUtils {
    @Autowired var applicationContext: ApplicationContext = _
    @Autowired var resourceLoader: ResourceLoader = _
    val objectMapper = new ObjectMapper()
    objectMapper.registerModule(DefaultScalaModule)
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    var ruleConditionOptions: RuleConditionOptions = RuleConditionOptions()
    var graphReadOptions: GraphReaderOpts = GraphReaderOpts()
    val rubberRandom = new org.finra.datagenerator.scaffolding.random.core.RubberRandomImpl()

    def _readResource(resource: String): InputStream = resourceLoader.getResource(resource).getInputStream

    def nodeRepository: NodeRepository = {
        val nr = applicationContext.getBean(classOf[NodeRepository])
        nr.ruleConditionOptions = ruleConditionOptions
        nr
    }

    def loadGraphResource(input: String): GraphScenarioReader = loadGraphString(scala.io.Source.fromInputStream(_readResource(input)).mkString)

    def loadGraphString(xml: String): GraphScenarioReader = loadGraph(scala.xml.XML.loadString(xml))

    def loadGraph(xml: Elem): GraphScenarioReader = {
        applicationContext.getBean(
            classOf[GraphScenarioReader],
            xml,
            graphReadOptions,
            applicationContext.getBean(classOf[GraphFactoryService]),
            nodeRepository
        )
    }

    def generateScenario(reader: GraphScenarioReader): GraphScenarioGenerator = applicationContext.getBean(
        classOf[GraphScenarioGenerator],
        reader,
        classOf[BreadthFirstGraphTraverser],
        applicationContext.getBean(classOf[GraphFactoryService]),
        rubberRandom
    )

    def traverserFactory(traverserClass: Class[_ <: GraphTraverser])(implicit graph: ScalaGraph): GraphTraverser = traverserClass match {
        case t: Class[BreadthFirstGraphTraverser] => BreadthFirstGraphTraverser()
    }

    def vertexById(id: String)(implicit graph: ScalaGraph): Vertex = graph.V.has(Key[String]("id"), id).head()

    def orderByOuts(implicit graph: ScalaGraph): List[(gremlin.scala.Vertex, java.lang.Long)] = {
        graph.V
        .where(v => v.inE.count.is(P.eq(0)))
        .map(v => v->v.outE.count().head())
        .orderBy(z => z._2, Order.incr)
        .toList()
    }

    def findStartingVertex(implicit graph: ScalaGraph): Vertex = {
        val ss = graph.V
            .where(v => v.inE.count.is(P.eq(0)))
            .map(v => v->v.outE.count().head())
            .orderBy(z => z._2, Order.incr).toList()

        if(ss.size == 1) ss.head._1
        else throw new IllegalStateException()
    }

    val clazz = "clazz"
}
case class DefaultVertex()
case class DefaultEdge()
trait EdgeConnection {
    val field: String
    val id: String
}
object EdgeConnection {
    implicit def unapply(edgeConnection: EdgeConnection): GraphMLNode=>Boolean = (n: GraphMLNode) => n.attributes(edgeConnection.field)==edgeConnection.id
}
case class Source(override val id: String) extends EdgeConnection {
    val field = "source"
}
case class Target(override val id: String) extends EdgeConnection {
    val field = "target"
}