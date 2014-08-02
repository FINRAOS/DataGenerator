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
package org.finra.datagenerator.scxml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by robbinbr on 3/11/14.
 */

/**
 * Defines a possible state that a state can be in. A possible state is a
 * combination of a state and values for variables.
 */
public class PossibleState {

    String id;
    /**
     * The name of the next state
     */
    String nextStateName;
    String transitionEvent;

    boolean varsInspected;

    /**
     * The variables that need to be set before jumping to that state
     */
    private final Map<String, String> variablesAssignment = new HashMap<>();

    /**
     * Any events that should be executed between start state and this state
     */
    private final List<String> events = new ArrayList<>();

    @Override
    public String toString() {
        return "{id=" + id + ",next:" + nextStateName + ",trans:" + transitionEvent + ","
                + "varsInspected:" + varsInspected + ",vars:" + variablesAssignment + ",events:" + events + "}\n";
    }

    public List<String> getEvents() {
        return events;
    }

    public Map<String, String> getVariablesAssignment() {
        return variablesAssignment;
    }

    @Override
    public boolean equals(Object a) {
        if (!(a instanceof PossibleState)) {
            return false;
        }
        return this.toString().equals(a.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
