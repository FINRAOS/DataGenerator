package org.finra.datagenerator.scaffolding.config

import scala.reflect.runtime.universe._

/**
  *
  * @param name Configuration name
  * @param allowedValues possible valid values, if empty all values
  * @tparam T
  */
case class ConfigDefinition[+T](
                                   name: ConfigName,
                                   default: Option[_ <: T]=Option.empty,
                                   allowedValues: Option[Seq[_ <: T]]=Option.empty,
                                   priority: Long=Long.MaxValue
                           )(implicit ev: TypeTag[T]) extends Comparable[ConfigDefinition[_]] {
    override def compareTo(o: ConfigDefinition[_]): Int = priority.compare(o.priority)

    def tpe[S]: S = ev.asInstanceOf[S]

    def apply[S >: T](value: S): Config[S]  = apply(Some(value))

    def apply[S >: T](value: Option[S]): Config[S] = {
        if(value.isDefined) {
            if(allowedValues.isDefined) {
                val allowed = allowedValues.get
                if(allowed.nonEmpty && !allowed.contains(value.get))
                    throw new IllegalArgumentException(s"The value ${value.get} is not one of the valid allowed values for this configuration!")
            }
        }
        Config(this, value)
    }

    override def equals(obj: scala.Any): Boolean = obj match {
        case cd: ConfigDefinition[_] => name.equals(cd.name)
        case _ => false
    }
}
object ConfigDefinition {
    implicit def convertDefs(defs: Seq[ConfigDefinition[_]]): Map[ConfigName, ConfigDefinition[_]] = defs.map(d => (d.name, d)).toMap
}
