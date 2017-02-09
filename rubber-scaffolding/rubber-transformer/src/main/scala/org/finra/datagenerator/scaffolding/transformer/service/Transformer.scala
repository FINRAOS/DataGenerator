package org.finra.datagenerator.scaffolding.transformer.service
import java.lang.Boolean
import java.lang.reflect.Method
import java.util
import java.util.function.Function

import org.finra.datagenerator.scaffolding.utils.Mapper
import org.finra.datagenerator.scaffolding.config.{Configuration, ConfigurationProvider}
import org.finra.datagenerator.scaffolding.random.core.{RubberRandom, RubberRandomImpl}
import org.finra.datagenerator.scaffolding.transformer.service.transformations.TransformationsImpl

import scala.collection.JavaConverters._

/**
  * Created by dkopel on 12/14/16.
  */
class Transformer(provider: ConfigurationProvider=ConfigurationProvider.empty) extends Configuration(provider)
    with TransformationSession
    with TransformerService
    with JoinService {
    override val random: RubberRandom = new RubberRandomImpl(conf=Option(this))

    override def registerTransformations(transformations: util.Set[TransformationsImpl]) = {
        registerTransformations(transformations.asScala.toSeq)
    }

    override def registerContextFunction(methodName: String, method: Method) = registerFunction(methodName, method)


    override def filterData[T](
                                  data: util.Collection[T],
                                  filter: Function[T, Boolean]
                              ): util.Collection[T] = {
        data.asScala.filter(d => filter(d)).asJavaCollection
    }

    override def transform(
                              container: util.List[TransformationContainer[_]],
                              sessionType: TransformationSessionType
                          ): util.List[TransformationContainer[_]] = {
        val m = collection.mutable.Map(new java.lang.Long(0)->container.asInstanceOf[util.Collection[TransformationContainer[_]]]).asJava
        transform(new TransformationContext(0L, m, new util.HashSet()))
    }

    override def transform(tContext: TransformationContext): util.List[TransformationContainer[_]] = {
        setContext(tContext)
        tContext.getIterationOutputs
            .asScala
            .map(c => processOutputClass(c)(this))
            .asJava
    }

    override def setGlobals(value: util.Map[String, AnyRef]) {
        setGlobals(value)
    }

    override def toString(obj: Object): String = Mapper.toString(obj)
}
object Transformer {
    def apply: Transformer = new Transformer(ConfigurationProvider.empty)
}