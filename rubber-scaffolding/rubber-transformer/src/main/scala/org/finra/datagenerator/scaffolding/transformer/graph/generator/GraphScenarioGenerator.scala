package org.finra.datagenerator.scaffolding.transformer.graph.generator

import gremlin.scala.{Edge, ScalaGraph, Vertex}
import org.finra.datagenerator.scaffolding.transformer.graph.rules.{EdgeTransformerRule, TransformerRule, VertexTransformerRule}

import scala.collection.mutable.ListBuffer

/**
  * Created by dkopel on 10/27/16.
  */
trait GraphScenarioGenerator {
    def generate(): GraphScenario
    val rules: ListBuffer[TransformerRule[_, _, _]] = collection.mutable.ListBuffer.empty
    def vertexRules[T]: Seq[VertexTransformerRule[T, _]] = rules
    def edgeRules[T]: Seq[EdgeTransformerRule[T, _]] = rules
}
case class GraphScenario(graph: ScalaGraph) {
    def vertices: Seq[Vertex] = graph.V.toList()
    def edges: Seq[Edge] = graph.E.toList()
}
