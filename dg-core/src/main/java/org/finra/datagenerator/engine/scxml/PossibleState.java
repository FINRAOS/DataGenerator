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

package org.finra.datagenerator.engine.scxml;

import org.apache.commons.scxml.model.TransitionTarget;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents a partial traversal over an SCXML state machine, storing
 * a list of variable assignments and the current state machine state.
 */
public class PossibleState implements Serializable {
    final TransitionTarget nextState;
    final Map<String, String> variables;

    /**
     * Constructor
     *
     * @param nextState the next state to expand as part of a search over an SCXML model
     * @param variables variables assigned so far in the search
     */
    public PossibleState(final TransitionTarget nextState, final Map<String, String> variables) {
        this.nextState = nextState;
        this.variables = variables;
    }

    /**
     * toString
     *
     * @return the id of next state and the state of assigned variables
     */
    public String toString() {
        return "<" + nextState.getId() + ";" + variables.toString() + ">";
    }
}