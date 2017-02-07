package org.finra.datagenerator.scaffolding.messaging

import scala.beans.BeanProperty

/**
  * Created by dkopel on 6/30/16.
  */
class MessageHeaders(@BeanProperty val topic: Option[String],
                     @BeanProperty val metadata: Map[_ <: Serializable, _ <: Serializable]=Map.empty) extends Serializable
object EmptyMessageHeaders extends MessageHeaders(Option.empty[String], Map.empty)