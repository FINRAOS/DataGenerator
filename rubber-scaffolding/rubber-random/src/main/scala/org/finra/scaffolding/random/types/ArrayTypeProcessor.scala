package org.finra.scaffolding.random.types
import org.finra.scaffolding.config.Configuration
import org.finra.scaffolding.random.core.RubberRandom
import org.finra.scaffolding.utils.TypeUtils

/**
  * Created by dkopel on 12/13/16.
  */
object ArrayTypeProcessor extends TypeProcessor {
    override def apply[T](typeContainer: TypeContainer[T])(implicit rubberRandom: RubberRandom, conf: Configuration=Configuration.empty): T = {
        TypeUtils.convertToArray(
            rubberRandom.generateCount[T, T](
                typeContainer,
                tc => {
                    val g = TypeContainer(tc.clazz.getComponentType, Option(tc))
                    rubberRandom.generateTypeContainer(g)
                }
            )(conf).toList, typeContainer.clazz).asInstanceOf[T]
    }

    override def eval[T](typeContainer: TypeContainer[T])(
        implicit rubberRandom: RubberRandom,
        conf: Configuration=Configuration.empty
    ): Boolean = typeContainer.clazz.isArray
}