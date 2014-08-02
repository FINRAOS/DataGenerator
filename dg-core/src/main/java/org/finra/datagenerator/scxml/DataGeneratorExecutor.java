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

import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.env.jsp.ELContext;
import org.apache.commons.scxml.env.jsp.ELEvaluator;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.apache.log4j.Logger;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by robbinbr on 3/3/14.
 */
public class DataGeneratorExecutor extends SCXMLExecutor {

    /**
     * Logger
     */
    protected static final Logger log = Logger.getLogger(DataGeneratorExecutor.class);

    private StateMachineListener listener;

    /**
     * Default public constructor
     *
     * @throws ModelException if errors happen during the super class
     * initialization {@link SCXMLExecutor}
     */
    public DataGeneratorExecutor() throws ModelException {
        super();
    }

    /**
     * Given the state chart xml text, initializes the DataGeneratorExecutor
     *
     * @param stateMachineText a String containing the SCXML text for the state
     * machine
     * @throws ModelException if errors occur during interpreting the XML as
     * SCXML
     * @throws org.xml.sax.SAXException if errors occur during parsing the XML
     * @throws java.io.IOException if errors occur during parsing
     */
    public DataGeneratorExecutor(final String stateMachineText) throws ModelException, SAXException, IOException {
        InputStream is = new ByteArrayInputStream(stateMachineText.getBytes());
        SCXML stateMachine = SCXMLParser.parse(new InputSource(is), null);
        initExecutor(stateMachine);
    }

    /**
     * Given the state chart xml model, initializes the DataGeneratorExecutor
     *
     * @param xml an {@link SCXML} model of the state machine
     * @throws ModelException if errors occur during interpreting the state
     * machine
     */
    public DataGeneratorExecutor(final SCXML xml) throws ModelException {
        initExecutor(xml);
    }

    private void initExecutor(final SCXML stateMachine) throws ModelException {
        ELEvaluator elEvaluator = new ELEvaluator();
        ELContext context = new ELContext();
        this.listener = new StateMachineListener();

        this.setEvaluator(elEvaluator);
        this.setStateMachine(stateMachine);
        this.setRootContext(context);
        this.addListener(stateMachine, listener);

        this.reset();
    }

    public StateMachineListener getListener() {

        return listener;
    }

    /**
     * Reset the state machine, set the initial variables, and trigger the
     * initial events
     *
     * @param varsOut the set of output variables
     * @param initialVariablesMap initial variables values
     * @param initialEvents initial events to fire on reset
     * @throws ModelException if errors happen during reset
     */
    public void resetStateMachine(Set<String> varsOut, Map<String, String> initialVariablesMap,
            List<String> initialEvents) throws ModelException {
        this.resetStateMachine(varsOut, initialVariablesMap, initialEvents, null);
    }

