package org.finra.datagenerator

import scala.reflect.ClassTag

/**
  * Created by dkopel on 10/31/16.
  */
package object scaffolding {
    import scala.reflect.runtime.universe.{InstanceMirror, TermName, TermSymbol, Type, runtimeMirror}
    def rm = runtimeMirror(getClass.getClassLoader)
    implicit def clazzToTypeTag[T](clazz: Class[T]): Type = rm.staticClass(clazz.getName).selfType
    implicit def typeToClassTag[T](tpe: Type) = ClassTag[T](rm.runtimeClass(tpe))
    implicit def reflect[T](inst: T)(implicit $ev1: ClassTag[T]): InstanceMirror = runtimeMirror(getClass.getClassLoader).reflect(inst)
    def reflect[T](inst: T, tpe: Type): InstanceMirror = reflect(inst)(typeToClassTag(tpe))
    def fieldOfClass[T](clazz: Class[T], field: String): TermSymbol = clazzToTypeTag(clazz).decl(TermName(field)).asTerm
}
