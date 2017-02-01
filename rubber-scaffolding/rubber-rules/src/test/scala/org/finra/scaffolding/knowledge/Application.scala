package org.finra.scaffolding.knowledge

import org.finra.scaffolding.knowledge.rule.RuleCompiler
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.context.annotation.{Bean, ComponentScan}

/**
  * Created by dkopel on 01/06/16.
  */
object Application extends App {
    SpringApplication.run(classOf[Configuration])
}

@EnableAutoConfiguration(exclude=Array(
    classOf[DataSourceAutoConfiguration],
    classOf[GroovyTemplateAutoConfiguration]
))
@ComponentScan(Array("org.finra.scaffolding.knowledge.*"))
case class Configuration() {
    @Bean
    def ruleCompiler = RuleCompiler
}
