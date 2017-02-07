package org.finra.datagenerator.scaffolding.messaging

import org.finra.datagenerator.scaffolding.messaging.routing.{AntMatcher, RegexMatcher, RoutingKeyMatcher, TopicMatcher}
import org.junit.{Assert, Test}

/**
  * Created by dkopel on 6/30/16.
  */
class TopicMatcherTests {
    private def routingKey: TopicMatcher = RoutingKeyMatcher()

    private def regex: TopicMatcher = RegexMatcher()

    private def ant: TopicMatcher = AntMatcher()

    @Test def simpleMatchesTest() {
        Assert.assertFalse(routingKey.matches("test", "T3ST"))
        Assert.assertTrue(routingKey.matches("test", "TEST"))
    }

    @Test def matchOneTest() {
        Assert.assertTrue(routingKey.matches("a.*", "a.b"))
        Assert.assertTrue(routingKey.matches("*.a", "z.a"))
        Assert.assertTrue(routingKey.matches("*.a.*", "z.a.b"))
        Assert.assertTrue(routingKey.matches("*.a.*.*", "z.a.b.d"))
        Assert.assertTrue(routingKey.matches("*.a.*.d.*", "z.a.b.d.e"))
        Assert.assertFalse(routingKey.matches("a.*", "z.a.b"))
        Assert.assertFalse(routingKey.matches("*.a", "z.a.b"))
    }

    @Test def matchZeroTest() {
        Assert.assertTrue(routingKey.matches("#", "a.b"))
        Assert.assertTrue(routingKey.matches("a.#", "a.b.c"))
        Assert.assertTrue(routingKey.matches("a.#.z", "a.b.c.d.e.f.z"))
        Assert.assertTrue(routingKey.matches("#.z", "a.b.c.d.e.f.z"))
    }

    @Test def regexTest() {
        Assert.assertTrue(regex.matches("[abcdef]+\\d{2,}", "ab13"))
        Assert.assertFalse(regex.matches("[abcdef]+\\d{2,}", "ab1"))
    }

    @Test def antTest() {
        Assert.assertTrue(ant.matches("a/b/*/d/**", "a/b/c/d/e/f"))
        Assert.assertFalse(ant.matches("a/b/*/d/**", "a/b/c/r/d/e/f"))
    }
}