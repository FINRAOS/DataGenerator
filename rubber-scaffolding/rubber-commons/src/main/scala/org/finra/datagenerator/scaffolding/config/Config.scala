package org.finra.datagenerator.scaffolding.config

case class Config[+T](conf: ConfigDefinition[T], value: Option[T]) extends Comparable[Config[_]] {
    def isNotDefault = value.isEmpty

    def getValue(): T = {
        if(value.isDefined) value.get
        else if(conf.default.isDefined) conf.default.get
        else throw new NoSuchElementException()
    }

    override def compareTo(o: Config[_]): Int = conf.priority.compare(o.conf.priority)
}