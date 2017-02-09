package org.finra.datagenerator.scaffolding.transformer.graph.reader

import gremlin.scala._

trait GraphEntity {
    val attributes: Map[String, String]
    def addVertexToGraph[T <: AnyVal](implicit graph: ScalaGraph): Vertex
    def addEdgeToGraph[T <: AnyVal](source: Vertex, target: Vertex, id: Option[AnyVal]=Some[AnyVal]())(implicit graph: ScalaGraph): Edge
    def id: String
    def label: String
}