package org.finra.datagenerator.scaffolding.transformer.service

/**
  * Created by dkopel on 12/14/16.
  */
object DefaultContextMethods {
    val methods: Map[String, java.lang.reflect.Method] = Map(
        "asList"->classOf[java.util.Arrays].getDeclaredMethod("asList", classOf[Array[java.lang.Object]])
    )
}