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

import org.finra.datagenerator.consumer.{DataPipe, DataConsumer}
import java.util

import scala.collection.JavaConversions._

/**
 * Pass the data through dataPipe and write output to OutputStream
 *
 * Created by Brijesh on 6/11/2015.
 */
class ScalaDataConsumer extends DataConsumer with java.io.Serializable {

  val scalaDataWriter = new ScalaDataWriter

  private val scalaDatawriters = new util.ArrayList[AnyRef]

  val scalaDataPipe = new ScalaDataPipe

  /**
   * Put key value pair into dataPipe,
   * call writeOutput method to write output to Output Stream for each dataWriter
   */
  override def consume(initialVars: util.Map[String, String]): Int = {

    for (ent <- initialVars.entrySet) {

      scalaDataPipe.getDataMap.put(ent.getKey, ent.getValue)
    }

    // Writing Output
    for(dw <- scalaDatawriters) {

      scalaDataWriter.writeOutput(scalaDataPipe)
    }

    1   // return Integer
  }
}
