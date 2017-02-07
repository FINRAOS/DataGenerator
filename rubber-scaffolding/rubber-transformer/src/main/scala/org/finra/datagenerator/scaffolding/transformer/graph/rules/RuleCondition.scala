package org.finra.datagenerator.scaffolding.transformer.graph.rules

/**
  * Created by dkopel on 11/2/16.
  */
trait RuleCondition {
    /***
      *
      * @param clazz the Class[_] that represents the element in question's class
      * @param otherClass the Class[_] that represents the rule in question's class
      * @param options to be used when evaluating the equality
      * @return Boolean
      */
    protected def eval(clazz: Class[_], otherClass: Class[_])(implicit options: RuleConditionOptions=RuleConditionOptions()): Boolean = {
        if(options.includeSubclasses) {
            otherClass.isAssignableFrom(clazz)
        } else if(options.includeSuperclasses) {
            clazz.isAssignableFrom(otherClass)
        } else if(options.includeSubclasses && options.includeSuperclasses) {
            clazz.isAssignableFrom(otherClass) || otherClass.isAssignableFrom(clazz)
        } else {
            clazz == otherClass
        }
    }
}
