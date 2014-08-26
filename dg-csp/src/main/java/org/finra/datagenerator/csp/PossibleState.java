package org.finra.datagenerator.csp;

import org.apache.commons.scxml.model.TransitionTarget;

import java.lang.String;import java.util.Map;

/**
 * Marshall Peters
 * Date: 8/26/14
 */
public class PossibleState {
    TransitionTarget nextState;
    Map<String, String> variables;

    public PossibleState(TransitionTarget nextState, Map<String, String> variables) {
        this.nextState = nextState;
        this.variables = variables;
    }

    public String toString() {
        return "<" + nextState.toString() + ";" + variables.toString() + ">";
    }
}