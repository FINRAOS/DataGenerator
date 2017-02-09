package org.finra.datagenerator.scaffolding.hierarchy;

/**
  * Created by dkopel on 14/06/16.
  */
trait DynamicMultipleParentsNested[T <: DynamicMultipleParentsNested[_]] extends MultipleParentsNested {
    def addParent(parent: T): T

    def addChild(child: T): T
}
