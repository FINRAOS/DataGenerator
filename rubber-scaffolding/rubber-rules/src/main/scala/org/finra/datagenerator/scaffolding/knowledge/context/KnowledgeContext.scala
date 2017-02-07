package org.finra.datagenerator.scaffolding.knowledge.context

/**
  * Created by dkopel on 19/05/16.
  */
object KnowledgeContext {
    val NO_ENTITY: Long = 1490L
}

trait KnowledgeContext extends Context with Serializable {
    var data: Map[String, Any]
    def extend: KnowledgeContext
}