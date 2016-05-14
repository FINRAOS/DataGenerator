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

package org.finra.datagenerator.common.Graph

import org.finra.datagenerator.common.NodeData.DisplayableData

/**
 * Description of an edge to add to the graph. Can be used to rebuild a graph
 * (for example, perhaps an analogous graph of a different type) in the same order as originally created.
 */
abstract class EdgeCreationDescription[+T <: DisplayableData]
