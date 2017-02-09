package org.finra.datagenerator.scaffolding.operators

import enumeratum._

/**
  * Created by dkopel on 01/06/16.
  */
sealed abstract class SequentialOperator(val direction: Direction=Direction.NONE, val opposite: SequentialOperator=SequentialOperator.NONE) extends EnumEntry
object SequentialOperator extends Enum[SequentialOperator] {
    val values = findValues
    case object NONE extends SequentialOperator
    case object STARTS extends SequentialOperator(Direction.NONE, SequentialOperator.ENDS)
    case object ENDS extends SequentialOperator(Direction.NONE, SequentialOperator.STARTS)
    case object IMMEDIATELY_BEFORE extends SequentialOperator(Direction.BEFORE, SequentialOperator.IMMEDIATELY_AFTER)
    case object IMMEDIATELY_AFTER extends SequentialOperator(Direction.AFTER, SequentialOperator.IMMEDIATELY_BEFORE)
    case object INDIRECTLY_BEFORE extends SequentialOperator(Direction.BEFORE, SequentialOperator.INDIRECTLY_AFTER)
    case object INDIRECTLY_AFTER extends SequentialOperator(Direction.AFTER, SequentialOperator.INDIRECTLY_BEFORE)
}