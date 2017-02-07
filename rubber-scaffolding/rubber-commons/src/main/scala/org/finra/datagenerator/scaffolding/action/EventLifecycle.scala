package org.finra.datagenerator.scaffolding.action

import enumeratum._


/**
  * Created by dkopel on 6/30/16.
  */
sealed trait EventLifecycle extends EnumEntry
object EventLifecycle extends Enum[EventLifecycle] {
    val NONE, ALL, BEFORE_INVOCATION, AFTER_INVOCATION, ERROR = new EventLifecycle {}
    val values = findValues
}