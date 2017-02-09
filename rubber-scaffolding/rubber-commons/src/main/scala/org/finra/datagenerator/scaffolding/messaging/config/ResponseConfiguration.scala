package org.finra.datagenerator.scaffolding.messaging.config

import org.finra.datagenerator.scaffolding.messaging.response.Response
import org.finra.datagenerator.scaffolding.messaging.{MessageContext, Replies}

/**
  * Created by dkopel on 8/22/16.
  */
/**
  * Used to define the configuration of each intention.
  * If `waitForReplies()` is false then the function is fire and forget
  * If the predicate is found to be true then the function continues to block.
  * If there are replies then the `apply()` method is invoked with the collected
  * replies. The return value of the replies is passed to the initial calling `Action`
  *
  */
trait ResponseConfiguration {
    def test(context: MessageContext): Boolean
    def apply(replies: Replies): Response[_]
    def waitForReplies: Boolean

    val body: Any
    var response: Response[_]
}