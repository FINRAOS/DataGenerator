package org.finra.datagenerator.scaffolding.random.randomizers

import org.finra.datagenerator.scaffolding.config.{AnnotationCapable, AnnotationField}
import org.finra.datagenerator.scaffolding.random.predicate.{RandomContext, RandomGenerator}

/**
  * Created by dkopel on 2/13/17.
  */
class StringEmailRandomizer extends RandomGenerator[String] {
    override def apply(rc: RandomContext): String = new EmailRandomizer().apply(rc).toString
}
