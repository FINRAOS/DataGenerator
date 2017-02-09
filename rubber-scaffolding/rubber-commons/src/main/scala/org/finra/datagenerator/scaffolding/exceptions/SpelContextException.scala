package org.finra.datagenerator.scaffolding.exceptions

/**
  * Created by dkopel on 12/19/16.
  */
case class SpelContextException(message: String, org: Option[Exception]=Option.empty) extends RuntimeException
