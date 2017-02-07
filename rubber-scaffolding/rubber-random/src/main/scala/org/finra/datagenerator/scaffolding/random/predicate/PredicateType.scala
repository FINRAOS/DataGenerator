package org.finra.datagenerator.scaffolding.random.predicate

/**
  * Created by dkopel on 12/6/16.
  */
sealed trait PredicateType
object InternalPredicate extends PredicateType
object CustomPredicate extends PredicateType