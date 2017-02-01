package org.finra.scaffolding.random.predicate

import org.finra.scaffolding.config.Configuration
import org.finra.scaffolding.random.JavaPrimitiveRandomizer
import org.finra.scaffolding.random.core.JavaPrimitives
import org.finra.scaffolding.random.types.TypeContainer

/**
  * Created by dkopel on 11/30/16.
  */
class RandomContext(val tc: TypeContainer[_])
                   (implicit val conf: Configuration, javaPrimitives: JavaPrimitives) {
    val parent: Option[TypeContainer[_]] = tc.ptc
    val jpr: JavaPrimitiveRandomizer = javaPrimitives.jpr
}
object RandomContext {
    implicit def apply(tc: TypeContainer[_])
                      (implicit conf: Configuration, javaPrimitives: JavaPrimitives): RandomContext = {
        new RandomContext(tc)(conf, javaPrimitives)
    }
}