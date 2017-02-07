package org.finra.datagenerator.scaffolding.config

/**
  * Created by dkopel on 1/12/17.
  */
class ConfigurationProvider(val basePackages: Seq[String], val conf: Option[Configuration]) {

}
object Conf {
    val DEFAULT: Seq[String] = Seq("org.finra.datagenerator.scaffolding")
}
object ConfigurationProvider {
    def apply(basePackages: Seq[String], conf: Option[Configuration]): ConfigurationProvider = {
        new ConfigurationProvider(basePackages, conf)
    }
    def empty: ConfigurationProvider = ConfigurationProvider(Conf.DEFAULT, Option.empty)
}