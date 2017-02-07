package org.finra.datagenerator.scaffolding.transformer.graph.reader

import java.util.concurrent.atomic.AtomicBoolean

import gremlin.scala._
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.finra.datagenerator.scaffolding.utils.ValWrapper
import org.finra.datagenerator.scaffolding.graph.GraphFactoryService
import org.finra.datagenerator.scaffolding.transformer.graph._
import org.finra.datagenerator.scaffolding.transformer.graph.nodes.NodeRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.xml.Elem

/**
  * Created by dkopel on 10/14/16.
  */
@Component
@Scope("prototype")
class GraphMLGraphReader @Autowired() (
                            override val xml: Elem,
                            override val options: GraphReaderOpts = GraphReaderOpts(),
                            private val graphFactoryService: GraphFactoryService,
                            private val _nodeRepository: NodeRepository
                        ) extends GraphScenarioReader {
    val logger = LoggerFactory.getLogger(getClass)
    private val graphXML: ValWrapper[Elem] = ValWrapper()
    private val loaded = new AtomicBoolean(false)
    private implicit val _graph: ScalaGraph = TinkerGraph.open()

    override def globals: Map[String, String] = (graphXML \\ "globals" \\ "data")
        .map(n => n.attribute("key").get.head.text->n.text)
        .toMap

    private def rawNodes: Seq[GraphMLNode] = graphXML \\ "node"

    private def rawEdges: Seq[GraphMLNode] = graphXML \\ "edge"

    private def clazzNodes: Seq[GraphMLNode] = rawNodes.filter(n => n.attributes.contains(GraphReader.clazz))

    private def clazzEdges: Seq[GraphMLNode] = rawNodes.filter(n => n.attributes.contains(GraphReader.clazz))

    private def findEdge(connection: EdgeConnection): Seq[GraphMLNode] = rawEdges.filter(connection)

    private def sourceEdge(id: String): Seq[GraphMLNode] = findEdge(Source(id))

    private def targetEdge(id: String): Seq[GraphMLNode] = findEdge(Target(id))

    private def process: ScalaGraph = {
        graphXML(xml)
        clazzNodes.foreach(n => {
            nodeRepository.registerNode(n.addVertexToGraph)
        })
        clazzNodes.foreach(n => {
            val srcId = n.id
            logger.debug("Looking for edges with the source of {}", srcId)
            val source = GraphUtils.vertexById(srcId)
            val es = sourceEdge(srcId)
            es.foreach(s => {
                logger.debug("Source edge {}", s)
                val target = GraphUtils.vertexById(s.attributes("target"))
                val e: Edge = s.addEdgeToGraph(target, source, Option(s.id.asInstanceOf[AnyVal]))
                nodeRepository.registerNode(e)
            })
        })
        loaded.set(true)
        _graph
    }

    override def graph: ScalaGraph = if(loaded.get) _graph else process

    override def vertices: List[Vertex] = graph.V.toList()

    override def edges: List[Edge] = graph.E.toList()

    override def elements: List[Element] = vertices ++ edges

    override def nodeRepository: NodeRepository = _nodeRepository
}