package org.finra.datagenerator.scaffolding.random.strategy

import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.random.Parameter
import org.finra.datagenerator.scaffolding.random.types.TypeContainer

/**
  * Created by dkopel on 12/8/16.
  */
object MethodAccessorGeneratingStrategy extends GeneratingStrategy {
    override def apply[T](typeContainer: TypeContainer[T], args: Seq[Parameter[_]])
                         (implicit conf: Configuration): T = ???
}
