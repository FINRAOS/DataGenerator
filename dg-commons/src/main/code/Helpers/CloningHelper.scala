package Helpers

import com.rits.cloning.Cloner

/**
 * Implicit methods for object cloning
 */
object CloningHelper {
  // Uses third-party tool
  private val cloner = new Cloner()

  implicit class ObjectCloning[T](private val cloneableObject: T) {

    /**
     * Performs a deep clone of the specified object, using a third-party library utilizing Java reflection.
     * Avoid using if possible -- it's better-performing if you're able to implement your own method to copy an
     * object, or copy using Scala's built in copy if utilizing a case class.
     * @return
     */
    def deepClone: T = {
      cloner.deepClone(cloneableObject)
    }
  }

}