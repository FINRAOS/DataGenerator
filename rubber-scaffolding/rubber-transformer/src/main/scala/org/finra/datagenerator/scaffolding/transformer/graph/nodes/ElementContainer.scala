package org.finra.datagenerator.scaffolding.transformer.graph.nodes

import java.util.UUID

import gremlin.scala.Element
import org.finra.datagenerator.scaffolding.transformer.graph.reader.GraphReader
import org.finra.datagenerator.scaffolding.transformer.graph.rules.RuleConditionOptions

import scala.collection.JavaConverters._
/**
  * Created by dkopel on 11/2/16.
  */
trait ElementContainer[T] {
    val uuid: UUID
    implicit val ruleConditionOptions: RuleConditionOptions
    def id: Any
    val element: Element
    def properties: Map[String, AnyVal] = element.keys().asScala
        .map(k => k -> element.property(k).value()).toMap
    def clazz: Class[T] = Class.forName(properties(GraphReader.clazz).toString).asInstanceOf[Class[T]]
    def overrides: Map[String, AnyVal] = properties.filterKeys(k => !ElementContainer.IGNORE.contains(k))
}
object ElementContainer {
    val IGNORE = Seq(GraphReader.clazz, "_id", "id", "_uuid", "_label")
}