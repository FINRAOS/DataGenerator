package org.finra.datagenerator.scaffolding.transformer.graph.rules


import java.util.UUID

import org.finra.datagenerator.scaffolding.transformer.graph.GraphUtils
import org.finra.datagenerator.scaffolding.transformer.graph.nodes.{EdgeContainer, ElementContainer, VertexContainer}

trait TransformerRule[C <: ElementContainer[T], T, U] {
    val id = UUID.randomUUID()
    def cond: TransformerRuleCondition[C, T]
    def target: RuleTarget[T, U]
    def action: C=>U
    def eval(element: C, inst: T)(implicit options: RuleConditionOptions=RuleConditionOptions()): C = {
        if(cond.apply(element)) {
            target.set(inst, action(element))
        }
        element
    }
}

object TransformerRule {
    implicit def apply[T](map: Map[_, _])(implicit clazz: Class[T]): T = GraphUtils.objectMapper.convertValue(map, clazz)
    implicit def unapply[T](element: T): Map[_, _] = GraphUtils.objectMapper.convertValue(element, classOf[Map[_, _]])
    implicit def toVertexRules[T](rules: Seq[TransformerRule[_, _, _]]): Seq[VertexTransformerRule[T, _]] = rules.filter(r => r.isInstanceOf[VertexTransformerRule[T, _]]).map(_.asInstanceOf[VertexTransformerRule[T, _]])
    implicit def toEdgeRules[T](rules: Seq[TransformerRule[_, _, _]]): Seq[EdgeTransformerRule[T, _]] = rules.filter(r => r.isInstanceOf[EdgeTransformerRule[T, _]]).map(_.asInstanceOf[EdgeTransformerRule[T, _]])
    def transformerRuleFilter[T <: TransformerRule[_, _, _]](t: T) = t match {
        case v: VertexTransformerRule[_, _] => v
    }
}

case class VertexTransformerRule[T, U](
                                          cond: VertexRuleCondition[T],
                                          target: RuleTarget[T, U],
                                            action: VertexContainer[T]=>U
                                      ) extends TransformerRule[VertexContainer[T], T, U]
case class EdgeTransformerRule[T, U](
                                        cond: EdgeRuleCondition[T],
                                        target: RuleTarget[T, U],
                                        action: EdgeContainer[T]=>U
                                    ) extends TransformerRule[EdgeContainer[T], T, U]

