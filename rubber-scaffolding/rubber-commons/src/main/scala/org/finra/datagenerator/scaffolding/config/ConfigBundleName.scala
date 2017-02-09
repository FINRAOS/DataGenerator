package org.finra.datagenerator.scaffolding.config

/**
  * Created by dkopel on 12/13/16.
  */
case class ConfigBundleName(name: String) {
    override def toString: String = name

    override def equals(obj: scala.Any): Boolean = obj match {
        case a: String => name.equals(a)
        case a: ConfigBundleName => name.equals(a.name)
        case _ => false
    }
}
object ConfigBundleName {
    implicit def clazzName(clazz: Class[_]): ConfigBundleName = ConfigBundleName(clazz.getName)
}
