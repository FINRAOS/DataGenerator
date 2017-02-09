package org.finra.datagenerator.scaffolding.random.types

import java.lang.reflect.{ParameterizedType, Type}

/**
  * Created by dkopel on 11/30/16.
  */
abstract class ParameterizedTypeReference[T] protected() extends Type {
    val parameterizedTypeReferenceSubclass: Class[_] = ParameterizedTypeReference.findParameterizedTypeReferenceSubclass(getClass)
    val pt: Type = parameterizedTypeReferenceSubclass.getGenericSuperclass
    val tpe: Type = pt match {
        case ppt: ParameterizedType => ppt.getActualTypeArguments.apply(0)
    }
}

object ParameterizedTypeReference {
    private def findParameterizedTypeReferenceSubclass(child: Class[_]): Class[_] = {
        val parent: Class[_] = child.getSuperclass
        if (classOf[Any] eq parent) throw new IllegalStateException("Expected ParameterizedTypeReference superclass")
        else if (classOf[ParameterizedTypeReference[_]] eq parent) child
        else findParameterizedTypeReferenceSubclass(parent)
    }
    def apply[T](): ParameterizedTypeReference[T] = {
        new ParameterizedTypeReference[T] {}
    }
}