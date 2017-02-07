package org.finra.datagenerator.scaffolding.knowledge.context

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.finra.datagenerator.scaffolding.hierarchy.Nested
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ListBuffer

/**
  * Created by dkopel on 19/05/16.
  */
case class SimpleKnowledgeContext(
                                           isRoot: Boolean=true,
                                           var data: Map[String, Any] = Map.empty[String, AnyVal],
                                           parents: Seq[KnowledgeContext] = List.empty[KnowledgeContext]
                                       ) extends KnowledgeContext {
    val childrenContexts = new ListBuffer[KnowledgeContext]

    final private val objectMapper: ObjectMapper = new ObjectMapper
    final protected val logger: Logger = LoggerFactory.getLogger(getClass)

    def extend: KnowledgeContext = {
        SimpleKnowledgeContext(false, data, this :: parents.toList)
    }

    def getDepth: Integer = {
        if(isRoot || parents.isEmpty) {
            0
        } else {
            parents.map(c => c.getDepth).max + 1
        }
    }

    def toString(obj: Any): String = {
        try {
            return objectMapper.writeValueAsString(obj)
        }
        catch {
            case e: JsonProcessingException => {
                logger.error("Could not serialize object {}", obj)
            }
        }
        return null
    }

    override def getParents: List[_ <: Nested] = parents.toList

    override def getChildren: List[_ <: Nested] = childrenContexts.toList
}