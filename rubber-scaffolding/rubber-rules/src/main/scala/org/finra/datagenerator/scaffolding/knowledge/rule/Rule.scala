package org.finra.datagenerator.scaffolding.knowledge.rule

import java.util.UUID

import org.finra.datagenerator.scaffolding.messaging.response.Response

/**
  * Created by dkopel on 19/05/16.
  */
trait Rule {
    val id: UUID
    val name: String
    def knowledgePackage: KnowledgePackage

    val positiveReactions: List[Function[KnowledgeContext, Response[_]]] = Nil
    val negativeReactions: List[Function[KnowledgeContext, Response[_]]] = Nil

    def precondition: Option[Rule] = Option.empty

    def evaluate(implicit kc: KnowledgeContext): Boolean
}