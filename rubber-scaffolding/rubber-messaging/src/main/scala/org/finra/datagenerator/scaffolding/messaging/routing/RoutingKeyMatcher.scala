package org.finra.datagenerator.scaffolding.messaging.routing

import java.util.regex.Pattern

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
  * Created by dkopel on 7/12/16.
  */
@ConditionalOnProperty(prefix = "messaging", name = Array("matcher"), havingValue = "routingKey", matchIfMissing = true)
@Component case class RoutingKeyMatcher() extends TopicMatcher {
    final private val MATCH_ONE: Char = '*'
    final private val MATCH_ZERO: Char = '#'
    final private val SEPARATOR: Char = '.'
    final private val logger: Logger = LoggerFactory.getLogger(getClass)

    def matches(consumerTopic: String, producerTopic: String): Boolean = {
        val pt: String = producerTopic.toLowerCase
        var ct: String = consumerTopic.toLowerCase
        if (pt == ct || ct == MATCH_ZERO) {
            return true
        }
        ct = ct.replaceAll("\\" + SEPARATOR, "[\\" + SEPARATOR + "]").replaceAll("\\" + MATCH_ONE, "([^\\" + SEPARATOR + "]+)").replaceAll("^" + MATCH_ZERO + "$|\\[\\" + SEPARATOR + "\\]" + MATCH_ZERO + "|" + MATCH_ZERO + "\\[" + SEPARATOR + "\\]", "(.*)")
        logger.debug("Looking for {} with pattern {} on {}", consumerTopic, ct, producerTopic)
        return Pattern.compile("^" + ct + "$").matcher(pt).matches
    }
}