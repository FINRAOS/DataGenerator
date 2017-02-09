package org.finra.datagenerator.scaffolding.utils

/**
  * Created by dkopel on 11/1/16.
  */
sealed case class ValWrapper[T]() {
    private var _value: T = _
    def apply(value: T) = if(_value == null) _value = value else throw new IllegalAccessException()
}
object ValWrapper {
    implicit def toValue[T](value: ValWrapper[T]): T = value._value
}