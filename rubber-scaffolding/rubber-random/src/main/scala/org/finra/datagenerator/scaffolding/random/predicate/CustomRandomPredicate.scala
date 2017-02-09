package org.finra.datagenerator.scaffolding.random.predicate

import java.util.UUID

/**
  * Created by dkopel on 12/6/16.
  */
case class CustomRandomPredicate[T](
                                  predicate: RandomContext=>Boolean,
                                  override val action: RandomGenerator[T],
                                  override val priority: Long=Long.MaxValue,
                                  override val id: UUID=UUID.randomUUID()
                              ) extends RandomPredicate[T] {
    override def apply(rc: RandomContext): Boolean = predicate.apply(rc)
}
