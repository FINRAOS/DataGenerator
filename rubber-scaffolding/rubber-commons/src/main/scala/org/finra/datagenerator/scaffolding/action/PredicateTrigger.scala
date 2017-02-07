package org.finra.datagenerator.scaffolding.action

import org.finra.datagenerator.scaffolding.utils.Predicate

/**
  * Created by dkopel on 10/5/16.
  */
trait PredicateTrigger[T] extends Trigger[T] with Predicate[T]