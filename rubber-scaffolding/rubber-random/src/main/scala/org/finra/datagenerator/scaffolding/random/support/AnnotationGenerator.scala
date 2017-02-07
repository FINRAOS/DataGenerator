package org.finra.datagenerator.scaffolding.random.support

import java.nio.file.{Files, Paths}

import org.finra.datagenerator.scaffolding.config.{AnnotationCapable, AnnotationField}
import org.finra.datagenerator.scaffolding.config.AnnotationField

/**
  * Created by dkopel on 1/5/17.
  */

class AnnotationGenerator extends AnnotationUtils {
    override val basePackages: Seq[String] = Seq("org.finra.datagenerator.scaffolding")

    case class FieldType(clazz: Set[Class[_]], javaType: String, default: String)
    object FieldType {
        def apply(clazz: Class[_]): FieldType = {
            val ft = types.find(f => f.clazz.contains(clazz))
            if(ft.isDefined) ft.get
            else {
                logger.error("Class {} does not have a valid mapper field type", clazz)
                throw new IllegalArgumentException
            }
        }

        def apply(f: AnnotationField[_, _]): FieldType = apply(f.inputType)

        val types = Set(
            FieldType(Set(classOf[String]), "String", "\"\""),
            FieldType(Set(classOf[Integer]), "int", "0"),
            FieldType(Set(classOf[Long], classOf[java.lang.Long]), "long", "0L"),
            FieldType(Set(classOf[Boolean]), "boolean", "false"),
            FieldType(Set(classOf[Float]), "float", "0F"),
            FieldType(Set(classOf[Double]), "double", "0D"),
            FieldType(Set(classOf[Char]), "char", "'\0'"),
            FieldType(Set(classOf[Byte]), "byte", "0"),
            FieldType(Set(classOf[Short]), "short", "0")
        )
    }

    def findAnnotationCapable: Seq[AnnotationCapable] = findAnnotation(classOf[AnnotationCapable], INTERFACE).map(a => makeObject(a)).asInstanceOf[Seq[AnnotationCapable]]

    def currentDir: String = System.getenv("PWD")

    def generateAnnotations = {
        val basePkg = getClass.getPackage.getName
        val pkg = basePkg+".annotations"
        val annotations = findAnnotationCapable
        annotations.foreach(a => {
            logger.debug("Class is: {}", a.getClass.getName)
            val className = a.getClass.getName
            val fields = a.values.map(v => {
                val ft = FieldType(v)
                s"${ft.javaType} ${v.name}() default ${ft.default};"
            }).mkString("\n\t")
            val code =
                s"""|package $pkg;\n
                    |import java.lang.annotation.ElementType;
                    |import java.lang.annotation.Retention;
                    |import java.lang.annotation.RetentionPolicy;
                    |import java.lang.annotation.Target;
                    |import org.finra.datagenerator.scaffolding.random.support.annotations.RandomConfigAnnotation;
                    |/** Auto-generated **/
                    |@Target(ElementType.FIELD)
                    |@Retention(RetentionPolicy.RUNTIME)
                    |@RandomConfigAnnotation(value=${className}.class)
                    |public @interface ${a.name} {
                    |\t$fields
                    |}""".stripMargin
            logger.debug("Annotation code source: {}", code)
            val srcRoot = currentDir+s"/src/main/java/"+pkg.replace(".", "/")
            val fileName = srcRoot+s"/${a.name}.java"
            logger.debug("Filename: {}", fileName)
            Files.write(Paths.get(fileName), code.getBytes)
        })
    }
}