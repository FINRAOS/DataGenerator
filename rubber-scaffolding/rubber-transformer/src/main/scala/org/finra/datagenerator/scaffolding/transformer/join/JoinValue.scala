package org.finra.datagenerator.scaffolding.transformer.join

/**
  * Created by dkopel on 12/28/16.
  */
case class JoinValue[T, S](inst: T, key: String, alias: String, value: S)
