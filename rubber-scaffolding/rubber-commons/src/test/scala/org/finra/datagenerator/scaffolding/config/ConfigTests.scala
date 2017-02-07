package org.finra.datagenerator.scaffolding.config

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.{Bean, ComponentScan}
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
  * Created by dkopel on 12/7/16.
  */
@EnableAutoConfiguration
@ComponentScan(Array("org.finra.datagenerator.scaffolding.config.*"))
case class TestConfiguration() {
    @Bean
    def conf = ConfigurationUtils
}

@RunWith(classOf[SpringJUnit4ClassRunner])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringApplicationConfiguration(classes = Array(classOf[TestConfiguration]))
class ConfigTests {

    @Test
    def test1(): Unit = {
        val cxt = ConfigurationUtils.applicationContext
        System.out.println(cxt.getEnvironment.getProperty("maxRecursions"))
    }

}
