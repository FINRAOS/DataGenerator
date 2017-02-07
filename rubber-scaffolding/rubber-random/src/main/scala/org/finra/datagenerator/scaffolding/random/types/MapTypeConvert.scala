package org.finra.datagenerator.scaffolding.random.types

import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.random.core.RubberRandom

import scala.collection.JavaConverters._
/**
  * Created by dkopel on 12/9/16.
  */
object MapTypeConvert extends TypeProcessor {
    def apply[T, D <: IndexedSeq[(T, T)]](value: D, clazz: Class[T]): Any = {
        if(clazz.equals(classOf[scala.collection.immutable.Map[_, _]])) {
            value.toMap
        } else if(clazz.equals(classOf[scala.collection.mutable.Map[_, _]])) {
            collection.mutable.Map.empty[T, T] ++ value.toMap
        } else if(clazz.equals(classOf[java.util.Map[_, _]])) {
            value.toMap.asJava
        } else {
            value.toMap
        }
    }

    override def apply[T](typeContainer: TypeContainer[T])(implicit rubberRandom: RubberRandom, conf: Configuration): T = {
        apply(
            rubberRandom.generateCount[T, (T, T)](typeContainer, tc => {
                val rs = tc.types.map(t => rubberRandom.generateTypeContainer(t))
                rs(0)->rs(1)
            })(conf),
            typeContainer.clazz
        ).asInstanceOf[T]
    }

    override def eval[T](typeContainer: TypeContainer[T])(
        implicit rubberRandom: RubberRandom,
        conf: Configuration
    ): Boolean = CollectionTypes.eval(typeContainer)
}