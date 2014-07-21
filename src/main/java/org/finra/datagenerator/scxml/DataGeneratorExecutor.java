package org.finra.datagenerator.scxml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
import org.finra.datagenerator.utils.ScXmlUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Created by robbinbr on 3/3/14.
 */
public class DataGeneratorExecutor extends SCXMLExecutor {
    protected static final Logger log = Logger.getLogger(DataGeneratorExecutor.class);

    private StateMachineListener listener;
    private final static String setPrefix = "set:{";

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
     * Reset the state machine, set the initial variables, and trigger the initial events
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
     * Executes a list of given events(). The format is [beforeState]-event-[afterState] with the before and after
     * states and their separators optional
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

    private Map<String, Map<String, String>> readVarsOut(Map<String, Set<String>> varsOut) {
    	Map<String, Map<String, String>> result = new HashMap<String, Map<String,String>>();
    	for (String stepName : varsOut.keySet()) {
    		Set<String> stepVars = varsOut.get(stepName);
    		Map<String, String> stepVarsOut = new HashMap<String, String>();
    		for (String varNamr : stepVars) {
    			stepVarsOut.put(varNamr, (String) this.getRootContext().get(varNamr));
    		}
    		result.put(stepName, stepVarsOut);
    	}
        return result;
    }
    
    /**
     * Check all the variables in the context. Generate a state with a list of variables correctly assigned
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
            possibleState.getVariablesAssignment().putAll(vars);
            possiblePositiveStates.add(possibleState);
        }
        return possiblePositiveStates;
    }

    public void traceDepth(ArrayList<ArrayList<PossibleState>> possiblePositiveStatesList, Map<String, Set<String>> varsOut,
            Map<String, String> initialVariablesMap, List<String> initialEvents, Map<String, String> expandedVars) throws
            ModelException, IOException, SCXMLExpressionException {
        //log.debug("TraceDepth");
        if (possiblePositiveStatesList.isEmpty()) {
            this.resetStateMachine(ScXmlUtils.mapSetToSet(varsOut), initialVariablesMap, initialEvents, expandedVars);
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
        while (listener.getCurrentState() == null || (listener.getCurrentState() != null && !listener.getCurrentState().getId().equals("end"))) {
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
                   Map<String, Map<String, String>> varsVals = readVarsOut(varsOut);
                   Map<String, String> varsValsSimple = readVarsOut(ScXmlUtils.mapSetToSet(varsOut));
                    //log.debug("varsVals has " + varsVals);
                    //log.debug("Vars not initialzed, initializing");
                    if (varsValsSimple == null || varsValsSimple.isEmpty()) {
                        throw new IOException("Empty or null varsVals");
                    }
                    
					for (String stepName : varsVals.keySet()) {
						String[][] varsValsForStepMultiplied = multiplyValues(varsVals.get(stepName));
						if (null != varsValsForStepMultiplied) {
							String[] tt = varsValsForStepMultiplied[0];
							states.remove(0);
							for(int i = 1; i < varsValsForStepMultiplied.length ; i++) {
								PossibleState possibleState = new PossibleState();
	                            possibleState.id = initialState.id;
	                            possibleState.nextStateName = initialState.nextStateName;
	                            possibleState.transitionEvent = initialState.transitionEvent;
	                            possibleState.getVariablesAssignment().putAll(initialState.getVariablesAssignment());
								for (int y = 0; y < tt.length; y++) {
	                                possibleState.getVariablesAssignment().put(tt[y], varsValsForStepMultiplied[i][y]);
	                            }
								 possibleState.varsInspected = true;
	                             states.add(0, possibleState);
							}
						} else {
							for (Entry<String, String> rr : varsVals.get(stepName).entrySet()) {
								states.get(0).getVariablesAssignment().put(rr.getKey(), rr.getValue());
	                            states.get(0).varsInspected = true;
							}
                        }
						initialState = states.get(0);
					}
                }

                // Set the variables
                for (Map.Entry<String, String> var : initialState.getVariablesAssignment().entrySet()) {
                    this.getRootContext().set(var.getKey(), var.getValue());
                }
            }

            //log.debug("ALL BEFORE: " + possiblePositiveStatesList);
            ArrayList<PossibleState> nextPositiveStates = findPossibleStates(ScXmlUtils.mapSetToSet(varsOut));
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
    public void searchForScenariosDFS(PossibleState startState, Queue queue, Map<String, Set<String>> varsOut, Map<String, String> initialVariablesMap, List<String> initialEvents, Map<String, AtomicBoolean> flags)
            throws ModelException, SCXMLExpressionException,
            IOException, SAXException {
        //log.debug(Thread.currentThread().getName() + " starting DFS on " + startState);
        //log.info("Search for scenarios using depth first search");

        ArrayList<ArrayList<PossibleState>> possiblePositiveStatesList = new ArrayList<ArrayList<PossibleState>>();
        ArrayList<String> currentStates = new ArrayList<String>();
        ArrayList<Integer> activePostiveState = new ArrayList<Integer>();

        // First we have to generate the first level in the depth, so that we have something to start
        // the recursion from
        //log.debug("Searching for the initial next possible states");
        traceDepth(possiblePositiveStatesList, varsOut, initialVariablesMap, startState.getEvents(), startState.getVariablesAssignment());
        //log.debug("Initial depth trace: " + possiblePositiveStatesList);

        int scenariosCount = 0;
        // Now we have the initial list with sets decompressed
        Map<String, String> dataSet = readVarsOut(ScXmlUtils.mapSetToSet(varsOut));
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

            dataSet = readVarsOut(ScXmlUtils.mapSetToSet(varsOut));
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

    public List<PossibleState> searchForScenarios(Set<String> varsOut, Map<String, String> initialVariablesMap,
            List<String> initialEvents, int maxEventReps, long maxScenarios,
            int lengthOfScenario, int minCount) throws ModelException,
            SCXMLExpressionException, IOException, SAXException {

        int numberOfScenariosGenerated = 0;
        // Next-level PossibleStates
        List<PossibleState> nextLevel = new ArrayList<PossibleState>();

        // Initialize states to have start state
        PossibleState startState = new PossibleState();
        startState.getVariablesAssignment().putAll(initialVariablesMap);
        startState.getEvents().addAll(initialEvents);

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
                Map<String, String> stateVariables = iState.getVariablesAssignment();

                // Get initial events
                List<String> stateEventPrefix = iState.getEvents();

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

    /**
     * Delete the scenario if an event is repeated more than maxEventReps times
     *
     * @param eventList
     * @return
     */
    private ArrayList<String> pruneEvents(ArrayList<String> eventList, List<String> initialEventsList, int maxEventReps, int lengthOfScenario) {
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
    }
    
