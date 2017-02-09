package org.finra.datagenerator.scaffolding.config

import org.finra.datagenerator.scaffolding.utils.Logging

/**
  * Created by dkopel on 12/7/16.
  */
abstract class Configuration(val provider: ConfigurationProvider) extends Logging {
    private[this] val conf: Option[Configuration] = provider.conf

    private[this] var basePackages: Seq[String] = provider.basePackages

    private[this] val overrides: collection.mutable.Map[ConfigName, Any] = collection.mutable.Map.empty[ConfigName, Any]

    private[this] var bundles: Map[ConfigBundleName, ConfigBundle] = {
        if(conf.isDefined) conf.get.getBundles
        else ConfigurationUtils.findConfigurableBundles(basePackages)
    }

    def getBundles: Map[ConfigBundleName, ConfigBundle] = bundles

    def setBasePackages(pkgs: Seq[String]) = basePackages = pkgs

    def getBasePackages: Seq[String] = basePackages

    def getOverride(name: ConfigName): Option[Any] = overrides.get(name)

    def setOverride(name: ConfigName, value: Any) = overrides.put(name, value)

    def clearOverride(name: ConfigName) = overrides.remove(name)

    def confs(name: Option[ConfigName]=Option.empty): List[Config[_]] = {
        flattenBundles.filter(c => {
            if(name.isEmpty) true
            else if(name.isDefined) c.name.equals(name.get)
            else {
                false
            }
        })
            .map(c => ConfigurationUtils.getConfig(c, getOverride(c.name)))
            .toList
            .sorted
    }

    def configBundle(bundle: ConfigBundleName): ConfigBundle = bundles(bundle)

    def addBundle(bundle: ConfigBundle) = bundles += bundle.name->bundle

    def addBundles(bundles: Seq[ConfigBundle]) = this.bundles ++= bundles.map(b => b.name->b)

    def flattenBundles: Seq[ConfigDefinition[_]] = bundles.flatten(t => t._2.defs.values).toSeq

    def confValue(name: ConfigName): Any = confs(Option(name)).map(c => c.getValue()).head

    def conf[T](name: ConfigName): Config[T] = {
        val c = confs(Option(name))

        if(c.size == 0) throw new IllegalArgumentException("No configuration is found for "+name)

        c.head.asInstanceOf[Config[T]]
    }
}
object Configuration {
    def empty: Configuration = new Configuration(ConfigurationProvider.empty) {}
}