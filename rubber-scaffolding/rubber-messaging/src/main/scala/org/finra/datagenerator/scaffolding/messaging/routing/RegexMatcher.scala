package org.finra.datagenerator.scaffolding.messaging.routing

import java.util.regex.Pattern

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
  * Created by dkopel on 7/12/16.
  */
@Component
@ConditionalOnProperty(prefix = "messaging", name = Array("matcher"), havingValue = "regex")
case class RegexMatcher() extends TopicMatcher {
    def matches(pattern: String, producerTopic: String): Boolean = {
        return Pattern.compile(pattern).matcher(producerTopic).matches
    }
}