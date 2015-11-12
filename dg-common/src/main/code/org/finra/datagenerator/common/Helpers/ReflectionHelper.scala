
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

import StringHelper.StringImplicits
import scala.language.{implicitConversions, reflectiveCalls}

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
    def invokeGetter(name: String, skipIfNotExists: Boolean = false, caseInsensitive: Boolean = true): Any = {
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
    val shortSomeClass = Some(0.toShort).getClass
    val intSomeClass = Some(0).getClass
    val longSomeClass = Some(0L).getClass
    val charSomeClass = Some(' ').getClass
    val doubleSomeClass = Some(0.toDouble).getClass
    val boolSomeClass = Some(true).getClass
    val byteSomeClass = Some(0.toByte).getClass
    val stringSomeClass = Some("").getClass
    val floatSomeClass = Some(0.toFloat).getClass
    val dateSomeClass = Some("20080405".toDateTime).getClass
    val sqlDateSomeClass = Some("20080405".toDate).getClass
    val shortOptionClass = shortSomeClass.getSuperclass
    val intOptionClass = intSomeClass.getSuperclass
    val longOptionClass = longSomeClass.getSuperclass
    val charOptionClass = charSomeClass.getSuperclass
    val doubleOptionClass = doubleSomeClass.getSuperclass
    val boolOptionClass = boolSomeClass.getSuperclass
    val byteOptionClass = byteSomeClass.getSuperclass
    val stringOptionClass = stringSomeClass.getSuperclass
    val floatOptionClass = floatSomeClass.getSuperclass
    val dateOptionClass = dateSomeClass.getSuperclass
    val sqlDateOptionClass = sqlDateSomeClass.getSuperclass

    val classesConvertibleFromString = Seq(stringClass, longClass, charClass, boolClass, intClass, byteClass
      , doubleClass, floatClass, shortClass, dateClass, sqlDateClass
      , shortOptionClass, intOptionClass, longOptionClass, charOptionClass, doubleOptionClass
      , boolOptionClass, byteOptionClass, stringOptionClass, floatOptionClass, dateOptionClass, sqlDateOptionClass
      , shortSomeClass, intSomeClass, longSomeClass, charSomeClass, doubleSomeClass
      , boolSomeClass, byteSomeClass, stringSomeClass, floatSomeClass, dateSomeClass, sqlDateSomeClass)

    /**
     * Invoke the Scala setter under the current object.
     * @param name Name of setter to invoke
     * @param value Value to pass to setter
     * @param skipIfNotExists If this is false, throws IllegalArgumentException; if true, continues if getter not found
     * @param caseInsensitive Whether nor to ignore case when searching for getter by name
     * @param forceTypeCoercion If true, do not require the setter param type to match the value type, but instead perform a cast if necessary.
     */
    def invokeSetter(name: String, value: Any, skipIfNotExists: Boolean = false, caseInsensitive: Boolean = true
                      , forceTypeCoercion: Boolean = false): Unit = {
      val methodOption = getClassMethods(ref.getClass).find(method => {
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

      if (methodOption.nonEmpty) {
        val method = methodOption.get
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
            case `shortOptionClass` | `shortSomeClass` => if (valueAsString.isEmpty) None else Some(valueAsString.toShort)
            case `intOptionClass` | `intSomeClass` => if (valueAsString.isEmpty) None else Some(valueAsString.toInt)
            case `longOptionClass` | `longSomeClass` => if (valueAsString.isEmpty) None else Some(valueAsString.toLong)
            case `charOptionClass` | `charSomeClass` => if (valueAsString.isEmpty) None else Some(valueAsString.head)
            case `doubleOptionClass` | `doubleSomeClass` => if (valueAsString.isEmpty) None else Some(valueAsString.toDouble)
            case `boolOptionClass` | `boolSomeClass` => if (valueAsString.isEmpty) None else Some(valueAsString.toBoolean)
            case `byteOptionClass` | `byteSomeClass` => if (valueAsString.isEmpty) None else Some(valueAsString.toByte)
            case `stringOptionClass` | `stringSomeClass` => if (valueAsString == null) None else Some(valueAsString)
            case `floatOptionClass` | `floatSomeClass` => if (valueAsString.isEmpty) None else Some(valueAsString.toFloat)
            case `dateOptionClass` | `dateSomeClass` => if (valueAsString.isEmpty) None else Some(valueAsString.toDateTime)
            case `sqlDateOptionClass` | `sqlDateSomeClass` => if (valueAsString.isEmpty) None else Some(valueAsString.toDate)
          }
          method.invoke(ref, convertedValue)
        } else {
          method.invoke(ref, value.asInstanceOf[AnyRef])
        }
      } else if (!skipIfNotExists) {
        throw new IllegalArgumentException(
          s"Setter method for var $name not found in class ${ref.getClass.getName} with case insensitivity=$caseInsensitive and target value=$value.")
      } // TODO: Else log?
    }
  }
}
