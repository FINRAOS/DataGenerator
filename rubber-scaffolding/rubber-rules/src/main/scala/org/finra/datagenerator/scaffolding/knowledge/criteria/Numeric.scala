package org.finra.datagenerator.scaffolding.knowledge.criteria

import org.finra.scaffolding.knowledge.context.{KnowledgeContext, Variable}
import org.finra.scaffolding.knowledge.rule.Criteria
import org.slf4j.LoggerFactory

/**
  * Created by dkopel on 10/5/16.
  */
object Numeric {
    val logger = LoggerFactory.getLogger(getClass)

    def convert(any: Variable[Any])(implicit kc: KnowledgeContext): spire.math.Number = {
        if(any.name.isDefined && kc.data.contains(any.name.get)) spire.math.Number.apply(kc.data(any.name.get).toString)
        else spire.math.Number.apply(any.value.toString)
    }
    def gt(num: Variable[Any], any: Variable[Any])(implicit kc: KnowledgeContext): Boolean = convert(num) > convert(any)
    def gte(num: Variable[Any], any: Variable[Any])(implicit kc: KnowledgeContext): Boolean = convert(num) > convert(any)
    def lt(num: Variable[Any], any: Variable[Any])(implicit kc: KnowledgeContext): Boolean = convert(num) < convert(any)
    def lte(num: Variable[Any], any: Variable[Any])(implicit kc: KnowledgeContext): Boolean = convert(num) < convert(any)
    def sequentialIncrement(inputs: Seq[Variable[Any]])(implicit kc: KnowledgeContext): Boolean = {
        val is = convertSeq(inputs)
        is.sortWith((t1, t2) => t1 < t2) == is
    }
    def convertSeq(inputs: Seq[Variable[Any]])(implicit kc: KnowledgeContext): Seq[spire.math.Number] = inputs.map(convert)


    case class SequentialIncrement(inputs: Any*) extends Criteria {
        implicit override def test(implicit knowledgeContext: KnowledgeContext): Boolean = sequentialIncrement(inputs)
    }


    case class GreaterThan(num0: Variable[Any], num1: Variable[Any]) extends Criteria {
        override def test(implicit knowledgeContext: KnowledgeContext): Boolean = gt(num0, num1)
    }
    object GreaterThan {
        def apply(num: AnyVal): Variable[Any]=>GreaterThan = new GreaterThan(num, _:Any)
    }



    case class LessThan(num0: Variable[Any], num1: Variable[Any]) extends Criteria {
        override def test(implicit kc: KnowledgeContext): Boolean = lt(num0, num1)
    }
    object LessThan {
        def apply(num: AnyVal): Variable[Any]=>LessThan = LessThan(num, _:Any)
    }
}