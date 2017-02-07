package org.finra.datagenerator.scaffolding.messaging

import java.util.UUID

import org.finra.datagenerator.scaffolding.messaging.response.Response

import scala.beans.BeanProperty

/**
 * Created by dkopel on 6/30/16.
 */
trait MessageHandler extends Ordered[MessageHandler] with Serializable {
    def apply[T <: java.io.Serializable](message: Message[T]): Response[_]
    @BeanProperty val id = UUID.randomUUID()
    var parentId: UUID = _
    val priority: Long

    override def compare(that: MessageHandler): Int = priority.compareTo(that.priority)
}

