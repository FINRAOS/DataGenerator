package org.finra.datagenerator.scaffolding.messaging.routing

/**
  * Created by dkopel on 6/30/16.
  */
trait TopicMatcher extends Serializable {
    def matches(consumerTopic: String, producerTopic: String): Boolean
}