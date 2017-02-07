package org.finra.datagenerator.scaffolding.transformer.graph.nodes

import java.util.UUID

import gremlin.scala._
import org.finra.datagenerator.scaffolding.transformer.graph.rules.{EdgeTransformerRule, RuleConditionOptions}

/**
  * Created by dkopel on 11/2/16.
  */
trait EdgeContainer[T] extends ElementContainer[T] {
    def addRule(transformerRule: EdgeTransformerRule[T, _])
    val edge: Edge
    def rules: Seq[EdgeTransformerRule[T, _]]
}

case class EdgeContainerImpl[T](override val edge: Edge)(implicit override val ruleConditionOptions: RuleConditionOptions) extends EdgeContainer[T] {
    override val uuid = UUID.randomUUID()
    edge.property("uuid", uuid)

    override val element = edge

    private var _rules = Seq.empty[EdgeTransformerRule[T, _]]

    def addRule(transformerRule: EdgeTransformerRule[T, _]) = {
        if(transformerRule.cond.eval(clazz)) _rules = _rules :+ transformerRule
    }

    override def rules: Seq[EdgeTransformerRule[T, _]] = _rules

    override def id: Any = edge.id
}