
package Helpers

import scala.language.{implicitConversions, reflectiveCalls}

/**
 * Reflection helper methods
 */
object ReflectionHelper {
  /**
   * Get the class type from a Scala companion object, else None.
   * @tparam T
   * @return
   */
  def companionOf[T : Manifest] : Option[AnyRef] = try {
    val classOfT = implicitly[Manifest[T]].runtimeClass
    val companionClassName = classOfT.getName + "$"
    val companionClass = Class.forName(companionClassName)
    val moduleField = companionClass.getField("MODULE$")
    Some(moduleField.get(null))
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
   * @param value
   * @return
   */
  def getClassMethods(value: Class[_ <: AnyRef]) : Array[java.lang.reflect.Method] = {
    ensureClassMethodsPopulated(value)
    classToMethodsMap.get(value).get
  }

  /**
   * Implicit methods to do reflection on any reference object.
   * @param ref
   * @return
   */
  //noinspection NoReturnTypeForImplicitDef
  implicit def reflector(ref: AnyRef) = new {
    /**
     * Invoke the Scala getter under the current object, and return the value from the getter.
     * @param name
     * @param skipIfNotExists
     * @param caseInsensitive
     * @return
     */
    def invokeGetter(name: String, skipIfNotExists: Boolean = false, caseInsensitive: Boolean = true): Any = {
      val methodOption = getClassMethods(ref.getClass).find(method =>
        (caseInsensitive && method.getName.toLowerCase == name.toLowerCase
          || method.getName == name)
        && method.getParameterTypes.length == 0)

      if (methodOption.nonEmpty) {
        methodOption.get.invoke(ref)
      } else if (!skipIfNotExists) {
        throw new IllegalArgumentException(s"Getter method for var $name not found in class ${ref.getClass.getName} with case insensitivity = $caseInsensitive.")
      } // TODO: Else log?
    }

    /**
     * Invoke the Scala setter under the current object.
     * @param name
     * @param value
     * @param skipIfNotExists
     * @param caseInsensitive
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
        throw new IllegalArgumentException(s"Setter method for var $name not found in class ${ref.getClass.getName} with case insensitivity=$caseInsensitive and target value=$value.")
      } // TODO: Else log?
    }
  }
}
