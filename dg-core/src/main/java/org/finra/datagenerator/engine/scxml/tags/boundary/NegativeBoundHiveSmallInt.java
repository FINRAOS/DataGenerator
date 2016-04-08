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
 * Implementation of dg:negativeBoundHiveSmallInt tag
 */
public class NegativeBoundHiveSmallInt extends BoundaryInteger<NegativeBoundHiveSmallInt.NegativeBoundHiveSmallIntTag> {

    public Class<NegativeBoundHiveSmallIntTag> getTagActionClass() {
        return NegativeBoundHiveSmallIntTag.class;
    }

    public String getTagName() {
        return "negativeBoundHiveSmallInt";
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
    public List<Map<String, String>> pipelinePossibleStates(NegativeBoundHiveSmallIntTag action,
                                                            List<Map<String, String>> possibleStateList) {
        final String minVALUE = "-32768";
        final String maxVALUE = "32767";
        return returnStates(action, possibleStateList, buildNumericData(action, minVALUE, maxVALUE, false));
    }

    /**
     * A custom Action for the 'dg:negativeBoundHiveSmallInt' tag inside models
     */
    public static class NegativeBoundHiveSmallIntTag extends BoundaryActionNumeric {
        /**
         * constructor
         */
        public NegativeBoundHiveSmallIntTag() {
            this.setMin("-32768");
            this.setMax("32767");
        }
    }
}

