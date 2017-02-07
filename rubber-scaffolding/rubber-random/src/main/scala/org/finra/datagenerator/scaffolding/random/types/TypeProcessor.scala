package org.finra.datagenerator.scaffolding.random.types

import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.random.core.RubberRandom

/**
  * Created by dkopel on 12/13/16.
  */
trait TypeProcessor {
    def eval[T](typeContainer: TypeContainer[T])(implicit rubberRandom: RubberRandom, conf: Configuration): Boolean
    def apply[T](typeContainer: TypeContainer[T])(implicit rubberRandom: RubberRandom, conf: Configuration): T
}
