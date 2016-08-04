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

import java.io.OutputStream
import java.io.ObjectOutputStream

import org.finra.datagenerator.consumer.DataPipe
import org.finra.datagenerator.writer.DataWriter

/**
 * DataWriter to write output to Output Stream
 *
 * Created by Brijesh on 6/12/2015.
 */

class ScalaDataWriter extends DataWriter with java.io.Serializable {

  val template: Array[String] = new Array[String](10)

  /**
   * Write bytes to Output Stream
   *
   * @param dataPipe Datapipe
   */
  def writeOutput(dataPipe: DataPipe): Unit = {

    val os: OutputStream = System.out
    val objectOS = new ObjectOutputStream(os)

    objectOS.write(dataPipe.getPipeDelimited(template).getBytes)

    objectOS.write("\n".getBytes)
  }
}
