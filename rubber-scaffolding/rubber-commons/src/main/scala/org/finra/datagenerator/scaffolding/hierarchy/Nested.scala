package org.finra.datagenerator.scaffolding.hierarchy;

/**
  * Created by dkopel on 31/05/16.
  */
trait Nested extends scala.Serializable {
    def isRoot: Boolean

    def getDepth: Integer

    def getChildren: Seq[_ <: Nested]
}
