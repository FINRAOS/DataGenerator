package org.finra.datagenerator.scaffolding.knowledge.support.annotations

import java.lang.annotation.{ElementType, Retention, RetentionPolicy, Target}

import scala.annotation.StaticAnnotation

/**
  * Created by dkopel on 6/20/16.
  */
@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.TYPE))
trait SequentialDataArgument extends StaticAnnotation {
    def value: String

    def `type`: Class[_]
}