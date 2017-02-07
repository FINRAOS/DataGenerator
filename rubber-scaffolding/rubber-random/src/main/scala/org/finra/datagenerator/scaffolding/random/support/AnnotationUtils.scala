package org.finra.datagenerator.scaffolding.random.support

import java.lang.reflect.{Field, Modifier}

import org.finra.datagenerator.scaffolding.utils.{Logging, SimpleClassPathScanner}
import org.finra.datagenerator.scaffolding.utils.SimpleClassPathScanner
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.core.`type`.filter.{AnnotationTypeFilter, AssignableTypeFilter}

import scala.collection.JavaConverters._
import scala.reflect.runtime.universe._

/**
  * Created by dkopel on 1/9/17.
  */
trait AnnotationUtils extends Logging {
    val basePackages: Seq[String]

    private[this] var knownNot = Seq.empty[Any]
    private[this] var known = Seq.empty[Any]
    private[this] var fields: Seq[FieldReference] = Seq.empty[FieldReference]

    private val mirror = runtimeMirror(getClass.getClassLoader)

    def makeObject[T](className: String): T = {
        Class.forName(className).newInstance().asInstanceOf[T]
        //mirror.reflectModule(mirror.staticModule(className)).instance.asInstanceOf[T]
    }

    def makeObject[T](clazz: Class[T]): T = {
        clazz.newInstance().asInstanceOf[T]
        //mirror.reflectModule(mirror.staticModule(clazz.getName)).instance.asInstanceOf[T]
    }

    def getField[T <: FieldReference](field: Field): Option[T] = {
        fields.find(f => f.field.equals(field)).asInstanceOf[Option[T]]
    }

    def fieldIsPresent(field: Field, get: Class[_]=>Seq[FieldReference]): Boolean = {
        if(knownNot.contains(field)) {
            logger.debug("Field {} is in `knownNot`", field)
            false
        }
        else if(getField(field).isDefined) {
            logger.debug("Field {} already is known", field)
            true
        }
        else {
            val rs = get(field.getDeclaringClass)
            logger.debug("Field {} yielded {} entries", field.getName.asInstanceOf[Any], rs)
            rs.foreach(f => fields = fields :+ f)
            val exists = fields.exists(f => f.field.equals(field))
            if(exists) known = known :+ field
            else knownNot = knownNot :+ field
            exists
        }
    }

    def findAnnotation(clazz: Class[_], st: SearchType, concrete: Boolean=true): Seq[Class[_]] = {
        AnnotationUtils.findAnnotation(basePackages, clazz, st, concrete)
    }
}
trait FieldReference {
    def field: java.lang.reflect.Field
}
sealed trait SearchType
object ANNOTATION extends SearchType
object INTERFACE extends SearchType
object AnnotationUtils {
    def findAnnotation(basePackages: Seq[String], clazz: Class[_], st: SearchType, concrete: Boolean=true): Seq[Class[_]] = {
        var annotations = Seq.empty[Class[_]]
        val classPathScanner = new SimpleClassPathScanner(basePackages.asJava, false)
        classPathScanner.includeConcrete()

        st match {
            case ANNOTATION => classPathScanner.addIncludeFilter(new AnnotationTypeFilter(clazz.asInstanceOf[Class[_ <: java.lang.annotation.Annotation]], true, true))
            case INTERFACE => classPathScanner.addIncludeFilter(new AssignableTypeFilter(clazz))
        }

        for (bd: BeanDefinition <- classPathScanner.findComponents.asScala) {
            val className = bd.getBeanClassName
            val cl = Class.forName(bd.getBeanClassName)
            if(!Modifier.isAbstract(cl.getModifiers) || !concrete) {
                annotations = annotations :+ cl
            }
        }
        annotations
    }
}