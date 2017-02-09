package org.finra.datagenerator.scaffolding.transformer.graph.reader

import gremlin.scala.{Edge, Element, ScalaGraph, Vertex}
import org.finra.datagenerator.scaffolding.transformer.graph.nodes.NodeRepository

import scala.xml.Elem
/**
  * Created by dkopel on 10/14/16.
  */
trait GraphScenarioReader {
    def xml: Elem
    implicit val options: GraphReaderOpts = GraphReaderOpts()
    def nodeRepository: NodeRepository
    def graph: ScalaGraph
    def globals: Map[String, String]
    def vertices: List[Vertex]
    def edges: List[Edge]
    def elements: List[Element]
}
object GraphReader {
    val clazz = "_clazz"
}