package org.finra.datagenerator.scaffolding.config

/**
  * Created by dkopel on 12/7/16.
  */
case class ConfigName(name: String) {
    override def toString: String = name

    override def equals(obj: scala.Any): Boolean = obj match {
        case a: String => name.equals(a)
        case a: ConfigName => name.equals(a.name)
        case _ => false
    }
}