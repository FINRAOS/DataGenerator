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

package org.finra.datagenerator.engine.negscxml;

import org.apache.commons.scxml.model.TransitionTarget;

import java.util.Map;

/**
 * Marshall Peters
 * Date: 9/4/14
 */
public class NegPossibleState {
    final TransitionTarget nextState;
    final Map<String, String> variables;
    final String negVariable;

    /**
     * Constructor
     *
     * @param nextState the next state to expand as part of a search over an SCXML model
     * @param variables variables assigned so far in the search
     * @param negVariable the single variable with a negative value assignment, or null if none exists
     */
    public NegPossibleState(final TransitionTarget nextState, final Map<String, String> variables, final String negVariable) {
        this.nextState = nextState;
        this.variables = variables;
        this.negVariable = negVariable;
    }

    /**
     * toString
     *
     * @return the id of next state and the state of assigned variables
     */
    public String toString() {
        return "<" + nextState.getId() + ";" + variables.toString() + ";" + negVariable + ">";
    }
}