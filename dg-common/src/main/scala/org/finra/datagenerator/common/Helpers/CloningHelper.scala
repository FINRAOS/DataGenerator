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

import com.rits.cloning.Cloner

/**
 * Implicit methods for object cloning
 */
object CloningHelper {
  // Uses third-party tool
  private val cloner = new Cloner()

  implicit class ObjectCloning[T](val cloneableObject: T) extends AnyVal {

    /**
     * Performs a deep clone of the specified object, using a third-party library utilizing Java reflection.
     * Avoid using if possible -- it's better-performing if you're able to implement your own method to copy an
     * object, or copy using Scala's built in copy if utilizing a case class.
     * @return Deep clone of object
     */
    def deepClone(classesNotToClone: Set[Class[_]] = Set()): T = {
      classesNotToClone.foreach(classNotToClone => {
        cloner.dontClone(classNotToClone)
      })
      cloner.deepClone(cloneableObject)
    }
  }
}
