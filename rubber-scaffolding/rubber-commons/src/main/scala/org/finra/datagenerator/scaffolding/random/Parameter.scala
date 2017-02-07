package org.finra.datagenerator.scaffolding.random

import org.finra.datagenerator.scaffolding.utils.TypeUtils

import scala.reflect.runtime.universe._
/**
  * Created by dkopel on 1/12/17.
  */
case class Parameter[T <: Any](value: T)(implicit typeTag: TypeTag[T]) {
    val clazz: Class[T] = TypeUtils.mirror.runtimeClass(typeTag.tpe).asInstanceOf[Class[T]]
}
object Parameter {
    def apply(args: Seq[Parameter[_]]): (Array[Any], Array[Class[_]]) = {
        (args.map(_.value).toArray, args.map(_.clazz).toArray)
    }
}