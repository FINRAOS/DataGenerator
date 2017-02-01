package org.finra.scaffolding.messaging

import org.finra.scaffolding.messaging.config.ResponseConfiguration
import org.finra.scaffolding.messaging.response.Response

import scala.concurrent.Future


/**
  * Created by dkopel on 9/8/16.
  */
class MessageContextImpl(
                                                                  override val message: Message[_],
                                                                  override val configuration: ResponseConfiguration,
                                                                  private val count: Long,
                                                                  private val accumulator: () => List[Response[_]],
                                                                  private val futures: List[Future[_]])
    extends MessageContext {
    val started = System.currentTimeMillis()

    override def getStartedAt: Long = started

    override def getElapsed: Long = System.currentTimeMillis() - started

    override def getSubscribedCount: Long = count

    private def getReplies = futures.filter(fc => fc.isCompleted)

    override def getRepliedCount: Long = getReplies.size

    override def getAccumulator: () => List[Response[_]] = accumulator

    override def getResponses: List[Response[_]] = accumulator.apply()
}
