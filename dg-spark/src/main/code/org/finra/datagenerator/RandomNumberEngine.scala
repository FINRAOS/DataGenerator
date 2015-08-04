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

import java.io.InputStream
import java.util

import org.finra.datagenerator.distributor.SearchDistributor
import org.finra.datagenerator.engine.{Engine, Frontier}

import scala.io

/**
 * Engine Implementation for generating data
 *
 * Created by Brijesh on 5/26/2015.
 */

class RandomNumberEngine extends Engine {

  var totalCount: Int = 0
  var frontierList = new util.LinkedList[Frontier]()

  /**
   * Iterate for loop from 0 to Number of Split and create instance of Frontier
   * Add them to FrontierList
   *
   * Call distribute method which distribute data to Spark using Map and Reduce
   *
   * @param distributor SearchDistributor
   */
  def process(distributor: SearchDistributor): Unit  = {

    for (i <- 0 to RandomNumberEngine.numSplit) {
      val frontierImplementation = new RandomNumberFrontier
      frontierList.add(frontierImplementation)
    }

    distributor.distribute(frontierList)
  }

  /**
   * Read the lines from text file using InputStream
   * Store these two values to Total Number of Count and Number of Split
   *
   * @param inputFileStream the model input stream
   */
  def setModelByInputFileStream(inputFileStream : InputStream) : Unit = {

    val fileLines = io.Source.fromInputStream(inputFileStream).getLines()

    try{
      totalCount = fileLines.next().toInt
      RandomNumberEngine.numSplit = fileLines.next().toInt
    }catch {
      case e: NumberFormatException => throw new RuntimeException("File should have two lines, one int in each.")
    }
    /*
    try { (totalCount, RandomNumberEngine.numSplit) ;
      (fileLines.next().toInt, fileLines.next().toInt)
    } catch {
      case e: NumberFormatException => throw new RuntimeException("File should have two lines, one int in each.")
    }
    */

    RandomNumberEngine.numberInEachFrontier = totalCount / RandomNumberEngine.numSplit

  }

  /**
   * Set the model with a string
   *
   * @param model the model text
   */
  def setModelByText(model: String) : Unit = {
    // TODO set model with a string
    ???
  }

  /**
   * bootstrapMin setter
   *
   * @param min set the desired bootstrap min
   * @return this
   */
  def setBootstrapMin(min: Int) : Engine = {
    ???
    this
  }
}

object RandomNumberEngine {

  //Declare static variable in Object RNEngine
  var numberInEachFrontier: Int = 0
  var numSplit: Int = 0

}
