package org.finra.datagenerator.scaffolding.transformer.service

import java.lang.reflect.Field

import org.finra.datagenerator.scaffolding.config.{ConfigDefinition, ConfigName, Configurable, Configuration}
import org.finra.datagenerator.scaffolding.utils.{Mapper, ReflectionUtils, TypeUtils}
import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.exceptions.SpelContextException
import org.finra.datagenerator.scaffolding.random.core.RubberRandom
import org.finra.datagenerator.scaffolding.transformer.service.{OutputTransformationContainer, TransformationContainer, TransformationContext}
import org.finra.datagenerator.scaffolding.transformer.service.TransformationSession.{AbortOnSpelExceptionsName, AddTransformationContainerToContextName, DefaultFieldGenerateName}
import org.finra.datagenerator.scaffolding.transformer.service.transformations.TransformationImpl
import org.finra.datagenerator.scaffolding.utils.{Mapper, TypeUtils}

import scala.reflect.runtime.universe._
/**
  * Created by dkopel on 12/14/16.
  */
trait TransformationSession extends TransformationContextProvider
    with TransformationsProvider
    with FunctionTransformationProvider {
    implicit var tContext: TransformationContext = _
    val random: RubberRandom

    def setContext(tcxt: TransformationContext) = {
        tContext = tcxt
        updateContext(tContext)
    }

    def processOverride[S](): S = {
        val oo = tContext.getCurrentOutputOverride
        if(oo.isPresent) oo.get().action.get().asInstanceOf[S]
        else null.asInstanceOf[S]
    }

    def processValue[S](field: Field)(implicit transformation: TransformationImpl, tt: TypeTag[S]): S = {
        // Overrides
        if(tContext.hasOverrides) {
            processOverride()
        }
        // isNull
        else if(transformation.isNull) {
            null.asInstanceOf[S]
        }
        // isEmptyString
        else if(transformation.isEmptyString) {
            "".asInstanceOf[S]
        }
        // FunctionTransformation
        else if(hasFunctionTransformation) {
            processFunctionTransformation[S](transformation, tContext).asInstanceOf[S]
        }
        // Expression Value
        else if(transformation.getValue != null && transformation.getValue.length > 0) {
            parseExpression[S](transformation.getValue)
        }
        // Nothing
        else {
            throw new IllegalStateException()
        }
    }

    def getRandom[T](container: TransformationContainer[_]): T = {
        implicit val tt: TypeTag[T] = TypeUtils.stringToTypeTag(container.clazz.getName)
        implicit val _random = random
        _random.generate[T]
        //new RubberRandom().nextObject(container.clazz).asInstanceOf[T]
    }

    def checkLimits[T](field: Field, value: T)(implicit transformation: TransformationImpl) = {
        if(value != null && transformation.getLimits.length > 0) {
            for(l <- transformation.getLimits[T]) {
                if (!l.isValid(value)) {
                    throw new IllegalArgumentException()
                }
            }
        }
    }

    def setField[T, S](field: Field, inst: T, value: S): T = {
        logger.debug(s"Setting the field ${field.getName} to the value $value")
        try {
            ReflectionUtils.setField(field, inst, value)
        } catch {
            case e: IllegalArgumentException => logger.error(e.getMessage)
        }

        inst
    }

    def getInitialValue[T](container: TransformationContainer[T]): T = {
        if (container.value != null) {
            container.value
        } else {
            container.clazz.newInstance()
        }
    }

    def setField[T](field: Field, inst: T)(implicit conf: Configuration): T = {
        conf.conf[DefaultFieldGenerateStrategy](DefaultFieldGenerateName).getValue()(field, inst)(random)
    }

    def processOutputClass[T, S](container: TransformationContainer[T])(implicit conf: Configuration): TransformationContainer[_] = {
        val output: T = getInitialValue(container)
        logger.debug(s"Here is the initialized value for the class ${container.clazz}: {}", Mapper.toString(output))
        setRoot(output)
        tContext.setCurrentClass(container.clazz)
        tContext.setCurrentInstance(output)

        val fields = getFields(container)
        logger.debug(s"Found ${fields.size} for class ${container.clazz.getName}")

        fields.foreach(f => {
            logger.debug(s"Processing class ${container.clazz.getName} field ${f.getName}")
            tContext.field = f
            val transformation = getTransformation(f)
            implicit val tt = TypeUtils.stringToTypeTag(f.getType.getName)
            var value: S = null.asInstanceOf[S]
            // Has Override
            if(tContext.hasOverrides) {
                value = processOverride[S]()
                if(transformation.isDefined) {
                    implicit val actualTransformation = transformation.get
                    checkLimits(f, value)
                }
                setField(f, output, value)
            }
            // No override...has a transformation
            else if(transformation.isDefined) {
                implicit val actualTransformation = transformation.get
                try {
                    value = processValue(f)
                    checkLimits(f, value)
                    setField(f, output, value)
                } catch {
                    case e: SpelContextException => {
                        logger.error("An error occurred in the Spel context: {}", e.message)
                        if(conf.conf(AbortOnSpelExceptionsName).getValue()) throw e
                    }
                    case e: IllegalStateException => {
                        logger.warn("A transformation was not executed on the field {}, using random data", f.getName)
                        setField(f, output)
                    }
                }
            } else {
                logger.warn(s"No valid transformations were found on the field ${f.getName}")
                setField(f, output)
            }
        })

        val nc = new OutputTransformationContainer[T](container.alias, container.clazz, output, container.order, container.join)

        if(conf.conf[Boolean](AddTransformationContainerToContextName).getValue()) {
            registerVariable(container.alias, output)
            tContext.addTransformationContainer(nc)
        }

        logger.debug(s"Transformation completed for ${tContext.iteration} ${container.alias}/${container.clazz}: {}", Mapper.toString(output))
        nc
    }
}

trait DefaultFieldGenerateStrategy {
    def apply[T](field: Field, inst: T)(implicit rubberRandom: RubberRandom): T
}

object RandomFieldGenerate extends DefaultFieldGenerateStrategy {
    override def apply[T](field: Field, inst: T)(implicit rubberRandom: RubberRandom): T = {
        ReflectionUtils.setField(field, inst, rubberRandom.generate(field))
        inst
    }
}

object TransformationSession extends Configurable {
    object DefaultFieldGenerateName extends ConfigName("defaultFieldGenerateStrategy")
    object AddTransformationContainerToContextName extends ConfigName("addTransformationContainerToContext")
    object AbortOnSpelExceptionsName extends ConfigName("abortOnSpelExceptions")


    val DefaultFieldStrategy: ConfigDefinition[DefaultFieldGenerateStrategy] = ConfigDefinition[DefaultFieldGenerateStrategy](
        DefaultFieldGenerateName,
        Some(RandomFieldGenerate)
    )

    val AddTransformationContainerToContext: ConfigDefinition[Boolean] = ConfigDefinition[Boolean](
        AddTransformationContainerToContextName,
        Some(true)
    )

    val AbortOnSpelExceptions: ConfigDefinition[Boolean] = ConfigDefinition[Boolean](
        AbortOnSpelExceptionsName,
        Some(false)
    )

    override def configBundle: ConfigBundle = {
        ConfigBundle(
            getClass,
            Seq(
                DefaultFieldStrategy,
                AddTransformationContainerToContext,
                AbortOnSpelExceptions
            )
        )
    }
}
