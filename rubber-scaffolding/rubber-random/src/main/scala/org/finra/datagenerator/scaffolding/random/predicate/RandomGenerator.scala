package org.finra.datagenerator.scaffolding.random.predicate

import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.random.support.{AnnotationUtils, INTERFACE}

/**
  * Created by dkopel on 1/13/17.
  */
// A simple function that takes in a `RandomContext` and yields an instance of `T`
trait RandomGenerator[T] extends (RandomContext=>T)
object RandomGenerator {
    // This allows for a static value to be yielded
    implicit def apply[T](action: ()=>T): RandomGenerator[T] = {
        new RandomGenerator[T] {
            override def apply(v1: RandomContext): T = action.apply()
        }
    }

    implicit def apply[T](action: (RandomContext)=>T): RandomGenerator[T] = {
        new RandomGenerator[T] {
            override def apply(rc: RandomContext): T = action.apply(rc)
        }
    }
}
// Used to add the scanning capacity to the `RandomGenerator[T]`
trait ClassRandomGenerator[T] extends RandomGenerator[T] {
    // The list of classes that will be associated with the generator
    def classes: Array[Class[_]]

    // The default priority for the generator
    def priority: Long=Long.MaxValue
}
object ClassRandomGenerator {
    def findClassRandomPredicates(implicit conf: Configuration): Set[ClassRandomPredicate[_]] = {
        AnnotationUtils.findAnnotation(conf.getBasePackages, classOf[ClassRandomGenerator[_]], INTERFACE)
            .map(c => ClassRandomGenerator(c.asInstanceOf[Class[ClassRandomGenerator[_]]])).toSet
    }

    def findJavaClassRandomPredicates(implicit conf: Configuration): Set[ClassRandomPredicate[_]] = {
        AnnotationUtils.findAnnotation(conf.getBasePackages, classOf[JavaClassRandomGenerator[_]], INTERFACE)
            .map(c => ClassRandomGenerator.fromJava(c.asInstanceOf[Class[JavaClassRandomGenerator[_]]])).toSet
    }

    implicit def apply(clazz: Class[ClassRandomGenerator[_]])(implicit conf: Configuration): ClassRandomPredicate[_] = {
        val c = clazz.newInstance()
        ClassRandomPredicate(c, c.classes:_*)
    }

    implicit def fromJava(clazz: Class[JavaClassRandomGenerator[_]])(implicit conf: Configuration): ClassRandomPredicate[_] = {
        val c = clazz.newInstance()
        ClassRandomPredicate(
            RandomGenerator((rc) => c.apply(rc)),
            c.priority(),
            c.classes():_*
        )
    }
}
trait RandomValidator[T] extends RandomGenerator[T] {
    def validate(value: T)(rc: RandomContext)(implicit conf: Configuration): Boolean
}