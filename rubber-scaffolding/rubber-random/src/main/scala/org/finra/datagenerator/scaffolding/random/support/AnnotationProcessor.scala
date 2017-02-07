package org.finra.datagenerator.scaffolding.random.support

import java.lang.reflect.Field

import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.utils.ReflectionUtils
import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.random.predicate.RandomContext
import org.finra.datagenerator.scaffolding.random.support.annotations.RandomConfigAnnotation
import org.finra.datagenerator.scaffolding.utils.ReflectionUtils.AnnotationAssociation

import scala.collection.JavaConverters._

/**
  * Created by dkopel on 1/9/17.
  */
class AnnotationProcessor extends AnnotationUtils {
    override val basePackages: Seq[String] = Seq("org.finra.datagenerator.scaffolding")
    val annotationsSeq: Seq[AnnotationContainer] = scanRandomConfigAnnotations

    type Randomizer = (RandomContext=>_) with Configurable with AnnotationCapable

    case class AnnotationContainer(annotation: Class[_], randomizer: Randomizer)

    case class AnnotationFieldConfig(ref: AnyRef, confs: Seq[Config[_]]) extends FieldReference {
        override def field: Field = ref.asInstanceOf[java.lang.reflect.Field]
    }

    def findAnnotationsConfig(clazz: Class[_]): Map[AnnotationContainer, Seq[AnnotationAssociation[_ <: java.lang.annotation.Annotation]]] = {
        annotationsSeq.map(e => {
            e->ReflectionUtils.findAnnotationsWithAssociation(
                clazz,
                e.annotation.asInstanceOf[Class[_ <: java.lang.annotation.Annotation]]).asScala.toSeq
        }).filter(e => e._2.nonEmpty).toMap
    }

    def filterValues[T, U](randomizer: Randomizer, values: Map[String, AnyRef]) = {
        randomizer.values
            .filter(value => values.keySet.contains(value.name))
            .map(value => {
                val vv = value.asInstanceOf[AnnotationField[T, U]]
                val rawValue: U = values(vv.name).asInstanceOf[U]
                val processedValue: T = vv.convert(rawValue)
                vv.link(processedValue)
            })
    }

    def fieldHasAnnotationConfig(field: Field): Boolean = fieldIsPresent(field, extractAnnotationConfig)

    // Find annotations for the class
    def extractAnnotationConfig(clazz: Class[_]): Seq[AnnotationFieldConfig] = {
        // Annotations
        findAnnotationsConfig(clazz).flatMap(e => {
            logger.debug("Found this clazz: {}", e._1)

            e._2.flatMap(aa => {
                // Right now the only way to grab the values
                // for the annotation is by pulling the fields
                aa.getAssociations.asScala.map(as =>
                    AnnotationFieldConfig(
                        as,
                        filterValues(
                            e._1.randomizer,
                            org.springframework.core.annotation.AnnotationUtils.getAnnotationAttributes(aa.getAnnotation).asScala.toMap
                        ).toSeq
                    )
                )
            })
        }).toSeq
    }

    def getConf(field: Field)(implicit conf: Configuration): LocalConfig = {
        val oConf = getField[AnnotationFieldConfig](field)
        if(oConf.isDefined) LocalConfig(oConf.get.confs)(conf)
        else LocalConfig(Seq.empty)(conf)
    }

    def scanRandomConfigAnnotations: Seq[AnnotationContainer] = {
        var ss = Seq.empty[AnnotationContainer]
        findAnnotation(classOf[RandomConfigAnnotation], ANNOTATION, false).foreach(a => {
            val aa: RandomConfigAnnotation = a.getAnnotation(classOf[RandomConfigAnnotation])
            val randomizer = makeObject(aa.value()).asInstanceOf[Randomizer]
            ss = ss :+ AnnotationContainer(a, randomizer)
            logger.debug("{} -> {}", a.getClass.toString.asInstanceOf[Any], randomizer.getClass)
            randomizer.values.foreach(v => logger.debug("Field: {}", v.name))
        })
        logger.debug("Found annotations: {}", ss)
        ss
    }
}
