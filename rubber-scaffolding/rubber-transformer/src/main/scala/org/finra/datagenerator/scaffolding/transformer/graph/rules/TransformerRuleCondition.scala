package org.finra.datagenerator.scaffolding.transformer.graph.rules

import org.finra.datagenerator.scaffolding.transformer.graph.nodes.{EdgeContainer, ElementContainer, VertexContainer}

/**
  * Created by dkopel on 11/2/16.
  */
abstract class TransformerRuleCondition[C <: ElementContainer[T], T <: Any](clazz: Class[T], cond: C=>Boolean) extends RuleCondition {
    def apply(element: C)(implicit options: RuleConditionOptions): Boolean = {
        if(eval(element.clazz, clazz)) cond(element)
        else false
    }

    def eval(elementClazz: Class[_])(implicit options: RuleConditionOptions): Boolean = eval(elementClazz, clazz)

}

case class VertexRuleCondition[T <: Any](clazz: Class[T], cond: VertexContainer[T]=>Boolean=(t: VertexContainer[T])=>true) extends TransformerRuleCondition[VertexContainer[T], T](clazz, cond)
case class EdgeRuleCondition[T <: Any](clazz: Class[T], cond: EdgeContainer[T]=>Boolean=(t: EdgeContainer[T])=>true) extends TransformerRuleCondition[EdgeContainer[T], T](clazz, cond)