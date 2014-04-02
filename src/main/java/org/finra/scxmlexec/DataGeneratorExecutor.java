package org.finra.scxmlexec;

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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Created by robbinbr on 3/3/14.
 */
public class DataGeneratorExecutor extends SCXMLExecutor {

    protected static final Logger log = Logger.getLogger(DataGeneratorExecutor.class);

    private StateMachineListener listener;

    public DataGeneratorExecutor() throws ModelException {
        super();
    }

    public DataGeneratorExecutor(String stateMachineText) throws ModelException, SAXException, IOException {
        InputStream is = new ByteArrayInputStream(stateMachineText.getBytes());
        SCXML stateMachine = SCXMLParser.parse(new InputSource(is), null);
        initExecutor(stateMachine);
    }

    public DataGeneratorExecutor(SCXML xml) throws ModelException {
        initExecutor(xml);
    }

    public void initExecutor(SCXML stateMachine) throws ModelException {
        ELEvaluator elEvaluator = new ELEvaluator();
        ELContext context = new ELContext();
        this.listener = new StateMachineListener();

        this.setEvaluator(elEvaluator);
        this.setStateMachine(stateMachine);
        this.setRootContext(context);
        this.addListener(stateMachine, listener);

        this.reset();
    }

    public void initExecutor(URL fileUrl) throws ModelException, SAXException, IOException {
        SCXML stateMachine = SCXMLParser.parse(fileUrl, null);
        initExecutor(stateMachine);
    }

    public StateMachineListener getListener() {

        return listener;
    }

    /**
     * Reset the state machine, set the initial variables, and trigger the
     * initial events
     *
     * @throws org.apache.commons.scxml.model.ModelException
     */

    public void resetStateMachine(Set<String> varsOut, Map<String, String> initialVariablesMap,
                                  List<String> initialEvents) throws ModelException {
        this.resetStateMachine(varsOut, initialVariablesMap, initialEvents, null);
    }


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
     * Executes a list of given events. The format is
     * [beforeState]-event-[afterState] with the before and after states and
     * their separators optional
     *
     * @param commaSeparatedEvents
     * @throws ModelException
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

    public void findEvents(List<String> positive, List<String> negative) throws ModelException,
            SCXMLExpressionException, IOException {
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
                throw new IOException("Found null targets for transition: " + transition.getEvent() + " in state: "
                        + currentState.getId());
            }

