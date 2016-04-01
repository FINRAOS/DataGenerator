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
 * Implementation of dg:negativeBoundHiveBigInt tag
 */
public class NegativeBoundHiveBigInt extends BoundaryInteger<NegativeBoundHiveBigInt.NegativeBoundHiveBigIntTag> {

    public Class<NegativeBoundHiveBigIntTag> getTagActionClass() {
        return NegativeBoundHiveBigIntTag.class;
    }

    public String getTagName() {
        return "negativeBoundHiveBigInt";
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
    public List<Map<String, String>> pipelinePossibleStates(NegativeBoundHiveBigIntTag action,
                                            List<Map<String, String>> possibleStateList) {
        final String minVALUE = Long.toString(Long.MIN_VALUE);
        final String maxVALUE = Long.toString(Long.MAX_VALUE);
        return returnStates(action, possibleStateList, buildNumericData(action, minVALUE, maxVALUE, false));
    }

    /**
     * A custom Action for the 'dg:negativeBoundHiveBigInt' tag inside models
     */
    public static class NegativeBoundHiveBigIntTag extends BoundaryActionNumeric {
        /**
         * constructor
         */
        public NegativeBoundHiveBigIntTag() {
            this.setMin(Long.toString(Long.MIN_VALUE));
            this.setMax(Long.toString(Long.MAX_VALUE));
        }
    }
}

