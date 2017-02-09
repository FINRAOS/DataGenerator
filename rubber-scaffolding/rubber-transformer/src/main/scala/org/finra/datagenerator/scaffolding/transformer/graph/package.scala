package org.finra.datagenerator.scaffolding.transformer

import scala.collection.JavaConverters._
import gremlin.scala._

/**
  * Created by dkopel on 11/2/16.
  */
package object graph {
    def toMap(element: Element): Map[String, AnyVal] = {
        element.keys().asScala.toList
            .map(k => k -> element.property(k).value())
            .toMap
    }
}