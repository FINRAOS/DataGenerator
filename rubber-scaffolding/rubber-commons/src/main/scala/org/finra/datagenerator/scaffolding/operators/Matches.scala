package org.finra.datagenerator.scaffolding.operators

import enumeratum._

/**
  * Created by dkopel on 06/06/16.
  */
sealed trait Matches extends EnumEntry
object Matches extends Enum[Matches] {
    val ALWAYS, NEVER, SOMETIMES = new Matches {}
    val values = findValues
}