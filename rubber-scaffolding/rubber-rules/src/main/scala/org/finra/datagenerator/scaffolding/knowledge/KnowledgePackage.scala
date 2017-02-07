package org.finra.datagenerator.scaffolding.knowledge

import java.util.UUID

/**
  * Created by dkopel on 19/05/16.
  */
trait KnowledgePackage {
    val id: UUID
    val name: String

    def addKnowledge(knowledges: Seq[Knowledge[_]])

    def getKnowledge: Seq[Knowledge[_]]

    def contains(id: UUID): Boolean

    def contains(name: String): Boolean

    def getKnowledge(id: UUID): Knowledge[_]

    def getKnowledge(name: String): Knowledge[_]
}