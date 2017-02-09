package org.finra.datagenerator.scaffolding.transformer.graph.generator

import java.util.UUID

import gremlin.scala._
import org.apache.tinkerpop.gremlin.structure.T
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.utils.ReflectionUtils
import org.finra.datagenerator.scaffolding.graph.GraphFactoryService
import org.finra.datagenerator.scaffolding.random.core.RubberRandom
import org.finra.datagenerator.scaffolding.transformer.graph._
import org.finra.datagenerator.scaffolding.transformer.graph.nodes.{EdgeContainer, ElementContainer, VertexContainer}
import org.finra.datagenerator.scaffolding.transformer.graph.reader.GraphScenarioReader
import org.finra.datagenerator.scaffolding.transformer.graph.traverser.GraphTraverser
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
  * Created by dkopel on 10/16/16.
  */
@Component
@Scope("prototype")
class SimpleGraphScenarioGenerator(private val reader: GraphScenarioReader,
                                   private val traverserClass: Class[GraphTraverser],
                                   private val graphService: GraphFactoryService,
                                   private val rubberRandom: RubberRandom
                                  ) extends GraphScenarioGenerator{
    val logger = LoggerFactory.getLogger(getClass)
    val IGNORE = Seq(classOf[AnyVal], classOf[Null])
    val conf = Configuration.empty

    private val objectMapper = GraphUtils.objectMapper

    private def randomObject[T](clazz: Class[T]): T = rubberRandom.generate[T](clazz)(conf)

    private def validClass(clazz: Class[_]) = !IGNORE.contains(clazz)

    private def convertValue[T](mapData: Map[String, AnyVal], clazz: Class[T]) = {
        if(validClass(clazz)) objectMapper.convertValue(mapData, clazz)
        else mapData
    }

    private def setOverrides[T](inst: T, ec: ElementContainer[T]) = {
        logger.debug(s"For element ${ec.id} these are the override: ${ec.overrides.mkString(", ")}")
        ec.overrides.foreach(t => {
            try {
                val field = ReflectionUtils.getDeepProperty(ec.clazz, t._1)
                logger.debug("Field {}", field.getName)
                ReflectionUtils.setField(field, inst, objectMapper.convertValue(t._2, field.getType))
            } catch {
                case e: IllegalAccessException => logger.warn("Error: {}", e.getMessage)
                case e: NoSuchFieldException => logger.warn("An instance of the class {} cannot accept the override property `{}`", ec.clazz.getName, e.getMessage.asInstanceOf[Any])
                case e: Exception => logger.error(e.getMessage)
            }
        })
    }

    private def generateVertex[T](vertex: VertexContainer[T]): VertexDataContainer[T] = {
        val o = rubberRandom.generate[T](vertex.clazz)(conf)
        setOverrides(o, vertex)

        vertex.rules.foreach(r => {
            logger.debug("Vertex rule {}", r)
            if(!vertex.overrides.keySet.contains(r.target.propertyName)) r.eval(vertex, o)
        })

        VertexDataContainer(vertex, o)
    }

    private def generateEdge[T](edge: EdgeContainer[T]): EdgeDataContainer[T] = {
        val o = rubberRandom.generate[T](edge.clazz)(conf)
        setOverrides(o, edge)

        edge.rules.foreach(r => {
            logger.debug("Edge rule {}", r)
            if(!edge.overrides.keySet.contains(r.target.propertyName)) r.eval(edge, o)
        })

        EdgeDataContainer(edge, o)
    }

    private def addInstanceToElement[T](element: Element, inst: T) = {
        val props = objectMapper.convertValue(inst, classOf[Map[String, AnyVal]])
        props.foreach(t => element.property(t._1, t._2))
    }

    private def addVertex(vertex: VertexDataContainer[_])(implicit graph: ScalaGraph): Vertex = {
        val id = vertex.vertex.vertex.id.toString.toInt
        val label = vertex.vertex.vertex.label
        logger.debug("Adding id {}", id)
        val v = graph.asJava.addVertex(T.id, id.asInstanceOf[java.lang.Integer], T.label, label)
        logger.debug("Added vertex {}", v.id())
        vertex.vertex.properties.foreach(t => v.property(t._1, t._2))
        addInstanceToElement(v, vertex.data)
        logger.debug("Returning vertex {}", v.id())
        v
    }

    private def addEdge(edge: EdgeDataContainer[_])(implicit graph: ScalaGraph): Edge = {
        val sid = edge.edge.edge.outVertex().id()
        val tid = edge.edge.edge.inVertex().id()
        logger.debug("Sid {}, Tid {}", sid.asInstanceOf[Any], tid.asInstanceOf[Any])
        val source = graph.V(sid).head()
        val target = graph.V(tid).head()
        val e = target.addEdge(edge.edge.edge.label, source)
        e.edge.valueMap.foreach(t => e.property(t._1, t._2))
        addInstanceToElement(e, edge.data)
        e
    }

    case class VertexDataContainer[T](vertex: VertexContainer[T], data: T)
    case class EdgeDataContainer[T](edge: EdgeContainer[T], data: T)

    def traverseNodes = {
        val traverser = GraphUtils.traverserFactory(traverserClass)(reader.graph)
        implicit val graph = reader.graph
        type T = AnyVal
        traverser.map(element => {
            val uuid = element.property("uuid").value.asInstanceOf[UUID]
            val container = reader.nodeRepository.lookup(uuid)

            element match {
                case vertex: Vertex => {
                    vertexRules[T].foreach(r => {
                        reader.nodeRepository.addRule(uuid, r)
                    })
                     generateVertex(container.asInstanceOf[VertexContainer[T]])
                }
                case edge: Edge => {
                    edgeRules[T].foreach(r => {
                        reader.nodeRepository.addRule(uuid, r)
                    })
                    generateEdge(container.asInstanceOf[EdgeContainer[T]])
                }
            }
        })
    }

    override def generate(): GraphScenario = {
//        implicit val outputGraph = graphService.newGraph().graph
        implicit val outputGraph = TinkerGraph.open().asScala
        val elements = traverseNodes

        elements.filter(_.isInstanceOf[VertexDataContainer[T]])
            .foreach(el => addVertex(el.asInstanceOf[VertexDataContainer[T]]))

        elements.filter(_.isInstanceOf[EdgeDataContainer[T]])
            .foreach(el => addEdge(el.asInstanceOf[EdgeDataContainer[T]]))

        GraphScenario(outputGraph)
    }
}