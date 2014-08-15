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

import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.apache.log4j.Logger;

/**
 * A listener that stores the current events and states changes for use by other parts of DataGenerator
 */
public class StateMachineListener implements SCXMLListener {

    private TransitionTarget currentState;
    private TransitionTarget lastState;
    private Transition lastTransition;
    private static final Logger log = Logger.getLogger(StateMachineListener.class);
    private static final boolean IS_DEBUG_ENABLED = false;

    /**
     * Resets all the variables held by the listener
     */
    public void reset() {
        if (IS_DEBUG_ENABLED) {
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
        if (IS_DEBUG_ENABLED) {
            log.debug("Entering state:" + state.getId());
        }
        currentState = state;
    }

    @Override
    public void onExit(TransitionTarget state) {
        if (IS_DEBUG_ENABLED) {
            log.debug("Exiting state:" + state.getId());
        }
        lastState = state;
    }

    @Override
    public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition) {
        if (IS_DEBUG_ENABLED) {
            log.debug("Transitioning between:" + from.getId() + " and " + to.getId() + " using event "
                    + transition.getEvent());
        }
        lastTransition = transition;
    }
}
