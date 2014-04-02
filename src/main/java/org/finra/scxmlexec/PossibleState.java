package org.finra.scxmlexec;

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

    boolean varsInspected = false;

    /**
     * The variables that need to be set before jumping to that state
     */
    final Map<String, String> variablesAssignment = new HashMap<String, String>();

    /**
     * Any events that should be executed between start state and this state
     */

    List<String> events = new ArrayList<String>();

    @Override
    public String toString() {
        return "id=" + id + ",next:" + nextStateName + ",trans:" + transitionEvent + "," +
                "varsInspected:" + varsInspected + ",vars:" + variablesAssignment + ",events:" + events;
    }

    @Override
    public boolean equals(Object a){
        return this.toString().equals(a.toString());
    }
}
