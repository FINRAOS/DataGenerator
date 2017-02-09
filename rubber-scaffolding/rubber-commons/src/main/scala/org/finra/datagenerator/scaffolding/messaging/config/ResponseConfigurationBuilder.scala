package org.finra.datagenerator.scaffolding.messaging.config

import org.finra.datagenerator.scaffolding.messaging.response.Response
import org.finra.datagenerator.scaffolding.messaging.{MessageContext, Replies}

/**
  * Created by dkopel on 9/8/16.
  */
abstract class ResponseConfigurationBuilder(
                                                                                     val _waitForReplies: Boolean = true,
                                                                                     val _handler: Function[Replies, Response[_]],
                                                                                     val _predicate: ((MessageContext) => Boolean)) {

    def build(_body: Any) = {
        new ResponseConfiguration() {

            override def waitForReplies: Boolean = _waitForReplies

            override def test(ctx: MessageContext): Boolean = _predicate.apply(ctx)

            override def apply(replies: Replies): Response[_] = {
                if(_handler != null) {
                    response = _handler.apply(replies)
                    response
                } else {
                    null
                }
            }

            override var response: Response[_] = _
            override val body: Any = _body
        }
    }
}
