package org.finra.datagenerator.scaffolding.utils

/**
  * Created by dkopel on 8/31/16.
  */
@FunctionalInterface trait Predicate[T] {
    def test(implicit t: T): Boolean
}