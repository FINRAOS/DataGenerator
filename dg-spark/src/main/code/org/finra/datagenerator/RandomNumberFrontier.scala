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

package org.finra.datagenerator

import java.util
import java.util.concurrent.atomic.AtomicBoolean

import org.finra.datagenerator.engine.Frontier

/**
 * Frontier implementation for generating data with model
 *
 * Created by Brijesh on 5/28/2015.
 */
class RandomNumberFrontier extends Frontier with java.io.Serializable {

  /**
   * Iterate for loop from 1 to Number in each frontier
   * Generate random number string and add them to Map
   * Add Map to Queue
   *
   * @param randomNumberQueue Random Number Queue
   * @param flag Atomic boolean flag
   */
  override def searchForScenarios(randomNumberQueue:util.Queue[util.Map[String, String]], flag: AtomicBoolean) : Unit = {

    this.synchronized {

      for (i <- 1 to RandomNumberEngine.numberInEachFrontier) {

        //Generate Random Number and add it to Map
        val randomNumber = scala.util.Random
        val value = randomNumber.nextInt(100000).toString

        val key = "Key for Random Number"

        val randomNumberMap = new util.HashMap[String, String]()

        randomNumberMap.put(key, value)

        //Add randomNumberMap to randomNumberQueue
        randomNumberQueue.add(randomNumberMap)

      }
    }
  }
}
