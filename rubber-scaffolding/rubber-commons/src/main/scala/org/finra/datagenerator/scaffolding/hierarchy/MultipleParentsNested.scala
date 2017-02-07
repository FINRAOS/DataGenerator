package org.finra.datagenerator.scaffolding.hierarchy;

/**
  * Created by dkopel on 31/05/16.
  */
trait MultipleParentsNested extends Nested {
    def getParents: Seq[_ <: Nested]
}
