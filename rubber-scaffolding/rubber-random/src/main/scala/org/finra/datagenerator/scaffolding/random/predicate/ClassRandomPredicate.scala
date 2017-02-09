package org.finra.datagenerator.scaffolding.random.predicate

import java.util.UUID

import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.random.types.TypeContainer

/**
  * Created by dkopel on 12/6/16.
  */
class ClassRandomPredicate[T](
                    override val action: RandomGenerator[T],
                    override val priority: Long,
                    val typeContainers: Array[TypeContainer[_]],
                    override val id: UUID=UUID.randomUUID()
               ) extends RandomPredicate[T]
               {
                   override def apply(rc: RandomContext): Boolean = typeContainers.exists(tc => tc.equals(rc.tc))
               }

object ClassRandomPredicate {
    implicit def convert[T](classes: Seq[Class[_]])(implicit conf: Configuration): Array[TypeContainer[_]] = {
        classes.map(c => TypeContainer(c)(conf)).toArray[TypeContainer[_]]
    }

    def apply[_](action: RandomGenerator[_], tc: Array[TypeContainer[_]], priority: Long=Long.MaxValue)(implicit conf: Configuration): ClassRandomPredicate[_] = {
        new ClassRandomPredicate(action, priority, tc)
    }

    def apply[_](rp: ClassRandomPredicate[_], rc: RandomContext)(implicit conf: Configuration): ClassRandomPredicate[_] = {
        ClassRandomPredicate(rp.action, rp.typeContainers, rp.priority)
    }

    def apply[_](action: RandomGenerator[_], classes: Class[_]*)(implicit conf: Configuration): ClassRandomPredicate[_] = {
        ClassRandomPredicate(action, classes)
    }

    def apply[_](action: RandomGenerator[_], priority: Long, classes: Class[_]*)(implicit conf: Configuration): ClassRandomPredicate[_] = {
        ClassRandomPredicate(action, classes, priority)
    }

    def apply[_](action: ()=>_, classes: Class[_]*)(implicit conf: Configuration): ClassRandomPredicate[_] = {
        ClassRandomPredicate(action, classes)
    }

    def apply[_](action: ()=>_, priority: Long, classes: Class[_]*)(implicit conf: Configuration): ClassRandomPredicate[_] = {
        ClassRandomPredicate(action, classes, priority)
    }

    def apply[_](action: AnyVal, classes: Class[_]*)(implicit conf: Configuration): ClassRandomPredicate[_] = {
        ClassRandomPredicate((rc: RandomContext)=>action, classes)
    }

    def apply[_](action: AnyVal, priority: Long, classes: Class[_]*)(implicit conf: Configuration): ClassRandomPredicate[_] = {
        ClassRandomPredicate((rc: RandomContext)=>action, classes, priority)
    }
}