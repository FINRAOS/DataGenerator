package org.finra.datagenerator.scaffolding.dependency

/**
  * Created by dkopel on 9/20/16.
  */
trait EntityDependency[+T] extends Dependency {
    type S <: T
    val clazz: Class[S]
    def entities: Seq[T]
}