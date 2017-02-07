package org.finra.datagenerator.scaffolding.knowledge.context

/**
  * Created by dkopel on 10/10/16.
  */
case class Variable[T](private var _value: Some[T]=null, name: Option[String]=Option.empty) {
    def value(implicit kc: KnowledgeContext): T = {
        if(_value.isDefined) _value.get
        else if(name.isDefined && kc.data.contains(name.get)) kc.data(name.get).asInstanceOf[T]
        else throw new IllegalArgumentException
    }
}

object Variable {
    implicit def apply(name: String): Variable[Any] = new Variable(null, Option(name))
    implicit def apply(value: Some[Any]): Variable[Any] = new Variable(Some(value))
    implicit def apply(value: Any): Variable[Any] = if(value.isInstanceOf[Variable[_]]) value.asInstanceOf[Variable[Any]] else new Variable(Some(value))
    implicit def apply(values: Seq[Any]): Seq[Variable[Any]] = values.map(v => Variable(v))
}