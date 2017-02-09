package org.finra.datagenerator.scaffolding.transformer.graph.nodes

import java.util.UUID

import gremlin.scala._
import org.finra.datagenerator.scaffolding.transformer.graph.rules.{RuleConditionOptions, VertexTransformerRule}

/**
  * Created by dkopel on 11/2/16.
  */
trait VertexContainer[T] extends ElementContainer[T] {
    def addRule(transformerRule: VertexTransformerRule[T, _])
    val vertex: Vertex
    def rules: Seq[VertexTransformerRule[T, _]]
}

case class VertexContainerImpl[T](override val vertex: Vertex)(implicit override val ruleConditionOptions: RuleConditionOptions) extends VertexContainer[T] {
    override val uuid = UUID.randomUUID()
    vertex.property("uuid", uuid)

    override val element = vertex

    private var _rules = Seq.empty[VertexTransformerRule[T, _]]

    def addRule(transformerRule: VertexTransformerRule[T, _]) = {
        if(transformerRule.cond.eval(clazz)) _rules = _rules :+ transformerRule
    }

    override def rules: Seq[VertexTransformerRule[T, _]] = _rules

    override def id: Any = vertex.id
}