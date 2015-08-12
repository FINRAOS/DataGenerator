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

import org.finra.datagenerator.consumer.DataPipe

import scala.beans.BeanProperty

/**
 * Wrapper for each result
 *
 * Created by Brijesh on 6/15/2015.
 */
class ScalaDataPipe extends DataPipe with  java.io.Serializable {

  val dataMap = new util.HashMap[String,String]()

  /**
   * Get data map and append it to String Builder
   *
   * @param outTemplate Array of String
   */
  override def getPipeDelimited(outTemplate: Array[String]): String = {

    val stringBuilder = new StringBuilder(1024)

    for(str <- outTemplate) {

      if(stringBuilder.nonEmpty) {
        stringBuilder.append('|')
      }
      stringBuilder.append(getDataMap.get(str))
    }
    stringBuilder.toString()
  }
}
