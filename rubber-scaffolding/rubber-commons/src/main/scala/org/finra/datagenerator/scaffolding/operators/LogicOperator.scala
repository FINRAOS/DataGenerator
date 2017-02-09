package org.finra.datagenerator.scaffolding.operators

import java.util.List

import enumeratum._


/**
  * Created by dkopel on 25/05/16.
  */
sealed trait LogicOperator extends EnumEntry {
    def eval(booleans: List[Boolean]): Boolean = this match  {
        case LogicOperator.AND => !booleans.contains(false)
        case LogicOperator.OR => booleans.contains(true)
        case LogicOperator.NOT => !booleans.contains(true)
        case LogicOperator.NOR => booleans.contains(false)
        case _ => false
    }
}
object LogicOperator extends Enum[LogicOperator] {
    val AND, OR, NOT, NOR = new LogicOperator {}
    val values = findValues
}