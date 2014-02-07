package org.finra.scxmlexec;

import java.util.ArrayList;

import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.apache.log4j.Logger;

public class StateMachineListener implements SCXMLListener {

    private final ArrayList<TransitionTarget> states = new ArrayList<TransitionTarget>();
    private final ArrayList<Transition> transitions = new ArrayList<Transition>();
    private TransitionTarget currentState = null;
    private TransitionTarget lastState = null;
    private Transition lastTransition = null;
    private static final Logger log = Logger.getLogger(StateMachineListener.class);

    public void reset() {
        log.debug("RESET");
        states.clear();
        transitions.clear();
        currentState = null;
        lastState = null;
        lastTransition = null;
    }

    public Transition getLastTransition() {
        return lastTransition;
    }

    public TransitionTarget getCurrentState() {
        return currentState;
    }

    public TransitionTarget getLastState() {
        return lastState;
    }

    public ArrayList<TransitionTarget> getStatesList() {
        return states;
    }

    public ArrayList<Transition> getTransitionList() {
        return transitions;
    }

    @Override
    public void onEntry(TransitionTarget state) {
        log.debug("Entering state:" + state.getId());
        currentState = state;
        states.add(state);
    }

    @Override
    public void onExit(TransitionTarget state) {
        log.debug("Exiting state:" + state.getId());
        lastState = state;
    }

    @Override
    public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition) {
        log.debug("Transitioning between:" + from.getId() + " and " + to.getId() + " using event "
                + transition.getEvent());
        lastTransition = transition;
        transitions.add(transition);
    }

}
