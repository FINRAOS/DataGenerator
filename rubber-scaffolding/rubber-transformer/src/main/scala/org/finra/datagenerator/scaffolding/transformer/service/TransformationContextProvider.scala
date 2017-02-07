package org.finra.datagenerator.scaffolding.transformer.service

import java.lang.reflect.Method

import org.finra.datagenerator.scaffolding.context.ContextProvider
import org.finra.datagenerator.scaffolding.transformer.service.{InputTransformationContainer, TransformationContext}

import scala.collection.JavaConverters._

/**
  * Created by dkopel on 12/14/16.
  */
trait TransformationContextProvider extends ContextProvider {
    private val contextMethods: collection.mutable.Map[String, Method] = collection.mutable.Map.empty
    contextMethods ++= DefaultContextMethods.methods
    contextMethods.foreach(t => registerFunction(t._1, t._2))

    def updateContext(tContext: TransformationContext): Unit = {
        tContext.getCurrentIteration.asScala.foreach(c => {
            if(c.isInstanceOf[InputTransformationContainer[_]] && c.value == null) {
                logger.error(s"The input container for the class ${c.clazz} has a null value!")
            }
            registerVariable(c.alias, c.value)
        })

        registerVariables(
            Map(
                "context"->tContext,
                "globals"->getGlobals,
                "iteration"->tContext.getIteration
            )
        )
    }
}