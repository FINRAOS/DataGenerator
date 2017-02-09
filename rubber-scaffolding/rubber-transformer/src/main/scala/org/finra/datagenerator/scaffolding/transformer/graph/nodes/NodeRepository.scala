package org.finra.datagenerator.scaffolding.transformer.graph.nodes

import java.util.UUID

import gremlin.scala.Element
import org.finra.datagenerator.scaffolding.transformer.graph.rules._

/**
  * Created by dkopel on 11/2/16.
  */
trait NodeRepository {
    implicit var ruleConditionOptions: RuleConditionOptions=RuleConditionOptions()
    def registerNode(element: Element): UUID
    def addRule[T](id: UUID, transformerRule: EdgeTransformerRule[T, _])
    def addRule[T](id: UUID, transformerRule: VertexTransformerRule[T, _])
    def nodes: Map[UUID, ElementContainer[_]]
    def lookup(id: UUID): ElementContainer[_]
    def vertices: Seq[VertexContainer[_]]
    def edges: Seq[EdgeContainer[_]]
}
