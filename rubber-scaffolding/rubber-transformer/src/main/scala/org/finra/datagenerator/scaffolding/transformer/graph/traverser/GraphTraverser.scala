package org.finra.datagenerator.scaffolding.transformer.graph.traverser

import gremlin.scala._

/**
  * Created by dkopel on 10/27/16.
  */
trait GraphTraverser extends Iterator[Element] {
    def next: Element
    def hasNext: Boolean
    def reset()
    val verticesCount: Long
    val edgesCount: Long
    val totalCount: Long
}
