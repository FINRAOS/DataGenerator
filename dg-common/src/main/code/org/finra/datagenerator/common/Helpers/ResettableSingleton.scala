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

// Motivation for this trait is described in the following blog post:
//   http://googlyadventures.blogspot.com/2015/08/today-i-learned-limitation-of.html

/**
 * Have a class's companion object extend this trait and you can use that class as a singleton.
 * The reason to use this over a scala singleton is the presence of the reset method, which comes in especially
 * handy, e.g., in unit tests, where you don't want any global state (e.g., configuration properties, etc.) persisted
 * between runs.
 */
trait ResettableSingleton[T] {
  private var instanceMaybe: Option[T] = None

  /**
   * Singleton instance
   * @return Object of the class
   */
  def instance: T = {
    if (instanceMaybe.isEmpty) {
      instanceMaybe = Option(instantiate)
    }
    instanceMaybe.get
  }

  /**
   * Create an instance of the class
   * This is abstract because there's no guarantee that every class has, e.g., a parameterless constructor.
   * @return Object of the class
   */
  def instantiate: T

  /**
   * Resets the singleton instance. Intended to be used if performing multiple test runs without restarting the JVM.
   */
  def reset(): Unit = {
    instanceMaybe = None
  }
}
