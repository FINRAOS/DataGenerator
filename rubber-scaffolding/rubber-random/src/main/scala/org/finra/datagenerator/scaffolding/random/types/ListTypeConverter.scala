package org.finra.datagenerator.scaffolding.random.types

import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.random.core.RubberRandom

import scala.collection.JavaConverters._
/**
  * Created by dkopel on 12/9/16.
  */
object ListTypeConverter extends TypeProcessor {
    def apply[T, D <: IndexedSeq[T]](value: D, clazz: Class[T]): Any = {
        if(clazz.equals(classOf[scala.collection.immutable.List[_]])) {
            collection.immutable.List(value)
        } else if(clazz.equals(classOf[java.util.List[_]])) {
            value.toList.asJava
        } else {
            value.toList
        }
    }

    override def apply[T](typeContainer: TypeContainer[T])(implicit rubberRandom: RubberRandom, conf: Configuration): T = {
        apply(
            rubberRandom.generateCount[T, T](typeContainer, tc => rubberRandom.generateTypeContainer(tc.types.head))(conf),
            typeContainer.clazz
        ).asInstanceOf[T]
    }

    override def eval[T](typeContainer: TypeContainer[T])(
        implicit rubberRandom: RubberRandom,
        conf: Configuration
    ): Boolean = CollectionTypes.eval(typeContainer)
}