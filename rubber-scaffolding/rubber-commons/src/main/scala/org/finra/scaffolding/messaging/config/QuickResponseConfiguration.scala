package org.finra.scaffolding.messaging.config

import org.finra.scaffolding.messaging.Replies
import org.finra.scaffolding.messaging.response.Response

/**
  * Created by dkopel on 9/8/16.
  */
case class QuickResponseConfiguration(
                                                             override val _handler: Function[Replies, Response[_]]
                                                         ) extends ResponseConfigurationBuilder(false, _handler, null)