    private String[][] multiplyValues(Map<String, String> varsValsForStep) {
		Map<String, String[]> values = new HashMap<String, String[]>();
		List<String> keys = new ArrayList<String>();
		
		for (Entry<String, String> varsValForStep : varsValsForStep.entrySet()) {
			String aValue = varsValForStep.getValue();
			keys.add(varsValForStep.getKey());
			String[] vals = extractSetValues(aValue);
			if (null != vals) {
				values.put(varsValForStep.getKey(), vals);
			}
		}
		
		if (values.size() == 0) {
			return null;
		}
		
		int resultLength = 1;
		for(String key : values.keySet()) {
			resultLength *= values.get(key).length;
		}

		// go through result matrix and fill it
		// one row for columns names 
		String[][] resultAsArray = new String[resultLength + 1][varsValsForStep.size()];

		//set column names
		for(int i = 0; i < varsValsForStep.size(); i++ ) {
			resultAsArray[0][i] = keys.get(i); 
		}
		
		int stepSize = 1;
		
		int i = 0;
		for(String valueKey : varsValsForStep.keySet()) {
			String value = varsValsForStep.get(valueKey);
			if (value.contains(setPrefix)) {
				String[] value1 = values.get(valueKey);
				int step = resultLength / (value1.length * stepSize);
				for(int y1 = 0; y1 < resultLength; y1++) {
					resultAsArray[y1 + 1][i] = value1[(y1/step)%value1.length];
				}
				stepSize *= 2;				
			} else {
				for(int y2 = 1; y2 <= resultLength; y2++) {
					resultAsArray[y2][i] = value;
				}
			}
			i++;
		}
		
		return resultAsArray;
	}
    
	private String[] extractSetValues(String aValue) {
		if (aValue != null && aValue.length() > setPrefix.length() && aValue.startsWith(setPrefix)) {
			// Remove the set:{ and }
			return aValue.substring(setPrefix.length(), aValue.length() - 1).split(",");
		}
		return null;
	}
}
