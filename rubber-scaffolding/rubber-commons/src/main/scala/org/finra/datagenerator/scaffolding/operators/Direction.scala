package org.finra.datagenerator.scaffolding.operators

import enumeratum._

/**
  * Created by dkopel on 9/16/16.
  */
sealed abstract class Direction(val opposite: Direction=Direction.NONE) extends EnumEntry
object Direction extends Enum[Direction] {
    val values = findValues
    case object NONE extends Direction
    case object BEFORE extends Direction(Direction.AFTER)
    case object AFTER extends Direction(Direction.BEFORE)

}
