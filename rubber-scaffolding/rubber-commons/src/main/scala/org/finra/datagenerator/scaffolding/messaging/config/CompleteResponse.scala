package org.finra.datagenerator.scaffolding.messaging.config

import org.finra.datagenerator.scaffolding.messaging.Replies
import org.finra.datagenerator.scaffolding.messaging.response.Response

/**
  * Created by dkopel on 9/8/16.
  */
case class CompleteResponse(override val _handler: Function[Replies, Response[_]]
                                                         ) extends ResponseConfigurationBuilder(true, _handler,
    t => t.getRepliedCount.equals(t.getSubscribedCount))
