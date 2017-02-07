package org.finra.datagenerator.scaffolding.operators

/**
  * Created by dkopel on 9/16/16.
  */
object Relationship extends Enumeration {
    val IMMEDIATELY, INDIRECTLY = Value
}
sealed case class Relationship(rel: Relationship) {
    lazy val opposite = rel match {
        case Relationship.IMMEDIATELY => Relationship.INDIRECTLY
        case Relationship.INDIRECTLY => Relationship.IMMEDIATELY
    }
}