package org.finra.datagenerator.scaffolding.random.support

import java.lang.reflect.Field
import java.util.UUID

import org.finra.datagenerator.scaffolding.utils.ReflectionUtils
import org.finra.datagenerator.scaffolding.random.predicate._
import org.finra.datagenerator.scaffolding.random.support.annotations.CustomRandomizer
import org.finra.datagenerator.scaffolding.random.types.TypeContainer

import scala.collection.JavaConverters._

/**
  * Created by dkopel on 1/11/17.
  */
class CustomRandomizerProcessor extends AnnotationUtils {
    override val basePackages: Seq[String] = Seq("org.finra.datagenerator.scaffolding")

    case class CustomRandomizerContainer[T](ref: AnyRef, func: RandomGenerator[T], priority: Long) extends FieldReference {
        override val field: java.lang.reflect.Field = ref.asInstanceOf[java.lang.reflect.Field]

        override def canEqual(that: Any): Boolean = that match {
            case a: CustomRandomizerContainer[_] => a.ref.equals(ref)
            case _ => false
        }
    }

    private def scanCustomRandomizers[T](clazz: Class[_]): Seq[CustomRandomizerContainer[_]] = {
        ReflectionUtils.findAnnotationsWithAssociation(
            clazz,
            classOf[CustomRandomizer]
        ).asScala.map(e => {
            CustomRandomizerContainer[T](
                e.getAssociations.asScala.head,
                makeObject(
                    e.getAnnotation.value().asSubclass(classOf[RandomGenerator[T]])
                ),
                e.getAnnotation.priority
            )
        }).toSeq
    }

    def fieldHasCustomRandomizer(field: Field): Boolean = fieldIsPresent(field, scanCustomRandomizers)

    def registerCustomRandomizer[T](typeContainer: TypeContainer[_], field: Field)(implicit predicateRegistry: PredicateRegistry) = {
        val oRandomizer = getField[CustomRandomizerContainer[_]](field)
        if(oRandomizer.isDefined) {
            val randomizer = oRandomizer.get
            logger.debug("Found custom randomizer for field {}: {}", field.getName.asInstanceOf[Any], randomizer.getClass)
            val rId = UUID.randomUUID()
            predicateRegistry.registerPredicate[T](
                CustomPredicate,
                CustomRandomPredicate[T](
                    (a)=>a.parent.get.equals(typeContainer) && a.tc.clazz.equals(field.getType),
                    new RandomGenerator[T] {
                        override def apply(rc: RandomContext): T = {
                            predicateRegistry.deregisterPredicate(CustomPredicate, rId)
                            randomizer.func
                            val func = randomizer.func.asInstanceOf[RandomGenerator[T]]
                            func(rc)
                        }
                    },
                    randomizer.priority,
                    rId
                )
            )
        }
    }
}