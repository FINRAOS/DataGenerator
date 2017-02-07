package org.finra.datagenerator.scaffolding.transformer.graph.rules

/**
  * Created by dkopel on 11/2/16.
  */
case class PropertyRuleTarget[U <: AnyVal](property: String) extends RuleTarget[Map[String, _], U] {
    // Instance of object to apply action to
    override def set[Z >: U](inst: Map[String, _], fieldValue: Z): Map[String, _] = inst + (property->fieldValue)
    override def get(inst: Map[String, _]): U = inst(property).asInstanceOf[U]

    override val propertyName: String = property
}
