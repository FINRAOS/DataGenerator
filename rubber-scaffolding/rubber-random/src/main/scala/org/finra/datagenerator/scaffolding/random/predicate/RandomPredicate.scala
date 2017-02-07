package org.finra.datagenerator.scaffolding.random.predicate

import java.util.UUID

/**
  * Created by dkopel on 11/30/16.
  */
trait RandomPredicate[T] extends (RandomContext=>Boolean)
    with Comparable[RandomPredicate[_]] {
    val action: RandomGenerator[T]
    val id: UUID
    val priority: Long

    def compareTo(o: RandomPredicate[_]): Int = priority.compareTo(o.priority)
}