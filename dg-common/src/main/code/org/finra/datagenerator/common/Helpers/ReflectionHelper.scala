
/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finra.datagenerator.common.Helpers

import org.finra.datagenerator.common.Helpers.StringHelper.StringImplicits

import scala.language.{implicitConversions, reflectiveCalls}
import scala.reflect.runtime.{universe => ru}

/**
 * Reflection helper methods
 */
object ReflectionHelper {
  /**
   * Get the class type from a Scala companion object, else None.
   * @tparam T Type parameter
   * @return Option(Class type), else None
   */
  def companionOf[T : Manifest] : Option[AnyRef] = try {
    val classOfT = implicitly[Manifest[T]].runtimeClass
    val companionClassName = classOfT.getName + "$"
    val companionClass = Class.forName(companionClassName)
    val moduleField = companionClass.getField("MODULE$")
    Option(moduleField.get(null))
  } catch {
    case e: Exception => None
  }

  // Performance improvement to cache class methods so we don't have to use reflection on the same class repeatedly.
  private val classToMethodsMap = collection.mutable.HashMap[Class[_ <: AnyRef], Array[java.lang.reflect.Method]]()
  private def ensureClassMethodsPopulated(value: Class[_ <: AnyRef]) = {
    value.synchronized {
      if (!classToMethodsMap.contains(value)) {
        classToMethodsMap.put(value, value.getMethods)
      }
    }
  }

  /**
   * Get all methods in a class.
   * @param value Class
   * @return All the methods in a class
   */
  def getClassMethods(value: Class[_ <: AnyRef]) : Array[java.lang.reflect.Method] = {
    ensureClassMethodsPopulated(value)
    classToMethodsMap.get(value).get
  }

