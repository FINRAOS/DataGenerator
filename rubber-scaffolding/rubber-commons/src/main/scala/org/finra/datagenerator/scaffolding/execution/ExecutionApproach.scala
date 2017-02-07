package org.finra.datagenerator.scaffolding.execution

import enumeratum._

/**
  * Created by dkopel on 7/12/16.
  */
sealed trait ExecutionApproach extends EnumEntry
object ExecutionApproach extends Enum[ExecutionApproach] {
    val SYNC, ASYNC = new ExecutionApproach {}
    val values = findValues
}