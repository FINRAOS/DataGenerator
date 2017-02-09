package org.finra.datagenerator.scaffolding.messaging.config

import java.time.Duration

import org.finra.datagenerator.scaffolding.messaging.Replies
import org.finra.datagenerator.scaffolding.messaging.response.Response

/**
  * Created by dkopel on 9/8/16.
  */
case class MaxDurationResponse(
                                                                        override val _handler: Function[Replies, Response[_]],
                                                                        private val maxDuration: Duration
                                                            ) extends ResponseConfigurationBuilder(true, _handler, (t) => t.getElapsed >= maxDuration.toMillis)
