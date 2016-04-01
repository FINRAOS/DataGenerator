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

import org.finra.datagenerator.common.NodeData.NodeData
import org.finra.datagenerator.common.NodeData.NodeDataType.NodeDataType

import scala.beans.BeanProperty

/*
Social network user
 */
class User( private var _dataType: UserType.UserType,
            @BeanProperty var firstName: String,
            @BeanProperty var lastName: String,
            @BeanProperty val dateOfBirth: java.sql.Date, // Assert > 13 years old when creating
            @BeanProperty var geographicalLocation: (Double, Double),
            @BeanProperty var isSecret: Boolean,
            @BeanProperty val socialNetworkId: Long) extends NodeData(None) {
  override def defaultDisplayableDataId: String = s"$socialNetworkId (${dataType.name}): $lastName, $firstName"

  override def getDataType: UserType.UserType = {
    _dataType
  }
}
