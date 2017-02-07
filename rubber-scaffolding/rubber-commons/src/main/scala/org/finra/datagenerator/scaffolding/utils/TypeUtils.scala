package org.finra.datagenerator.scaffolding.utils

import scala.reflect.runtime.universe._
import scala.reflect.{ClassTag, ManifestFactory, api}
/**
  * Created by dkopel on 12/8/16.
  */
object TypeUtils {
    val mirror: Mirror = runtimeMirror(getClass.getClassLoader)

    def getBaseClass[T](tpe: Type): Class[T] = TypeUtils.mirror.runtimeClass(tpe).asInstanceOf[Class[T]]

    def getType[T](clazz: Class[T]): Type = TypeUtils.mirror.classSymbol(clazz).toType

    def getBaseClass[T](typeTag: TypeTag[T]): Class[T] = getBaseClass(typeTag.tpe).asInstanceOf[Class[T]]

    def getTypes[T](typeTag: TypeTag[T]): List[Type] = typeTag.tpe.typeArgs

    def toManifest[T:TypeTag]: Manifest[T] = {
        val t = typeTag[T]
        val mirror = t.mirror
        def toManifestRec(t: Type): Manifest[_] = {
            val clazz = ClassTag[T](mirror.runtimeClass(t)).runtimeClass
            if (t.typeArgs.length == 1) {
                val arg = toManifestRec(t.typeArgs.head)
                ManifestFactory.classType(clazz, arg)
            } else if (t.typeArgs.length > 1) {
                val args = t.typeArgs.map(x => toManifestRec(x))
                ManifestFactory.classType(clazz, args.head, args.tail: _*)
            } else {
                ManifestFactory.classType(clazz)
            }
        }
        toManifestRec(t.tpe).asInstanceOf[Manifest[T]]
    }

    def stringToTypeTag[A](name: String): TypeTag[A] = {
        val c = Class.forName(name)  // obtain java.lang.Class object from a string
        val mirror = runtimeMirror(c.getClassLoader)  // obtain runtime mirror
        val sym = mirror.staticClass(name)  // obtain class symbol for `c`
        val tpe = sym.selfType  // obtain type object for `c`
        // create a type tag which contains above type object
        TypeTag(mirror, new api.TypeCreator {
            def apply[U <: api.Universe with Singleton](m: api.Mirror[U]) =
                if (m eq mirror) tpe.asInstanceOf[U # Type]
                else throw new IllegalArgumentException(s"Type tag defined in $mirror cannot be migrated to other mirrors.")
        })
    }

    def convertToArray[T](ls: List[T], clazz: Class[_ <: T]): Array[T] = {
        val arr: Array[T] = java.lang.reflect.Array.newInstance(clazz.getComponentType, ls.size).asInstanceOf[Array[T]]
        for(i <- ls.indices) arr(i) = ls(i)
        arr
    }
}