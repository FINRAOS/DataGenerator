package org.finra.datagenerator.scaffolding.knowledge

import java.util.UUID

import org.finra.datagenerator.scaffolding.knowledge.context.KnowledgeContextAware

/**
  * Created by dkopel on 19/05/16.
  */
trait Knowledge[T] extends KnowledgeContextAware {
    val id: UUID
    val name: String
    val knowledgePackage: KnowledgePackage
}