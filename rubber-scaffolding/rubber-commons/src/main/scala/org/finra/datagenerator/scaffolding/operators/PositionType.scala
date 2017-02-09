package org.finra.datagenerator.scaffolding.operators

/**
  * Created by dkopel on 10/06/16.
  */
object PositionType extends Enumeration {
    type PositionType = Value
    val FIXED, FIXED_RANGE, RELATIVE, ABSOLUTE = Value
}