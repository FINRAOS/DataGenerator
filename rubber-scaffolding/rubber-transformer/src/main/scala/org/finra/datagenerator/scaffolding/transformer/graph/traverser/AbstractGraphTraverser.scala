package org.finra.datagenerator.scaffolding.transformer.graph.traverser

import gremlin.scala.{ScalaGraph, _}
import org.finra.datagenerator.scaffolding.transformer.graph.GraphUtils
import org.slf4j.LoggerFactory

import scala.collection.immutable.Queue

/**
  * Created by dkopel on 10/27/16.
  */
abstract class AbstractGraphTraverser(implicit graph: ScalaGraph) extends GraphTraverser {
    val g = graph.graph
    val logger = LoggerFactory.getLogger(getClass)

    lazy override val verticesCount: Long = graph.V.count.head()
    lazy override val edgesCount: Long = graph.E.count.head()
    lazy override val totalCount: Long = verticesCount + edgesCount
    var processed: Set[Element] = Set.empty[Element]
    var queue = Queue.empty[Element]

    def starting = GraphUtils.findStartingVertex
    var previous: Element = _
    var current: Element = _

    override def hasNext: Boolean = {
        processed.size != totalCount.toInt
    }

    // Sets the current value
    @throws(clazz=classOf[IllegalArgumentException])
    def nextCurrent(element: Element): Element = {
        if(!processed.contains(element)) {
            current = element
            processed += processElement(current)
            current
        }
        else element
    }

    def processElement(element: Element): Element

    def nextNotNull: Element

    // Returns immediate next element
    // to be processed
    override def next: Element = {
        logger.debug("Processed: {}, Total: {}", processed.size, totalCount.toInt)
        if(hasNext) {
            // First element
            if(current == null) nextCurrent(starting)
            else nextNotNull
        } else {
            throw new NoSuchElementException
        }
    }

    override def reset() {
        current = null
        previous = null
        processed = Set.empty[Element]
        queue = Queue.empty[Element]
    }
}
