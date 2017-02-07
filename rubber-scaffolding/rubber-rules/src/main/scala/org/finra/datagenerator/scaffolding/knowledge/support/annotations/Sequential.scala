package org.finra.datagenerator.scaffolding.knowledge.support.annotations

import java.lang.annotation.{ElementType, Retention, RetentionPolicy, Target}

import org.finra.datagenerator.scaffolding.knowledge.dependency.sequential.SequentialDataProvider
import org.finra.datagenerator.scaffolding.utils.ClassUtils

import scala.annotation.StaticAnnotation

/**
  * Created by dkopel on 6/20/16.
  */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = Array(ElementType.FIELD)) object Sequential {

    object Helper {
        @throws[Exception]
        def getDataProvider[T <: SequentialDataProvider[_]](sequential: Sequential): T = {
            var args: Array[String] = null
            var classes: Array[Class[_]] = null
            val arguments: Array[SequentialDataArgument] = sequential.arguments
            if (arguments.length == 0) {
                return sequential.`type`.newInstance.asInstanceOf[T]
            }
            else {
                args = new Array[String](arguments.length)
                classes = new Array[Class[_]](arguments.length)
                var x: Int = 0
                while (x < arguments.length) {
                    {
                        args(x) = arguments(x).value
                        classes(x) = arguments(x).`type`
                    }
                    {
                        x += 1; x - 1
                    }
                }
                return ClassUtils.createNewInstance(sequential.`type`.asInstanceOf[Class[T]], args.asInstanceOf[Array[AnyRef]], classes)
            }
        }
    }

}

@Retention(RetentionPolicy.RUNTIME)
@Target(value = Array(ElementType.FIELD)) trait Sequential extends StaticAnnotation {
    def key: String

    def `type`: Class[_ <: SequentialDataProvider[_]]

    def arguments: Array[SequentialDataArgument]
}