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

package org.finra.datagenerator.common.SocialNetwork_Example

import org.finra.datagenerator.common.Helpers.RandomHelper

/**
 * Utility methods relating to data that are part of social event users.
 * Not fully implemented, and not used, because this is for data generation and not stub generation, and
 * the current implementation only supports stub generation and doesn't yet have any implementation of
 * stub-to-data translation.
 */
object SocialNetworkUtilities {
  def getRandomIsSecret: Boolean = {
    RandomHelper.evaluateProbability(0.1) // 10% of accounts are secret accounts.
  }

  def getRandomGeographicalLocation: (Double, Double) = {
    ((RandomHelper.randWithConfiguredSeed.nextInt(999) + 1).toDouble / 100,
     (RandomHelper.randWithConfiguredSeed.nextInt(999) + 1).toDouble / 100)
  }

  def getDistanceBetweenCoordinates(point1: (Double, Double), point2: (Double, Double)): Double = {
    // sqrt( (x2-x1)^2 + (y2-y2)^2 )
    val xDiff = point1._1 - point2._1
    val yDiff = point1._2 - point2._2
    math.sqrt((xDiff * xDiff) + (yDiff * yDiff))
  }

  def getDistanceWithinThresholdOfCoordinates(point1: (Double, Double), point2: (Double, Double)): Double = {
    ???
  }

  def getRandomBirthDate: java.util.Date = {
    ???
  }

  val COORDINATE_THRESHOLD = 2.0
  def areCoordinatesWithinThreshold(point1: (Double, Double), point2: (Double, Double)): Boolean = {
    getDistanceBetweenCoordinates(point1, point2) < COORDINATE_THRESHOLD
  }
}
