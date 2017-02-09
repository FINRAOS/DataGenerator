package org.finra.datagenerator.scaffolding.random.types
import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.utils.TypeUtils
import org.finra.datagenerator.scaffolding.random.core.RubberRandom

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