package org.finra.datagenerator.scaffolding.config

import java.lang.reflect.Modifier

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.finra.datagenerator.scaffolding.utils.{Logging, Mapper, SimpleClassPathScanner, TypeUtils}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.`type`.filter.AssignableTypeFilter

import scala.collection.JavaConverters._
import scala.reflect.runtime.universe._

/**
  * Created by dkopel on 12/13/16.
  */
@org.springframework.context.annotation.Configuration
class CConfigR

object ConfigurationUtils extends Logging {
    @Autowired
    var applicationContext: ApplicationContext = _
    val objectMapper: ObjectMapper with ScalaObjectMapper = Mapper.mapper

    val mirror = runtimeMirror(getClass.getClassLoader)

    def context: ApplicationContext = {
        if(applicationContext == null) {
            new AnnotationConfigApplicationContext(classOf[CConfigR])
        } else {
            applicationContext
        }
    }

    def findConfigurableBundles(basePackages: Seq[String]): Map[ConfigBundleName, ConfigBundle] = {
        var bundles = Seq.empty[ConfigBundle]
        val classPathScanner = new SimpleClassPathScanner(basePackages.asJava)
        classPathScanner.addIncludeFilter(new AssignableTypeFilter(classOf[Configurable]))
        for(bd: BeanDefinition <- classPathScanner.findComponents.asScala) {
            val className = bd.getBeanClassName

            if(className.endsWith("$")) {
                val c = mirror.staticModule(className)
                val b = mirror.reflectModule(c).instance.asInstanceOf[Configurable]
                bundles = bundles :+ b.configBundle
            } else {
                val c = mirror.staticModule(className)
                val cl = Class.forName(className)
                logger.debug("class: {}", cl)
                if(!Modifier.isAbstract(cl.getModifiers)) {
                    val b: Configurable = {
                        cl.newInstance().asInstanceOf[Configurable]
//                        if(c.isStatic) {
//                            mirror.reflectModule(c).instance.asInstanceOf[Configurable]
//                        } else {
//
//                        }
                    }

                    bundles = bundles :+ b.configBundle
                }
            }
        }
        bundles.map(b => (b.name, b)).toMap
    }

    def getConfig[T](configDef: ConfigDefinition[T], over: Option[T]=Option.empty)(implicit ev: TypeTag[T]): Config[T] = configDef(
        if(over.isDefined) over
        else getProperty[T](configDef)
    )

    def getProperty(key: String) = context.getEnvironment.getProperty(key)


    def getProperty[T](config: ConfigDefinition[T])(implicit ev: TypeTag[T]): Option[T] = {
        val _val = getProperty(config.name.name)

        if(_val != null) {
            implicit val m = TypeUtils.toManifest[T]
            Option(objectMapper.convertValue[T](_val))
        }
        else Option.empty
    }
}
