package org.finra.datagenerator.scaffolding.transformer.join

import org.finra.datagenerator.scaffolding.utils.ReflectionUtils

/**
  * Created by dkopel on 12/28/16.
  */
case class JoinImpl(clazz: Class[_], alias: String, fields: Set[JoinFieldImpl])
object JoinImpl {
    type JoinAnnotation = org.finra.datagenerator.scaffolding.transformer.support.Join

    implicit def multipleJoin(joins: Set[JoinAnnotation]): Set[JoinImpl] = joins.map(j => fromAnnotation(j))

    implicit def fromAnnotation(join: JoinAnnotation): JoinImpl = {
        JoinImpl(
            join.value(),
            join.alias(),
            join.fields()
                .map(j => JoinFieldImpl(
                    j.key(),
                    ReflectionUtils.getDeepProperty(join.value(), j.field()), join.value()
                ))
                .toSet
        )
    }
}