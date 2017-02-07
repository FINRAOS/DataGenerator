package org.finra.datagenerator.scaffolding.knowledge.support.util

/**
  * Created by dkopel on 9/22/16.
  */
case class TypeContainer(val clazz: Class[_], val args: Seq[TypeContainer]=List())
