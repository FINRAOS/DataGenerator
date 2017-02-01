package org.finra.scaffolding.random.strategy

import org.finra.scaffolding.config._
import org.finra.scaffolding.random.Parameter
import org.finra.scaffolding.random.types.TypeContainer
import org.finra.scaffolding.utils.Logging

/**
  * Created by dkopel on 12/6/16.
  */
trait GeneratingStrategy extends Logging {
    def apply[T](typeContainer: TypeContainer[T], args: Seq[Parameter[_]])(implicit conf: Configuration): T
}
object GeneratingStrategy extends Configurable {
    object GeneratingStrategyName extends ConfigName("generatingStrategy")

    val generatingStrategyDef = ConfigDefinition[Class[_ <: GeneratingStrategy]](
        GeneratingStrategyName,
        Some(classOf[FieldGeneratingStrategy])
    )

    override def configBundle: ConfigBundle = ConfigBundle(
        getClass,
        Seq(generatingStrategyDef)
    )
}