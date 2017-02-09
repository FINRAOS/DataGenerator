package org.finra.datagenerator.scaffolding.transformer.service

import java.lang.reflect.Field

import org.finra.datagenerator.scaffolding.utils.ReflectionUtils
import org.finra.datagenerator.scaffolding.context.ContextProvider
import org.finra.datagenerator.scaffolding.transformer.service.{TransformationContainer, TransformationSessionType}
import org.finra.datagenerator.scaffolding.transformer.service.transformations.{OrderImpl, TransformationImpl, TransformationsImpl}
import org.finra.datagenerator.scaffolding.transformer.utils.TransformerUtils
import org.finra.datagenerator.scaffolding.utils.ReflectionUtils.AnnotationAssociation

import scala.collection.JavaConverters._

/**
  * Created by dkopel on 12/14/16.
  */
trait TransformationsProvider extends ContextProvider {
    private[service] var registeredTransformations: Map[Field, TransformationsImpl] = Map.empty

    def registerTransformations(values: Seq[TransformationsImpl]) = {
        values.foreach(t => {
            if(!registeredTransformations.contains(t.getField)) registeredTransformations += t.getField->t
            else {
                val tt = registeredTransformations(t.getField)
                var trs = tt.getValue.asScala.toSet
                trs ++ t.getValue.asScala.toSet

                var ors = tt.getOrders.asScala.toSet
                ors ++ t.getOrders.asScala.toSet

                registeredTransformations += t.getField->new TransformationsImpl(t.getField, ors.asJava, trs.asJava)
            }
        })
    }

    def getTransformations(field: Field): Seq[TransformationImpl] = {
        if(registeredTransformations.contains(field)) {
            registeredTransformations(field).getValue.asScala.toList.sorted
        } else {
            Seq.empty
        }
    }

    def getTransformation(field: Field): Option[TransformationImpl] = getTransformations(field).find(t => evaluateCondition(t.getCondition))

    def getFields(fields: Map[Field, Seq[OrderImpl]]): List[Field] = {
        fields.map(fs => {
            fs._1->
                fs._2
                    .filter(o => evaluateCondition(o.getCondition))
                    .minBy(o => o.getOrder)
        }).toList.sortBy(t => t._2).map(t => t._1)
    }

    def getFields(clazz: Class[_]): List[Field] = {
        getFields(TransformationsProvider.getFields(clazz))
    }

    def getFields(container: TransformationContainer[_], sessionType: TransformationSessionType=TransformationsProvider.defaultSessionType): List[Field] = {
        sessionType match {
            case TransformationSessionType.MERGE => {
                val ts: collection.mutable.Map[Field, java.util.Set[OrderImpl]] = TransformerUtils.getFields(container.clazz).asScala
                registeredTransformations.foreach(t => if(ts.contains(t._1)) t._2.getOrders.addAll(ts(t._1)))
                ts.keySet.foreach(t => {
                    if (!registeredTransformations.contains(t)) {
                        val os = TransformerUtils.getFieldOrders(t)
                        val tts = TransformerUtils.getFieldTransformations(t)
                        registeredTransformations += t->new TransformationsImpl(t, os, tts)
                    }
                })
                getFields(container.clazz)
            }
            case TransformationSessionType.REGISTERED_ONLY => getFields(container.clazz)
            case TransformationSessionType.REPLACE => {
                TransformerUtils.getAllTransformations(container.clazz).asScala
                    .foreach(t => registeredTransformations += t.getField-> t)
                getFields(container.clazz)
            }
            case TransformationSessionType.CURRENT_ONLY => getFields(container.clazz)
        }
    }
}
object TransformationsProvider {
    val defaultSessionType: TransformationSessionType = TransformationSessionType.MERGE
    type OrderAnnotation = org.finra.datagenerator.scaffolding.transformer.support.Order

    def getField(target: AnnotationAssociation[_], clazz: Class[_]): Option[Field] = {
        target.getAssociations.asScala
            .filter(as => as.isInstanceOf[java.lang.reflect.Field])
            .map(as => as.asInstanceOf[java.lang.reflect.Field])
            .headOption
    }

    def getFields(clazz: Class[_]): Map[Field, Seq[OrderImpl]] = {
        val allFields = clazz.getDeclaredFields.toSeq
        var foundFields = ReflectionUtils.findAnnotationsWithAssociation(clazz, classOf[OrderAnnotation])
            .asScala.map(t => {
            val o = t.getAnnotation
            getField(t, classOf[OrderAnnotation]).get -> new OrderImpl(o.value(), o.condition())
        }).groupBy(t => t._1).map(t => t._1->t._2.map(tt => tt._2).toSeq)

        allFields.filter(f => !foundFields.contains(f)).foreach(f => foundFields += f->Seq[OrderImpl](new OrderImpl(java.lang.Long.MAX_VALUE)))
        foundFields
    }
}