            if (targets.size() > 1 || targets.isEmpty()) {
                throw new IOException("Found incorrect number of targets:" + targets.size() + "for transition: "
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
                    throw new RuntimeException("Error while evaluating the condition: " + condition + " in state: " +
                            currentState.getId(), ex);
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
     */
    public ArrayList<PossibleState> findPossibleStates(Set<String> varNames) throws ModelException,
            SCXMLExpressionException, IOException {
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
            possibleState.variablesAssignment.putAll(vars);
            possiblePositiveStates.add(possibleState);
        }
        return possiblePositiveStates;
    }

    public void traceDepth(ArrayList<ArrayList<PossibleState>> possiblePositiveStatesList, Set<String> varsOut,
                           Map<String, String> initialVariablesMap, List<String> initialEvents, Map<String,
            String> expandedVars) throws
            ModelException, IOException, SCXMLExpressionException {
        //log.debug("TraceDepth");
        if (possiblePositiveStatesList.isEmpty()) {
            this.resetStateMachine(varsOut, initialVariablesMap, initialEvents, expandedVars);
        } else {
            ArrayList<PossibleState> states = possiblePositiveStatesList.get(possiblePositiveStatesList.size() - 1);
            PossibleState initialState = states.get(0);
            this.getStateMachine().setInitial(initialState.nextStateName);
            this.getStateMachine().setInitialTarget((TransitionTarget) this.getStateMachine().getTargets().get
                    (initialState.nextStateName));
            for (Map.Entry<String, String> var : initialState.variablesAssignment.entrySet()) {
                this.getRootContext().set(var.getKey(), var.getValue());
            }
            this.reset();
        }

        //log.debug("Loop start");

        while (listener.getCurrentState() == null
                || (listener.getCurrentState() != null && !listener.getCurrentState().getId().equals("end"))) {
            //log.debug("ALL AFTER RESET: " + possiblePositiveStatesList);
            // Replay the last initial state
            /*for (ArrayList<PossibleState> states : possiblePositiveStatesList)*/
            if (possiblePositiveStatesList.size() > 0) {
                ArrayList<PossibleState> states = possiblePositiveStatesList.get(possiblePositiveStatesList.size() - 1);
                PossibleState initialState = states.get(0);
                //log.debug("**RESET");
                //log.debug("**SET INIT TO:" + initialState.nextStateName);
                this.getStateMachine().setInitial(initialState.nextStateName);
                this.getStateMachine().setInitialTarget((TransitionTarget) this.getStateMachine().getTargets().get
                        (initialState.nextStateName));
                for (Map.Entry<String, String> var : initialState.variablesAssignment.entrySet()) {
                    this.getRootContext().set(var.getKey(), var.getValue());
                }
                this.reset();

                //log.debug("current state:" + listener.getCurrentState().getId());

                if (!initialState.varsInspected) {
                    HashMap<String, String> varsVals = readVarsOut(varsOut);
                    //log.debug("varsVals has " + varsVals);
                    //log.debug("Vars not initialzed, initializing");
                    if (varsVals == null || varsVals.isEmpty()) {
                        throw new IOException("Empty or null varsVals");
                    }
                    for (Map.Entry<String, String> var : varsVals.entrySet()) {
                        String nextVal = var.getValue();
                        //log.debug("key:" + var.getKey());
                        //log.debug("val:" + nextVal);
                        if (nextVal.length() > 5 && nextVal.startsWith("set:{")) {
                            // Remove the set:{ and }
                            String[] vals = nextVal.substring(5, nextVal.length() - 1).split(",");

                            // Delete this state from the list
                            states.remove(0);
                            for (String val : vals) {
                                PossibleState possibleState = new PossibleState();
                                possibleState.id = initialState.id;
                                possibleState.nextStateName = initialState.nextStateName;
                                possibleState.transitionEvent = initialState.transitionEvent;
                                possibleState.variablesAssignment.putAll(initialState.variablesAssignment);
                                possibleState.variablesAssignment.put(var.getKey(), val);
                                possibleState.varsInspected = true;
                                states.add(0, possibleState);
                                //log.debug("Adding:" + possibleState);
                            }
                        } else {
                            states.get(0).variablesAssignment.put(var.getKey(), nextVal);
                            states.get(0).varsInspected = true;
                        }
                    }
                    initialState = states.get(0);
                }

                // Set the variables
                for (Map.Entry<String, String> var : initialState.variablesAssignment.entrySet()) {
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
     * @throws ModelException
     * @throws SCXMLExpressionException
     * @throws IOException
     */
    public void searchForScenariosDFS(PossibleState startState, Queue queue, Set<String> varsOut, Map<String,
            String> initialVariablesMap,
                                      List<String> initialEvents) throws ModelException, SCXMLExpressionException,
            IOException, SAXException {
        //log.debug(Thread.currentThread().getName() + " starting DFS on " + startState);
        //log.info("Search for scenarios using depth first search");

        ArrayList<ArrayList<PossibleState>> possiblePositiveStatesList = new ArrayList<ArrayList<PossibleState>>();
        ArrayList<String> currentStates = new ArrayList<String>();
        ArrayList<Integer> activePostiveState = new ArrayList<Integer>();

        // First we have to generate the first level in the depth, so that we have something to start
        // the recursion from
        //log.debug("Searching for the initial next possible states");
        traceDepth(possiblePositiveStatesList, varsOut, initialVariablesMap, startState.events, startState
                .variablesAssignment);
        //log.debug("Initial depth trace: " + possiblePositiveStatesList);

        int scenariosCount = 0;
        // Now we have the initial list with sets decompressed
        Map<String, String> dataSet = readVarsOut(varsOut);
        //log.debug(Thread.currentThread().getName() + " adding to queue: " + dataSet);
        queue.add(dataSet);
        while (true) {
            // Recursively delete one node from the end
            boolean empty;
            do {
                if (possiblePositiveStatesList.isEmpty()) {
                    empty = true;
                    break;
                }
                empty = false;
                int lastDepth = possiblePositiveStatesList.size() - 1;
                PossibleState removed = possiblePositiveStatesList.get(lastDepth).remove(0);

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
                    // Do nothing, since this is the main thread....
                }
            }
        }
    }

    private String getNextVarToExpand(PossibleState state) {
        for (Map.Entry<String, String> var : state.variablesAssignment.entrySet()) {
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
        String fullValue = state.variablesAssignment.get(variable);
        String[] values = fullValue.substring(5, fullValue.length() - 1).split(",");

        List<PossibleState> stateList = new ArrayList<PossibleState>();

        for (String singleValue : values) {
            PossibleState possibleStateInner = new PossibleState();
            possibleStateInner.id = state.id;
            possibleStateInner.variablesAssignment.putAll(state.variablesAssignment);
            possibleStateInner.variablesAssignment.put(variable, singleValue);
            possibleStateInner.transitionEvent = state.transitionEvent;
            possibleStateInner.nextStateName = state.nextStateName;
            possibleStateInner.events.addAll(state.events);
            possibleStateInner.varsInspected = true;
            stateList.add(possibleStateInner);
            //log.debug("Adding:" + possibleStateInner);
        }

        return stateList;
    }

    public List<PossibleState> searchForScenarios(Set<String> varsOut, Map<String, String> initialVariablesMap,
                                                  List<String> initialEvents, int maxEventReps, int maxScenarios,
                                                  int lengthOfScenario, int minCount) throws ModelException,
            SCXMLExpressionException, IOException, SAXException {

        int numberOfScenariosGenerated = 0;
        // Next-level PossibleStates
        List<PossibleState> nextLevel = new ArrayList<PossibleState>();

        // Initialize states to have start state
        PossibleState startState = new PossibleState();
        startState.variablesAssignment.putAll(initialVariablesMap);
        startState.events.addAll(initialEvents);

        int prevNextLevelSize = 0;
        nextLevel.add(startState);

        while ((nextLevel.size() < minCount) && (minCount > 0)) {
            prevNextLevelSize = nextLevel.size();

            // Initialize list of states for this depth
            List<PossibleState> states = new ArrayList<PossibleState>();
            states.addAll(nextLevel);
            nextLevel = new ArrayList<PossibleState>();

            for (PossibleState iState : states) {
                // Get initial variables
                Map<String, String> stateVariables = iState.variablesAssignment;

                // Get initial events
                List<String> stateEventPrefix = iState.events;

                // Reset ourselves (a state machine) to this state
                resetStateMachine(varsOut, initialVariablesMap, stateEventPrefix, stateVariables);

                // Get positive events from the current state
                ArrayList<String> positiveEvents = new ArrayList<String>();
                ArrayList<String> negativeEvents = new ArrayList<String>();
                findEvents(positiveEvents, negativeEvents);

                for (String pEvent : positiveEvents) {
                    // fire the event
                    List singleEventList = new ArrayList<String>();
                    singleEventList.add(pEvent);
                    fireEvents(singleEventList);

                    // Construct our current state, so that we can save it
                    PossibleState possibleState = new PossibleState();

                    possibleState.id = this.getListener().getCurrentState().getId();
                    possibleState.events.addAll(stateEventPrefix);
                    possibleState.events.add(pEvent.split("-")[1]);
                    possibleState.variablesAssignment.putAll(readVarsOut(varsOut));

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

    /**
     * Delete the scenario if an event is repeated more than maxEventReps times
     *
     * @param eventList
     * @return
     */
    private ArrayList<String> pruneEvents(ArrayList<String> eventList, List<String> initialEventsList,
                                          int maxEventReps, int lengthOfScenario) {
        // Count the number of repetitions of every event
        ArrayList<String> all = new ArrayList<String>();
        all.addAll(initialEventsList);
        all.addAll(eventList);

        if (all.size() > lengthOfScenario) {
            return null;
        }

        HashMap<String, Integer> count = new HashMap<String, Integer>();
        for (String event : all) {
            Integer counter = count.get(event);
            if (counter == null) {
                counter = 0;
            }
            counter++;
            count.put(event, counter);
            if (counter > maxEventReps) {
                return null;
            }
        }
        return eventList;
    }

    private void printEvents(String type, ArrayList<String> events, List<String> initialEventsList) {
        StringBuilder b = new StringBuilder();
        b.append(type);

        if (initialEventsList != null) {
            b
                    .append(initialEventsList)
                    .append(",");
        }

        boolean firstEvent = true;
        for (String event : events) {
            if (firstEvent) {
                firstEvent = false;
            } else {
                b.append(",");
            }
            b.append(event);
        }

        //log.info(b);

    }
}