    /**
     * Resets the state machine to the start, then fires the initial events and
     * assigns the initial values of variables to initialVariablesMap
     *
     * @param varsOut a set of the names of the output variables
     * @param initialVariablesMap a map containing the initial value assignments
     * of variables
     * @param initialEvents initial events to fire after resetting
     * @param variableOverride override the values of variables with the current
     * map. Useful when a variable should have been expanded at that point.
     * @throws ModelException thwon due to errors calling go or due to errors
     * from fireEvents
     */
    public void resetStateMachine(Set<String> varsOut, Map<String, String> initialVariablesMap,
            List<String> initialEvents, Map<String, String> variableOverride) throws
            ModelException {
        // Go to the initial state
        this.reset();

        // Set the initial variables values
        for (String var : varsOut) {
            this.getRootContext().set(var, "");
        }

        for (Map.Entry<String, String> entry : initialVariablesMap.entrySet()) {
            this.getRootContext().set(entry.getKey(), entry.getValue());
        }

        listener.reset();
        this.go();
        fireEvents(initialEvents);

        if (variableOverride != null) {
            for (Map.Entry<String, String> entry : variableOverride.entrySet()) {
                this.getRootContext().set(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Executes a list of given events(). The format is
     * [beforeState]-event-[afterState] with the before and after states and
     * their separators optional
     *
     * @param commaSeparatedEvents a comma separated lists of events to fire
     * @throws ModelException thrown due to errors in triggerEvent
     */
    public void fireEvents(String commaSeparatedEvents) throws ModelException {
        if (commaSeparatedEvents == null) {
            return;
        }
        commaSeparatedEvents = commaSeparatedEvents.trim();
        if (commaSeparatedEvents == null || commaSeparatedEvents.length() == 0) {
            return;
        }

        String[] events = commaSeparatedEvents.split(",");
        for (String event : events) {
            //log.debug("Firing event: " + event);
            String eventName = event;
            if (eventName.contains("-")) {
                String[] parts = event.split("-");
                eventName = parts[1];
            }
            //log.debug("EventName:" + eventName);
            this.triggerEvent(new TriggerEvent(eventName, TriggerEvent.SIGNAL_EVENT));
        }
    }

    /**
     * Fires a list of given events
     *
     * @param events a list of the ids of the events to fire
     *
     * @throws ModelException thrown upon errors when calling triggerEvent
     */
    public void fireEvents(List<String> events) throws ModelException {
        if (events == null) {
            return;
        }

        for (String event : events) {
            //log.debug("Firing event: " + event);
            String eventName = event;
            if (eventName.contains("-")) {
                String[] parts = event.split("-");
                eventName = parts[1];
            }
            //log.debug("EventName:" + eventName);
            this.triggerEvent(new TriggerEvent(eventName, TriggerEvent.SIGNAL_EVENT));
        }
    }

    /**
     * Finds a list of positive and negative events given the current state of
     * the state machine.
     *
     * @param positive a list to be filled with the possible positive
     * transitions
     * @param negative a list to be filled with the possible negative
     * transitions
     * @throws ModelException thrown if we reach a null target or null condition
     */
    public void findEvents(List<String> positive, List<String> negative) throws ModelException {
        positive.clear();
        negative.clear();
        TransitionTarget currentState = listener.getCurrentState();
        if (currentState == null) {
            throw new ModelException("Reached a null state");
        }
        List<Transition> transitions = currentState.getTransitionsList();
        for (Transition transition : transitions) {
            String condition = transition.getCond();
            List<TransitionTarget> targets = transition.getTargets();

            // In our case we should only have one target always
            if (targets == null) {
                throw new ModelException("Found null targets for transition: " + transition.getEvent() + " in state: "
                        + currentState.getId());
            }

            if (targets.size() > 1 || targets.isEmpty()) {
                throw new ModelException("Found incorrect number of targets:" + targets.size() + "for transition: "
                        + transition.getEvent() + " in state: " + currentState.getId());
            }

            String nextStateId = targets.get(0).getId();
            String transitionCode = currentState.getId() + "-" + transition.getEvent() + "-" + nextStateId;
            if (condition == null) {
                positive.add(transitionCode);
            } else {
                Boolean result;
                try {
                    result = (Boolean) this.getEvaluator().eval(this.getRootContext(), condition);
                } catch (Exception ex) {
                    throw new RuntimeException("Error while evaluating the condition: " + condition + " in state: "
                            + currentState.getId(), ex);
                }
                if (result == null) {
                    throw new ModelException("Condition: " + condition + " evaluates to null");
                }

                if (result) {
                    positive.add(transitionCode);
                } else {
                    negative.add(transitionCode);
                }
            }
        }
    }

    private HashMap<String, String> readVarsOut(Set<String> varsOut) {
        HashMap<String, String> result = new HashMap<String, String>();
        for (String varName : varsOut) {
            result.put(varName, (String) this.getRootContext().get(varName));
        }
        return result;
    }

    /**
     * Check all the variables in the context. Generate a state with a list of
     * variables correctly assigned
     *
     * @param varNames a set of variable names
     * @return a list of possible states
     * @throws org.apache.commons.scxml.model.ModelException due to errors from
     * findEvent
     * @throws org.apache.commons.scxml.SCXMLExpressionException due to errors
     * from findEvent
     */
    public ArrayList<PossibleState> findPossibleStates(Set<String> varNames) throws ModelException,
            SCXMLExpressionException {
        //log.debug("findPossibleStates");
        ArrayList<PossibleState> possiblePositiveStates = new ArrayList<PossibleState>();
        ArrayList<String> positive = new ArrayList<String>();
        ArrayList<String> negative = new ArrayList<String>();
        findEvents(positive, negative);
        Map<String, String> vars = readVarsOut(varNames);
        for (String state : positive) {
            PossibleState possibleState = new PossibleState();
            String[] parts = state.split("-");
            possibleState.id = parts[0];
            possibleState.nextStateName = parts[2];
            possibleState.transitionEvent = parts[1];
            possibleState.getVariablesAssignment().putAll(vars);
            possiblePositiveStates.add(possibleState);
        }
        return possiblePositiveStates;
    }

    /**
     * Given a certain point in the state machine, this function will attempt to
     * trace the leftmost path down until it finds 'end'
     *
     * @param possiblePositiveStatesList A list of the levels until the end
     * state. Inside every level, a list is given breadthwise of the states at
     * that level.
     * @param varsOut a set of string listing the names of the output variables
     * @param initialVariablesMap initial variables assignment map
     * @param initialEvents a list of initial events to be fired when resetting
     * the state machine
     * @param expandedVars values of variables after expanding sets
     * @throws ModelException Can be thrown due to errors while resetting the
     * state machine or due to null variables.
     * @throws SCXMLExpressionException thrown due to errors when attempting to
     * find the next possible states
     */
    public void traceDepth(ArrayList<ArrayList<PossibleState>> possiblePositiveStatesList, Set<String> varsOut,
            Map<String, String> initialVariablesMap, List<String> initialEvents, Map<String, String> expandedVars) throws
            ModelException, SCXMLExpressionException {
        //log.debug("TraceDepth");
        if (possiblePositiveStatesList.isEmpty()) {
            this.resetStateMachine(varsOut, initialVariablesMap, initialEvents, expandedVars);
        } else {
            ArrayList<PossibleState> states = possiblePositiveStatesList.get(possiblePositiveStatesList.size() - 1);
            PossibleState initialState = states.get(0);
            this.getStateMachine().setInitial(initialState.nextStateName);
            this.getStateMachine().setInitialTarget((TransitionTarget) this.getStateMachine().getTargets().get(initialState.nextStateName));
            for (Map.Entry<String, String> var : initialState.getVariablesAssignment().entrySet()) {
                this.getRootContext().set(var.getKey(), var.getValue());
            }
            this.reset();
        }

        //log.debug("Loop start");
        while (listener.getCurrentState() == null
                || listener.getCurrentState() != null && !listener.getCurrentState().getId().equals("end")) {
            //log.debug("ALL AFTER RESET: " + possiblePositiveStatesList);
            // Replay the last initial state
            /*for (ArrayList<PossibleState> states : possiblePositiveStatesList)*/
            if (possiblePositiveStatesList.size() > 0) {
                ArrayList<PossibleState> states = possiblePositiveStatesList.get(possiblePositiveStatesList.size() - 1);
                PossibleState initialState = states.get(0);
                //log.debug("**RESET");
                //log.debug("**SET INIT TO:" + initialState.nextStateName);
                this.getStateMachine().setInitial(initialState.nextStateName);
                this.getStateMachine().setInitialTarget((TransitionTarget) this.getStateMachine().getTargets().get(initialState.nextStateName));

                for (Map.Entry<String, String> var : initialState.getVariablesAssignment().entrySet()) {
                    this.getRootContext().set(var.getKey(), var.getValue());
                }
                this.reset();

                //log.debug("current state:" + listener.getCurrentState().getId());
                if (!initialState.varsInspected) {
                    HashMap<String, String> varsVals = readVarsOut(varsOut);
                    //log.debug("varsVals has " + varsVals);
                    //log.debug("Vars not initialzed, initializing");
                    if (varsVals == null || varsVals.isEmpty()) {
                        throw new ModelException("Empty or null varsVals");
                    }
                    for (Map.Entry<String, String> var : varsVals.entrySet()) {
                        String nextVal = var.getValue();
                        //log.debug("key:" + var.getKey());
                        //log.debug("val:" + nextVal);
                        if (nextVal != null && nextVal.length() > 5 && nextVal.startsWith("set:{")) {
                            // Remove the set:{ and }
                            String[] vals = nextVal.substring(5, nextVal.length() - 1).split(",");

                            // Delete this state from the list
                            states.remove(0);
                            for (String val : vals) {
                                PossibleState possibleState = new PossibleState();
                                possibleState.id = initialState.id;
                                possibleState.nextStateName = initialState.nextStateName;
                                possibleState.transitionEvent = initialState.transitionEvent;
                                possibleState.getVariablesAssignment().putAll(initialState.getVariablesAssignment());
                                possibleState.getVariablesAssignment().put(var.getKey(), val);
                                possibleState.varsInspected = true;
                                states.add(0, possibleState);
                                //log.debug("Adding:" + possibleState);
                            }
                        } else {
                            states.get(0).getVariablesAssignment().put(var.getKey(), nextVal);
                            states.get(0).varsInspected = true;
                        }
                    }
                    initialState = states.get(0);
                }

                // Set the variables
                for (Map.Entry<String, String> var : initialState.getVariablesAssignment().entrySet()) {
                    this.getRootContext().set(var.getKey(), var.getValue());
                }
            }

            //log.debug("ALL BEFORE: " + possiblePositiveStatesList);
            ArrayList<PossibleState> nextPositiveStates = findPossibleStates(varsOut);
            //System.err.println("nextPositiveStates: " + nextPositiveStates);

            possiblePositiveStatesList.add(nextPositiveStates);
            //log.debug("ALL AFTER: " + possiblePositiveStatesList);
        }

        // Remove end
        possiblePositiveStatesList.remove(possiblePositiveStatesList.size() - 1);
    }

    /**
     * Do a depth first search looking for scenarios
     *
     * @param startState the start state
     * @param queue a queue where the output will be placed
     * @param varsOut a list of names of the output variables
     * @param initialVariablesMap initial values of variables
     * @param initialEvents initial events to fire
     * @param flags a map of shared flags
     * @throws ModelException thrown due to errors from traceDepth
     * @throws SCXMLExpressionException thrown due to errors from traceDepth
     */
    public void searchForScenariosDFS(PossibleState startState, Queue queue, Set<String> varsOut, Map<String, String> initialVariablesMap,
            List<String> initialEvents, Map<String, AtomicBoolean> flags)
            throws ModelException, SCXMLExpressionException {
        //log.debug(Thread.currentThread().getName() + " starting DFS on " + startState);
        //log.info("Search for scenarios using depth first search");

        ArrayList<ArrayList<PossibleState>> possiblePositiveStatesList = new ArrayList<ArrayList<PossibleState>>();

        // First we have to generate the first level in the depth, so that we have something to start
        // the recursion from
        //log.debug("Searching for the initial next possible states");
        traceDepth(possiblePositiveStatesList, varsOut, initialVariablesMap, startState.getEvents(), startState
                .getVariablesAssignment());
        //log.debug("Initial depth trace: " + possiblePositiveStatesList);

        int scenariosCount = 0;
        // Now we have the initial list with sets decompressed
        Map<String, String> dataSet = readVarsOut(varsOut);
        //log.debug(Thread.currentThread().getName() + " adding to queue: " + dataSet);
        queue.add(dataSet);
        while (!DefaultDistributor.isSomeFlagTrue(flags)) {
            // Recursively delete one node from the end
            boolean empty;
            do {
                if (possiblePositiveStatesList.isEmpty()) {
                    empty = true;
                    break;
                }
                empty = false;
                int lastDepth = possiblePositiveStatesList.size() - 1;
                possiblePositiveStatesList.get(lastDepth).remove(0);

                //log.debug("Removing: " + removed.nextStateName);
                if (possiblePositiveStatesList.get(lastDepth).isEmpty()) {
                    //log.debug("Empty at level: " + possiblePositiveStatesList.size());
                    possiblePositiveStatesList.remove(possiblePositiveStatesList.size() - 1);
                    empty = true;
                }
            } while (empty);

            if (empty) {
                // We're done
                break;
            }

            //log.debug("**After removing, depth trace: " + possiblePositiveStatesList);
            traceDepth(possiblePositiveStatesList, varsOut, initialVariablesMap, initialEvents, null);
            //log.debug("**After finding next, depth trace: " + possiblePositiveStatesList);

            dataSet = readVarsOut(varsOut);
            //log.debug(Thread.currentThread().getName() + " adding to queue: " + dataSet);
            queue.add(dataSet);

            scenariosCount++;
            if (scenariosCount % 10000 == 0) {
                log.info("Queue size=" + queue.size());
            }

            if (queue.size() > 1000000) {
                log.info("Queue size " + queue.size() + " waiting for 1 sec");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    log.info("Interrupted ", ex);
                }
            }
        }
    }

    private String getNextVarToExpand(PossibleState state) {
        for (Map.Entry<String, String> var : state.getVariablesAssignment().entrySet()) {
            if (var.getKey().startsWith("_")) {
                continue;
            }
            String nextVal = var.getValue();

            if (nextVal.length() > 5 && nextVal.startsWith("set:{")) {
                return var.getKey();
            }
        }

        return null;
    }

    private PossibleState getStateNeedingExpansion(List<PossibleState> states) {
        for (PossibleState state : states) {
            if (getNextVarToExpand(state) != null) {
                return state;
            }
        }

        return null;
    }

    private List<PossibleState> expandStateOnVariable(PossibleState state, String variable) {
        String fullValue = state.getVariablesAssignment().get(variable);
        String[] values = fullValue.substring(5, fullValue.length() - 1).split(",");

        List<PossibleState> stateList = new ArrayList<PossibleState>();

        for (String singleValue : values) {
            PossibleState possibleStateInner = new PossibleState();
            possibleStateInner.id = state.id;
            possibleStateInner.getVariablesAssignment().putAll(state.getVariablesAssignment());
            possibleStateInner.getVariablesAssignment().put(variable, singleValue);
            possibleStateInner.transitionEvent = state.transitionEvent;
            possibleStateInner.nextStateName = state.nextStateName;
            possibleStateInner.getEvents().addAll(state.getEvents());
            possibleStateInner.varsInspected = true;
            stateList.add(possibleStateInner);
            //log.debug("Adding:" + possibleStateInner);
        }

        return stateList;
    }

    private void setStateMachineStartState(PossibleState iState, Set<String> varsOut, Map<String, String> initialVariablesMap) throws ModelException {
        if (iState.id != null) {
            this.getStateMachine().setInitial(iState.id);
            this.getStateMachine().setInitialTarget((TransitionTarget) this.getStateMachine().getTargets().get(iState.id));

            //this.reset() moved up before the for loop, potential fix for infinite loop issue
            this.reset();
            for (Map.Entry<String, String> var : iState.getVariablesAssignment().entrySet()) {
                this.getRootContext().set(var.getKey(), var.getValue());
            }
        } else {
            resetStateMachine(varsOut, initialVariablesMap, null, iState.getVariablesAssignment());
        }
    }

    /**
     *
     * @param varsOut set of output variables names
     * @param initialVariablesMap initial variables values
     * @param initialEvents initial events to fire on reset
     * @param maxScenarios maximum number of scenarios to generate
     * @param minCount minimum number of scenarios to look for
     * @return a list of possible states
     *
     * @throws ModelException if an error occurs during firing the events or
     * setting the start state
     * @throws SCXMLExpressionException if an error occurs while firing the
     * events
     */
    public List<PossibleState> searchForScenarios(Set<String> varsOut, Map<String, String> initialVariablesMap,
            List<String> initialEvents, long maxScenarios, int minCount)
            throws ModelException, SCXMLExpressionException {

        //log.info("Inside search for scenarios");
        //int numberOfScenariosGenerated = 0;
        // Next-level PossibleStates
        List<PossibleState> nextLevel = new ArrayList<PossibleState>();

        // Initialize states to have start state
        PossibleState startState = new PossibleState();
        startState.getVariablesAssignment().putAll(initialVariablesMap);
        startState.getEvents().addAll(initialEvents);

        int prevNextLevelSize = 0;
        nextLevel.add(startState);

        //int levels = 0;
        while (nextLevel.size() < minCount && minCount > 0) {

            //System.out.println("***** Current state:\n" + nextLevel.toString());
            prevNextLevelSize = nextLevel.size();
            //levels++;
            //log.info("At level:" + levels + " current size:" + prevNextLevelSize);
            // Initialize list of states for this depth
            List<PossibleState> states = new ArrayList<PossibleState>();
            states.addAll(nextLevel);
            nextLevel = new ArrayList<PossibleState>();

            for (PossibleState iState : states) {
                //log.info("\nExpanding state: " + iState.id);
                // Get initial variables
                //Map<String, String> stateVariables = iState.getVariablesAssignment();

                // Get initial events
                List<String> stateEventPrefix = iState.getEvents();

                if (iState.id != null && iState.id.equalsIgnoreCase("end")) {
                    throw new RuntimeException("Could not achieve the required bootstrap " + minCount
                            + " without reaching the end state. Achieved problem split: " + prevNextLevelSize
                            + ". Please either change your model or reduce the required split");
                }

                // Reset ourselves (a state machine) to this state
                // resetStateMachine(varsOut, initialVariablesMap, stateEventPrefix, stateVariables);
                setStateMachineStartState(iState, varsOut, initialVariablesMap);

                // Get positive events from the current state
                ArrayList<String> positiveEvents = new ArrayList<String>();
                ArrayList<String> negativeEvents = new ArrayList<String>();
                findEvents(positiveEvents, negativeEvents);

                for (String pEvent : positiveEvents) {
                    setStateMachineStartState(iState, varsOut, initialVariablesMap);
                    // fire the event
                    List<String> singleEventList = new ArrayList<String>();
                    singleEventList.add(pEvent);
                    //log.info("Current state:" + this.getListener().getCurrentState().getId());
                    //log.info("Firing:" + singleEventList);
                    fireEvents(singleEventList);
                    //log.info("Now in state:" + this.getListener().getCurrentState().getId());

                    // Construct our current state, so that we can save it
                    PossibleState possibleState = new PossibleState();

                    possibleState.id = this.getListener().getCurrentState().getId();
                    possibleState.getEvents().addAll(stateEventPrefix);
                    possibleState.getEvents().add(pEvent.split("-")[1]);
                    possibleState.getVariablesAssignment().putAll(readVarsOut(varsOut));

                    // Need to handle the case where a variable has a "set" as its value
                    // We construct a set of "expanded states" starting with the state
                    // that we are about to add.
                    List<PossibleState> expandedStates = new ArrayList<PossibleState>();
                    expandedStates.add(possibleState);
                    PossibleState stateToExpand = getStateNeedingExpansion(expandedStates);

                    // Here we expand any variables that require expansion
                    // Repeat the process until no states require expansion
                    while (stateToExpand != null) {
                        expandedStates.remove(stateToExpand);
                        String variable = getNextVarToExpand(stateToExpand);
                        expandedStates.addAll(expandStateOnVariable(stateToExpand, variable));
                        stateToExpand = getStateNeedingExpansion(expandedStates);
                    }

                    // All of the expanded states exist at the next level
                    nextLevel.addAll(expandedStates);
                }
            }
        }

        return nextLevel;
    }
}
