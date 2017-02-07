package org.finra.datagenerator.scaffolding.config

/**
  * Created by dkopel on 12/13/16.
  */
case class ConfigBundle(name: ConfigBundleName, defs: Map[ConfigName, ConfigDefinition[_]]) {
    def apply[T](key: ConfigName): ConfigDefinition[_] = defs(key)
}