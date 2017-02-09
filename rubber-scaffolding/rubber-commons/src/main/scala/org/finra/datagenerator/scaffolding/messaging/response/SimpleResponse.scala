package org.finra.datagenerator.scaffolding.messaging.response

import scala.beans.BeanProperty

/**
  * Created by dkopel on 7/7/16.
  */
case class SimpleResponse[+R](@BeanProperty response: R, priority: Long=0) extends Response[R]