package org.finra.datagenerator.scaffolding.random.strategy

import java.lang.reflect.{Field, ParameterizedType, TypeVariable}

import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.utils.{ClassUtils, ReflectionUtils}
import org.finra.datagenerator.scaffolding.random.Parameter
import org.finra.datagenerator.scaffolding.random.core.RubberRandom
import org.finra.datagenerator.scaffolding.random.core.RubberRandom.MaxRecursionCountName
import org.finra.datagenerator.scaffolding.random.exceptions.{MaxRecursiveException, NotGenerationException}
import org.finra.datagenerator.scaffolding.random.support.{AnnotationProcessor, CustomRandomizerProcessor}
import org.finra.datagenerator.scaffolding.random.types.{CollectionTypes, TypeContainer}
import org.finra.datagenerator.scaffolding.utils.ClassUtils

/**
  * Created by dkopel on 12/8/16.
  */
case class FieldGeneratingStrategy(rubberRandom: RubberRandom, custom: CustomRandomizerProcessor, annotationProcessor: AnnotationProcessor) extends GeneratingStrategy {
    override def apply[T](typeContainer: TypeContainer[T], args: Seq[Parameter[_]])(implicit conf: Configuration): T = {
        val inst = instantiate(typeContainer, args)
        typeContainer.clazz.getDeclaredFields.foreach(f => {
            if(typeContainer.stack.canAdd(f.getType)) {
                try {
                    // If there a config here we will want to fork the configuration and extend it
                    // we don't want to override the rest of the config
                    // Ideally...this should work with priorities. Like the randomizer. A config value
                    // should have a priority number. A default value, an annotation configured value,
                    // an explicitly specified value without a priority, and an explicitly specified value
                    // with a priority
                    val value = if(custom.fieldHasCustomRandomizer(f)) {
                        custom.registerCustomRandomizer(typeContainer, f)(rubberRandom)
                        generateField(f, typeContainer)
                    } else if(annotationProcessor.fieldHasAnnotationConfig(f)) {
                        logger.debug("There is an annotation config for the field {}", f)
                        generateField(f, typeContainer)(annotationProcessor.getConf(f))
                    } else {
                        generateField(f, typeContainer)
                    }

                    logger.debug("Value: {}", value)
                    ReflectionUtils.setField(f, inst, value)
                } catch {
                    case e: NotGenerationException => {
                        logger.warn("Could not add the field {}", f.getName)
                        if(typeContainer.stackSize > conf.conf[Int](MaxRecursionCountName).getValue()) throw e
                        else inst
                    }
                }
            } else {
                logger.warn("Could not add the field {}", f.getName)
                throw new NotGenerationException
            }
        })
        inst.asInstanceOf[T]
    }

    def instantiate[T](typeContainer: TypeContainer[T], args: Seq[Parameter[_]]): T = {
        if(args.nonEmpty) {
            val ps = Parameter(args)
            ClassUtils.createNewInstance(typeContainer.clazz, ps._1.asInstanceOf[Array[AnyRef]], ps._2)
        }
        else typeContainer.clazz.newInstance()
    }

    def generateField[T](field: Field, typeContainer: TypeContainer[T])(implicit configuration: Configuration): T = {
        try {
            val g = field match {
                // Array
                case f if f.getType.isArray => TypeContainer(f.getType, Option(typeContainer))
                // Collection
                case f if CollectionTypes.isCollection(f.getType) => TypeContainer(field.getGenericType, Option(typeContainer))
                case _ => field.getGenericType match {
                    case tv: TypeVariable[_] =>
                        logger.debug("Type variable present {} on field {}", tv.getName, field.getName.asInstanceOf[Any])
                        // Look for type variable
                        val tcs = typeContainer.stack.get.filter(s => {
                            s.isInstanceOf[TypeContainer[T]] &&
                                s.asInstanceOf[TypeContainer[T]].clazz.equals(tv.getGenericDeclaration)
                        })
                        tcs.head.asInstanceOf[TypeContainer[T]].types.head
                    case pt: ParameterizedType => TypeContainer(field.getGenericType, Option(typeContainer))
                    case _ => TypeContainer(field.getType, Option(typeContainer))
                }
            }
            rubberRandom.generateTypeContainer(g)
        } catch {
            case e: NotGenerationException => throw e
            case e: MaxRecursiveException => throw new NotGenerationException
        }
    }
}
