package org.finra.datagenerator.scxml;

import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.apache.log4j.Logger;

public class StateMachineListener implements SCXMLListener {
    private TransitionTarget currentState = null;
    private TransitionTarget lastState = null;
    private Transition lastTransition = null;
    private static final Logger log = Logger.getLogger(StateMachineListener.class);
    private static final boolean isDebugEnabled = false;

    public void reset() {
        if (isDebugEnabled) {
            log.debug("RESET");
        }
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

    @Override
    public void onEntry(TransitionTarget state) {
        if (isDebugEnabled) {
            log.debug("Entering state:" + state.getId());
        }
        currentState = state;
    }

    @Override
    public void onExit(TransitionTarget state) {
        if (isDebugEnabled) {
            log.debug("Exiting state:" + state.getId());
        }
        lastState = state;
    }

    @Override
    public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition) {
        if (isDebugEnabled) {
            log.debug("Transitioning between:" + from.getId() + " and " + to.getId() + " using event "
                    + transition.getEvent());
        }
        lastTransition = transition;
    }
}
