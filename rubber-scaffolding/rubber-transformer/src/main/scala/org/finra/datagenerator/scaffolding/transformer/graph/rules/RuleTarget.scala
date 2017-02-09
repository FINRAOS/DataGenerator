package org.finra.datagenerator.scaffolding.transformer.graph.rules

/**
  * Created by dkopel on 10/27/16.
  */


/*
- Condition is the class of the vertex or edge as well as an optional condition
- Rule Applier is the change that is applied to the object in question as a result of a truthful conditional evaluation
- Rule Target is the aspect of the target object that the rule applier is applied to
 */

trait RuleTarget[T, +U] {
    val propertyName: String
    // Instance of object to apply action to
    def set[Z >: U](inst: T, fieldValue: Z): T
    def get(inst: T): U
}
