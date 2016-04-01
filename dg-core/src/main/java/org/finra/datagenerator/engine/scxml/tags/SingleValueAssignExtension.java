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

package org.finra.datagenerator.engine.scxml.tags;

import org.apache.commons.scxml.model.Assign;

import java.util.List;
import java.util.Map;

/**
 * Implementation of assign tag of scxml model
 */
public class SingleValueAssignExtension implements CustomTagExtension<Assign> {

    public Class<Assign> getTagActionClass() {
        return Assign.class;
    }

    public String getTagName() {
        return "assign";
    }

    public String getTagNameSpace() {
        return "http://www.w3.org/2005/07/scxml";
    }

    /**
     * Assigns one variable to one value
     *
     * @param action an Assign Action
     * @param possibleStateList a current list of possible states produced so far from expanding a model state
     *
     * @return the same list, with every possible state augmented with an assigned variable, defined by action
     */
    public List<Map<String, String>> pipelinePossibleStates(Assign action, List<Map<String, String>> possibleStateList) {
        for (Map<String, String> possibleState : possibleStateList) {
            possibleState.put(action.getName(), action.getExpr());
        }

        return possibleStateList;
    }
}
