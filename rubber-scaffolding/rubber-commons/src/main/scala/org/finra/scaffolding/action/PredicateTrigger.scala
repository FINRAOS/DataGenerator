package org.finra.scaffolding.action

import org.finra.scaffolding.utils.Predicate

/**
  * Created by dkopel on 10/5/16.
  */
trait PredicateTrigger[T] extends Trigger[T] with Predicate[T]