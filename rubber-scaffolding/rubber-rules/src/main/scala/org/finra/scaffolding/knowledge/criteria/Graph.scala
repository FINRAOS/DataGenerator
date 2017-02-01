package org.finra.scaffolding.knowledge.criteria

import org.apache.tinkerpop.gremlin.structure.Graph
import org.finra.scaffolding.knowledge.context.KnowledgeContext
import org.finra.scaffolding.knowledge.rule.Criteria
import org.slf4j.LoggerFactory

/**
  * Created by dkopel on 10/7/16.
  */
object Graph {
    val logger = LoggerFactory.getLogger(getClass)

    def hasNode(clazz: Class[_], graph: Graph) = ???
    def hasEdge(clazz: Class[_], graph: Graph) = ???

    case class HasNode(clazz: Class[_]) extends Criteria {
        override def test(implicit t: KnowledgeContext): Boolean = ???
    }

    case class HasEdge(clazz: Class[_]) extends Criteria {
        override def test(implicit t: KnowledgeContext): Boolean = ???
    }
}
