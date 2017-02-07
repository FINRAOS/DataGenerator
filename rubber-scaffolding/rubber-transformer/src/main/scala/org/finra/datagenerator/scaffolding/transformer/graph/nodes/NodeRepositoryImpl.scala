package org.finra.datagenerator.scaffolding.transformer.graph.nodes

import java.util.UUID

import gremlin.scala.{Edge, Element, Vertex}
import org.finra.datagenerator.scaffolding.transformer.graph.rules._
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
  * Created by dkopel on 11/2/16.
  */
@Component
@Scope("prototype")
class NodeRepositoryImpl extends NodeRepository {
    private var _nodes = Map[UUID, ElementContainer[_]]()
    private val logger = LoggerFactory.getLogger(getClass)

    override def nodes: Map[UUID, ElementContainer[_]] = _nodes

    override def registerNode(element: Element): UUID = {
        val ec: ElementContainer[_] = element match {
            case v: Vertex => VertexContainerImpl(v)
            case e: Edge => EdgeContainerImpl(e)
            case _ => throw new IllegalArgumentException
        }
        logger.debug("Registered node {} with uuid {}, there are now {} nodes", element.id().toString, ec.uuid.toString, _nodes.size.toString)
        _nodes = _nodes + (ec.uuid->ec)
        ec.uuid
    }

    override def lookup(id: UUID): ElementContainer[_] = _nodes(id)

    override def vertices: Seq[VertexContainer[_]] = _nodes.values.filter(e => e.isInstanceOf[VertexContainer[_]]).map(e => e.asInstanceOf[VertexContainer[_]]).toSeq

    override def edges: Seq[EdgeContainer[_]] = _nodes.values.filter(e => e.isInstanceOf[EdgeContainer[_]]).map(e => e.asInstanceOf[EdgeContainer[_]]).toSeq

    override def addRule[T](id: UUID, transformerRule: EdgeTransformerRule[T, _]) = _nodes(id) match {
        case e: EdgeContainer[T] => e.addRule(transformerRule)
    }

    override def addRule[T](id: UUID, transformerRule: VertexTransformerRule[T, _]) = _nodes(id) match {
        case e: VertexContainer[T] => {
            e.addRule(transformerRule)
            logger.info("Vertex {} has {} rules", id, e.rules.size)
        }
    }
}
