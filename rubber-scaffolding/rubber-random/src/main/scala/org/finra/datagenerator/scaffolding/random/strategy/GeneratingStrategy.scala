package org.finra.datagenerator.scaffolding.random.strategy

import org.finra.datagenerator.scaffolding.config.{ConfigDefinition, ConfigName, Configurable, Configuration}
import org.finra.datagenerator.scaffolding.utils.Logging
import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.random.Parameter
import org.finra.datagenerator.scaffolding.random.types.TypeContainer

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