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

import org.finra.datagenerator.engine.scxml.tags.boundary.action.BoundaryActionNumeric;

import java.util.List;
import java.util.Map;


/**
 * Implementation of dg:boundaryHiveIntPos tag
 */
public class PositiveBoundHiveInt extends BoundaryInteger<PositiveBoundHiveInt.PositiveBoundHiveIntTag> {

    public Class<PositiveBoundHiveIntTag> getTagActionClass() {
        return PositiveBoundHiveIntTag.class;
    }

    public String getTagName() {
        return "boundaryHiveIntPos";
    }

    public String getTagNameSpace() {
        return "org.finra.datagenerator";
    }

    /**
     * @param action            an Action of the type handled by this class
     * @param possibleStateList a current list of possible states produced so far from
     *                          expanding a model state
     * @return a list with positive boundary conditions assigned
     * to the variable
     */
    public List<Map<String, String>> pipelinePossibleStates(PositiveBoundHiveIntTag action,
                                                            List<Map<String, String>> possibleStateList) {
        final String minVALUE = Integer.toString(Integer.MIN_VALUE);
        final String maxVALUE = Integer.toString(Integer.MAX_VALUE);
        return returnStates(action, possibleStateList, buildNumericData(action, minVALUE, maxVALUE, true));
    }

    /**
     * A custom Action for the 'dg:positiveBoundHiveInt' tag inside models
     */
    public static class PositiveBoundHiveIntTag extends BoundaryActionNumeric {
        /**
         * constructor
         */
        public PositiveBoundHiveIntTag() {
            this.setMin(Integer.toString(Integer.MIN_VALUE));
            this.setMax(Integer.toString(Integer.MAX_VALUE));
        }
    }
}

