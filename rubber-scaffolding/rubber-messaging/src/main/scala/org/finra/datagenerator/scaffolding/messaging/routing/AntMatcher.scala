package org.finra.datagenerator.scaffolding.messaging.routing

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher

/**
  * Created by dkopel on 7/12/16.
  */
@ConditionalOnProperty(prefix = "messaging", name = Array("matcher"), havingValue = "ant")
@Component
case class AntMatcher() extends TopicMatcher {
    final private val SEPARATOR: String = "/"

    def matches(pattern: String, producerTopic: String): Boolean = {
        return new AntPathMatcher(SEPARATOR).`match`(pattern, producerTopic)
    }
}