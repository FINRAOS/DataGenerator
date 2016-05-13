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
package org.finra.datagenerator.engine.scxml.tags.boundary;

import org.finra.datagenerator.engine.scxml.tags.boundary.action.BoundaryActionVarchar;

import java.util.List;
import java.util.Map;

/**
 * Implementation of dg:negativeBoundHiveVarchar tag
 */
public class NegativeBoundHiveVarchar extends BoundaryVarchar<NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag> {

    public Class<NegativeBoundHiveVarcharTag> getTagActionClass() {
        return NegativeBoundHiveVarcharTag.class;
    }

    public String getTagName() {
        return "negativeBoundHiveVarchar";
    }

    public String getTagNameSpace() {
        return "org.finra.datagenerator";
    }

    /**
     * @param action            an Action of the type handled by this class
     * @param possibleStateList a current list of possible states produced so far from
     *                          expanding a model state
     * @return a list with negative boundary conditions assigned
     * to the variable
     */
    public List<Map<String, String>> pipelinePossibleStates(NegativeBoundHiveVarcharTag action,
                                            List<Map<String, String>> possibleStateList) {

        return returnStates(action, possibleStateList, buildVarcharData(action, false));
    }

    /**
     * A custom Action for the 'dg:negativeBoundHiveVarchar' tag inside models
     */
    public static class NegativeBoundHiveVarcharTag extends BoundaryActionVarchar {
        /**
         * constructor
         */
        public NegativeBoundHiveVarcharTag() {
            this.setLength("10");
            this.setAllCaps("true");
        }
    }
}
