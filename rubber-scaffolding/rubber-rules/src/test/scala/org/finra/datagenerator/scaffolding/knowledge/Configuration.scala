package org.finra.datagenerator.scaffolding.knowledge

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.context.annotation.{Bean, ComponentScan}

/**
  * Created by dkopel on 2/6/17.
  */
@EnableAutoConfiguration(exclude=Array(
    classOf[DataSourceAutoConfiguration],
    classOf[GroovyTemplateAutoConfiguration]
))
@ComponentScan(Array("org.finra.scaffolding.knowledge.*"))
case class Configuration() {
    @Bean
    def ruleCompiler = RuleCompiler
}
