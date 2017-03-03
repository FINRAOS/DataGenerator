package org.finra.datagenerator.scaffolding.knowledge.rule

import org.finra.scaffolding.knowledge.context.{KnowledgeContext, Variable}

/**
  * Created by dkopel on 9/21/16.
  */
trait Criteria {
    implicit def test(implicit knowledgeContext: KnowledgeContext): Boolean
}
case class CriteriaContainer(criteria: Either[Criteria, Variable[Any]=>Criteria]) extends Criteria {
    var bound: Option[Criteria] = Option.empty

    override def test(implicit knowledgeContext: KnowledgeContext): Boolean = {
        if(bound.isDefined) return bound.get.test
        else if(criteria.isLeft) return criteria.left.get.test
        throw new IllegalArgumentException
    }

    def bind(value: Variable[Any]) = if(criteria.isRight) bound = Option(criteria.right.get.apply(value))

    def unbind = if(criteria.isRight) bound = Option.empty

    def test(value: Variable[Any])(implicit knowledgeContext: KnowledgeContext): Boolean = {
        if(bound.isDefined) bound.get.test
        else if(criteria.isLeft) criteria.left.get.test
        else criteria.right.get.apply(value).test
    }
}
object CriteriaContainer {
    implicit def apply(c: Criteria): CriteriaContainer = CriteriaContainer(criteria=Left(c))
    implicit def apply(c: Variable[Any]=>Criteria): CriteriaContainer = CriteriaContainer(criteria=Right(c))
}