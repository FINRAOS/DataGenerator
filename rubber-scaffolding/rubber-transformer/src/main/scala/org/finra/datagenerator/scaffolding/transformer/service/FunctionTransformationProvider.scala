package org.finra.datagenerator.scaffolding.transformer.service

import org.finra.datagenerator.scaffolding.transformer.service.TransformationContext
import org.finra.datagenerator.scaffolding.utils.Logging
import org.finra.datagenerator.scaffolding.transformer.function.FunctionTransformation
import org.finra.datagenerator.scaffolding.transformer.service.transformations.TransformationImpl

import scala.collection.JavaConverters._

/**
  * Created by dkopel on 12/16/16.
  */
trait FunctionTransformationProvider extends Logging {
    def hasFunctionTransformation(implicit transformation: TransformationImpl, tContext: TransformationContext): Boolean = {
        transformation.getFunction() != null && transformation.getFunction().getClazz() != classOf[FunctionTransformation[_]] &&
            tContext.hasFunctionTransformation(transformation.getFunction().getClazz())
    }

    def hasFunctionAndKey(transformation: TransformationImpl, ft: FunctionTransformationContainer[_]): Boolean =
        transformation.getFunction != null && transformation.getFunction.getKey.length > 0 &&
            ft.key.equals(transformation.getFunction.getKey) && ft.clazz.equals(transformation.getFunction.getClazz)

    def hasFunctionNullKey(transformation: TransformationImpl, ft: FunctionTransformationContainer[_]): Boolean =
        transformation.getFunction.getKey == null && transformation.getFunction.getClazz.equals(ft.clazz)

    def processFunctionTransformation[S](implicit transformation: TransformationImpl, tContext: TransformationContext): Any = {
        val ft = tContext.functionTransformations.asScala
            .filter(ft => hasFunctionAndKey(transformation, ft) || hasFunctionNullKey(transformation, ft))
            .map(t =>{
                logger.debug("Invoke function transformation {}/{}", t.key.asInstanceOf[Any], t.clazz.getName)
                t.inst.asInstanceOf[FunctionTransformation[S]]
            } ).head

        ft.next(tContext)
    }
}
