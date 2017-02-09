package org.finra.datagenerator.scaffolding.messaging.config

import org.finra.datagenerator.scaffolding.messaging.Replies
import org.finra.datagenerator.scaffolding.messaging.response.Response

/**
  * Created by dkopel on 9/8/16.
  */
case class MinCountResponseConfiguration(
                                                                override val _handler: Function[Replies, Response[_]],
                                                                private val minReplies: Int
                                                            ) extends ResponseConfigurationBuilder(true, _handler, (t) => t.getRepliedCount >= minReplies)
