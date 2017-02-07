package org.finra.datagenerator.scaffolding.transformer.join

import java.lang.reflect.Field

/**
  * Created by dkopel on 12/28/16.
  */
case class JoinFieldImpl(key: String, field: Field, clazz: Class[_])
