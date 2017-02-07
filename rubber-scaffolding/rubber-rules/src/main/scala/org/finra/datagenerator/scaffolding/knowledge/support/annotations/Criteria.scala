package org.finra.datagenerator.scaffolding.knowledge.support.annotations

import java.lang.annotation.{ElementType, Retention, RetentionPolicy, Target}

import scala.annotation.StaticAnnotation

/**
  * Created by dkopel on 01/06/16.
  */
@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.TYPE)) trait Criteria extends StaticAnnotation {}