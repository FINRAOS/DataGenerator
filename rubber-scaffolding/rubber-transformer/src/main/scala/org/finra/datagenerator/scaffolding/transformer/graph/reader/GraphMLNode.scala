package org.finra.datagenerator.scaffolding.transformer.graph.reader

import gremlin.scala._
import org.apache.tinkerpop.gremlin.structure.T

import scala.xml.{Node, NodeSeq}

/**
  * Created by dkopel on 10/20/16.
  */
object GraphMLNode {
    def getNodeAttributes(node: Node):  Map[String, String] = {
        var m = (node \ "data").map(z=>z.attribute("key").get.head.text -> z.text).toMap
        var as = node.attributes.map(t => t.key->t.value.toString).toMap[String, String]
        m ++ as
    }

    implicit def apply(node: Node): GraphMLNode = new GraphMLNode(node, getNodeAttributes(node))
    implicit def apply(nodes: NodeSeq): Seq[GraphMLNode] = nodes.map(node => new GraphMLNode(node, getNodeAttributes(node)))
}

case class Vert(@id id: Int, @label label: Option[String])

case class GraphMLNode(
    node: Node,
    override val attributes: Map[String, String]
) extends GraphEntity {
    override def toString: String = attributes.toString
    override def addVertexToGraph[T](implicit graph: ScalaGraph): Vertex = {
        val v = graph + Vert(id.toInt, Some(label))
        attributes.foreach(a => {
            v.setProperty(Key(a._1), a._2)
        })
        v
    }
    override def addEdgeToGraph[T](source: Vertex, target: Vertex, id: Option[AnyVal]=Some[AnyVal]())(implicit graph: ScalaGraph): Edge = {
        val e = {
            if(id.isDefined) target.addEdge(label, source, T.id, id.get.asInstanceOf[Object])
            else target.addEdge(label, source)
        }
        attributes.foreach(a => e.setProperty(Key(a._1), a._2))
        e
    }
    override def id: String = attributes("id")
    override def label: String = {
        if(attributes.contains("label")) attributes("label")
        else if(attributes.contains("_label")) attributes("_label")
        else id
    }
}