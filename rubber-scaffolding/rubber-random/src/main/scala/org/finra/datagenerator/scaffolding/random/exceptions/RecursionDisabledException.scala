package org.finra.datagenerator.scaffolding.random.exceptions

/**
  * Created by dkopel on 12/8/16.
  */
case class RecursionDisabledException() extends IllegalArgumentException {
    override def getMessage: String = s"Recursion is disabled"
}
