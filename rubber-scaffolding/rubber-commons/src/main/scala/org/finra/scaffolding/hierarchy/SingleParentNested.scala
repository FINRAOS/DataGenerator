package org.finra.scaffolding.hierarchy

/**
  * Created by dkopel on 31/05/16.
  */
trait SingleParentNested extends Nested {
    def getParent[T <: Nested]: T
}