  /**
   * Implicit methods to do reflection on any reference object.
   * @param ref Reference object
   */
  implicit class Reflector(ref: AnyRef) {
    /**
     * Invoke the Scala getter under the current object, and return the value from the getter.
     * @param name Name of getter to invoke
     * @param skipIfNotExists If this is false, throws IllegalArgumentException; if true, continues if getter not found
     * @param caseInsensitive Whether nor to ignore case when searching for getter by name
     * @return Value returned after invoking specified getter, else returns Unit
     */
    def invokeScalaGetter(name: String, skipIfNotExists: Boolean = false, caseInsensitive: Boolean = true): Any = {
      val methodOption = getClassMethods(ref.getClass).find(method =>
        (caseInsensitive && method.getName.toLowerCase == name.toLowerCase
          || method.getName == name)
          && method.getParameterTypes.length == 0)

      if (methodOption.nonEmpty) {
        methodOption.get.invoke(ref)
      } else if (!skipIfNotExists) {
        throw new IllegalArgumentException(
          s"Getter method for var $name not found in class ${ref.getClass.getName} with case insensitivity = $caseInsensitive.")
      } else {
        Unit
      }
    }

    val stringClass = "".getClass
    val longClass = 1L.getClass
    val charClass = ' '.getClass
    val boolClass = true.getClass
    val intClass = 0.getClass
    val byteClass = 0.toByte.getClass
    val doubleClass = 0.toDouble.getClass
    val floatClass = 0.toFloat.getClass
    val shortClass = 0.toShort.getClass
    val dateClass = ("20080405".toDateTime).getClass
    val sqlDateClass = ("20080405".toDate).getClass
    val someClass = Some(true).getClass // My kingdom for type reification! Grrrr, type erasure is the pits!!
    val optionClass = someClass.getSuperclass

    val simpleClasses = Seq(stringClass, longClass, charClass, boolClass, intClass, byteClass
      , doubleClass, floatClass, shortClass, dateClass, sqlDateClass)
    val optionClasses = Seq(someClass, optionClass)
    val complexClasses = optionClasses

    val classesConvertibleFromString = simpleClasses ++ complexClasses

    /**
     * Invoke the Scala setter under the current object.
     * @param name Name of setter to invoke
     * @param value Value to pass to setter
     * @param skipIfNotExists If this is false, throws IllegalArgumentException; if true, continues if getter not found
     * @param caseInsensitive Whether nor to ignore case when searching for getter by name
     * @param forceTypeCoercion If true, do not require the setter param type to match the value type, but instead perform a cast if necessary.
     */
    def invokeScalaSetter(name: String, value: Any, skipIfNotExists: Boolean = false, caseInsensitive: Boolean = true
                      , forceTypeCoercion: Boolean = false): Unit = {
      val methods = getClassMethods(ref.getClass).filter(method => {
        if (forceTypeCoercion && value.isInstanceOf[String]) {
          ((caseInsensitive && method.getName.toLowerCase == name.toLowerCase + "_$eq"
            || method.getName == name + "_$eq")
            && method.getParameterTypes.length == 1
            && classesConvertibleFromString.contains(method.getParameterTypes.head)) // Setter param type should match passed-in value type.
        } else {
          ((caseInsensitive && method.getName.toLowerCase == name.toLowerCase + "_$eq"
            || method.getName == name + "_$eq")
          && method.getParameterTypes.length == 1
          && method.getParameterTypes.head.isInstance(value)) // Setter param type should match passed-in value type.
        }
      })

      if (methods.nonEmpty) {
        val methodWithPrimitiveArgMaybe = methods.find(method => simpleClasses.contains(method.getParameterTypes.head))
        if (methodWithPrimitiveArgMaybe.nonEmpty) {
          val method = methodWithPrimitiveArgMaybe.get
          if (forceTypeCoercion && value.isInstanceOf[String]) {
            val parameterType = method.getParameterTypes.head
            val valueAsString = value.toString
            val convertedValue = parameterType match {
              case `stringClass` => valueAsString
              case `longClass` => Long.box(valueAsString.toLong)
              case `charClass` => Char.box(valueAsString.head)
              case `boolClass` => Boolean.box(valueAsString.toBoolean)
              case `intClass` => Int.box(valueAsString.toInt)
              case `byteClass` => Byte.box(valueAsString.toByte)
              case `doubleClass` => Double.box(valueAsString.toDouble)
              case `floatClass` => Float.box(valueAsString.toFloat)
              case `dateClass` => valueAsString.toDateTime
              case `sqlDateClass` => valueAsString.toDate
              case `shortClass` => Short.box(valueAsString.toShort)
            }
            method.invoke(ref, convertedValue)
          } else {
            method.invoke(ref, value.asInstanceOf[AnyRef])
          }
        } else {
          val method = methods.find(method => complexClasses.contains(method.getParameterTypes.head)).get

          if (forceTypeCoercion && value.isInstanceOf[String]) {
            val parameterType = method.getParameterTypes.head
            val valueAsString = value.toString

            if (optionClasses.contains(method.getParameterTypes.head)) {
              // Because of type erasure, there is no way (believe me, I've tried) to get the type argument using Java reflection.
              // If we have Object[Int] as method param, JVM sees it as Option[Object] and that's all we can get via reflection. Boo hoo hoo. :(
              // Even if we try to get creative by calling the setter and then getter to check for a ClassCastException
              // that would tell us the type, nope, that doesn't work either, because calling a getter by reflection doesn't
              // throw an exception even if the wrong type has been forced into the variable.
              // But in Scala 2.10+, the Scala compiler hides some type information in the compiled output,
              // which allows us to jump through a few hoops to learn the erased type (TypeTags and mirrors and whatnot).
              // This code is very ugly -- Scala reflection is way too complicated and hard to use.
              // I don't understand it fully -- with some work this code could probably be cleaned up somewhat...
              // but it gets the job done and that's probably fine.

              val typeMirror = ru.runtimeMirror(ref.getClass.getClassLoader)
              val instanceMirror = typeMirror.reflect(ref)
              val members = instanceMirror.symbol.typeSignature.members

              val scalaReflectionMethod = members.find(member => {
                member.name.toString.trim == method.getName &&
                member.typeSignature.isInstanceOf[scala.reflect.internal.Types#MethodType] &&
                  {
                    val methodTypeSignature = member.typeSignature.asInstanceOf[scala.reflect.internal.Types#MethodType]
                    methodTypeSignature.params.size == 1 && methodTypeSignature.resultType.toString == "Unit"
                  }
              }).get

              val typeSignature = scalaReflectionMethod.typeSignature
              val singleParam: scala.reflect.internal.Symbols#TermSymbol = typeSignature.asInstanceOf[scala.reflect.internal.Types#MethodType].
                params.head.asInstanceOf[scala.reflect.internal.Symbols#TermSymbol]
              val typeString = singleParam.originalInfo.toString // e.g., "Option[Int]"

              // Would be nice if we could refactor this matching to be strongly typed based on the actual type, not the type to string.

              val convertedValue = typeString match {
                case "Option[Int]" | "Some[Int]" => if (valueAsString.isEmpty) None else Some(valueAsString.toInt)
                case "Option[Long]" | "Some[Long]" => if (valueAsString.isEmpty) None else Some(valueAsString.toLong)
                case "Option[Char]" | "Some[Char]" => if (valueAsString.isEmpty) None else Some(valueAsString.head)
                case "Option[Double]" | "Some[Double]" => if (valueAsString.isEmpty) None else Some(valueAsString.toDouble)
                case "Option[Boolean]" | "Some[Boolean]" => if (valueAsString.isEmpty) None else Some(valueAsString.toBoolean)
                case "Option[Byte]" | "Some[Byte]" => if (valueAsString.isEmpty) None else Some(valueAsString.toByte)
                case "Option[String]" | "Some[String]" => if (valueAsString.isEmpty) None else Some(valueAsString)
                case "Option[Float]" | "Some[Float]" => if (valueAsString.isEmpty) None else Some(valueAsString.toFloat)
                case "Option[java.util.Date]" | "Some[java.util.Date]" => if (valueAsString.isEmpty) None else Some(valueAsString.toDateTime)
                case "Option[java.sql.Date]" | "Some[java.sql.Date]" => if (valueAsString.isEmpty) None else Some(valueAsString.toDate)
              }

              method.invoke(ref, convertedValue)
            } else {
              throw new IllegalArgumentException(s"Cannot call setter [instance of ${ref.getClass.getName}]."
                +s"${method.getParameterTypes.head.getName} because ReflectionHandler does not support its type.")
            }
          } else {
            method.invoke(ref, value.asInstanceOf[AnyRef])
          }
        }
      } else if (!skipIfNotExists) {
        throw new IllegalArgumentException(
          s"Setter method for var $name not found in class ${ref.getClass.getName} with case insensitivity=$caseInsensitive and target value=$value.")
      } // TODO: Else log?
    }
  }
}
