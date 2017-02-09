package org.finra.datagenerator.scaffolding.transformer.join

/**
  * Created by dkopel on 12/16/16.
  */
case class Joins[T](values: Map[JoinKeys, JoinValues[T]])
