
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

    /**
     * Invoke the Scala setter under the current object.
     * @param name Name of setter to invoke
     * @param value Value to pass to setter
     * @param skipIfNotExists If this is false, throws IllegalArgumentException; if true, continues if getter not found
     * @param caseInsensitive Whether nor to ignore case when searching for getter by name
     */
    def invokeSetter(name: String, value: Any, skipIfNotExists: Boolean = false, caseInsensitive: Boolean = true): Unit = {
      val methodOption = getClassMethods(ref.getClass).find(method =>
        (caseInsensitive && method.getName.toLowerCase == name.toLowerCase + "_$eq"
          || method.getName == name + "_$eq")
        && method.getParameterTypes.length == 1
        && method.getParameterTypes.head.isInstance(value)) // Setter param type should match passed-in value type.

      if (methodOption.nonEmpty) {
        methodOption.get.invoke(ref, value.asInstanceOf[AnyRef])
      } else if (!skipIfNotExists) {
        throw new IllegalArgumentException(
          s"Setter method for var $name not found in class ${ref.getClass.getName} with case insensitivity=$caseInsensitive and target value=$value.")
      } // TODO: Else log?
    }
  }
}
