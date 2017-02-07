package org.finra.datagenerator.scaffolding.transformer.graph.traverser

import gremlin.scala.{ScalaGraph, _}

/**
  * Created by dkopel on 10/27/16.
  */
// For breadth first traversal
// - Process all vertexes of the same depth together
// - Then process all of the out edges
case class BreadthFirstGraphTraverser(implicit graph: ScalaGraph) extends AbstractGraphTraverser()(graph) {
    override def processElement(element: Element): Element = {
        if(classOf[Vertex].isInstance(element)) {
            val v = element.asInstanceOf[Vertex]
            queue ++= v.outE.toList()
            v
        }
        else {
            val e = element.asInstanceOf[Edge]
            val o = e.inVertex()
            if(!processed.contains(o) && !queue.contains(o)) {
                queue ++= List(o)
            }
            e
        }
    }

    def forwardQueue: Element = {
        val z = queue.dequeue
        queue = z._2
        z._1
    }

    override def nextNotNull: Element = {
        previous = current
        nextCurrent(forwardQueue)
    }
}
