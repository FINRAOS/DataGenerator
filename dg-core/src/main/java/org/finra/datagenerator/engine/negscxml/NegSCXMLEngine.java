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

package org.finra.datagenerator.engine.negscxml;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.env.jsp.ELContext;
import org.apache.commons.scxml.env.jsp.ELEvaluator;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.Assign;
import org.apache.commons.scxml.model.CustomAction;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.OnEntry;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.engine.Engine;
import org.finra.datagenerator.engine.Frontier;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Marshall Peters
 * Date: 8/25/14
 */
public class NegSCXMLEngine extends SCXMLExecutor implements Engine {

    private SCXML model;
    private int bootStrapMin;

    /**
     * Constructor
     */
    public NegSCXMLEngine() {
        super();

        ELEvaluator elEvaluator = new ELEvaluator();
        ELContext context = new ELContext();

        this.setEvaluator(elEvaluator);
        this.setRootContext(context);
    }

    /**
     * Searches the model for all variable assignments and makes a default map of those variables, setting them to ""
     *
     * @return the default variable assignment map
     */
    private Map<String, String> fillInitialVariables() {
        Map<String, TransitionTarget> targets = model.getChildren();

        Set<String> variables = new HashSet<>();
        for (TransitionTarget target : targets.values()) {
            OnEntry entry = target.getOnEntry();
            List<Action> actions = entry.getActions();
            for (Action action : actions) {
                if (action instanceof Assign) {
                    String variable = ((Assign) action).getName();
                    variables.add(variable);
                }
            }
        }

        Map<String, String> result = new HashMap<>();
        for (String variable : variables) {
            result.put(variable, "");
        }

        return result;
    }

    /**
     * Expands a list of current variable assignments, adding on an additional variable which is assigned values
     * from a String array; produced all possible combinations of current variable assignments and values
     * for the new variable
     *
     * A cartesian product of core with set in essence
     *
     * @param core the set of current variable assignments
     * @param variable the new variable
     * @param set the set of values variable will take on
     * @return the product
     */
    private List<Map<String, String>> takeProduct(List<Map<String, String>> core, String variable, String[] set) {
        List<Map<String, String>> result = new LinkedList<>();

        for (Map<String, String> p : core) {
            for (String s : set) {
                HashMap<String, String> n = new HashMap<>(p);
                n.put(variable, s);
                result.add(n);
            }
        }

        return result;
    }

    /**
     * Handles the set:{} macro
     *
     * @param expr the expr to assign to a variable
     * @return a set of strings made from splitting the values in set:{}, or expr if expr is not the set macro
     */
    private String[] splitSet(String expr) {
        String[] set;

        if (expr.contains("set:{")) {
            expr = expr.substring(5, expr.length() - 1);
            set = expr.split(",");
        } else {
            set = new String[]{expr};
        }

        return set;
    }

    /**
     * For a given state in the state chart, every transition out from that state is checked against every possible
     * variable assignment; those combinations satisfying the transition condition are added to the bootstrap list
     *
     * @param nextState the state whose transitions are checked and which was expanded to get the variable assignments
     * @param product a list of variable assignment maps
     * @param negativeVariable the variable with a negative value, or null
     * @param bootStrap the bootstrap list
     */
    private void checkTransactions(TransitionTarget nextState, List<Map<String, String>> product,
                                   String negativeVariable, List<NegPossibleState> bootStrap) {
        //go through every transition and see which of the products are valid, adding them to the list
        List<Transition> transitions = nextState.getTransitionsList();

        for (Transition transition : transitions) {
            String condition = transition.getCond();
            TransitionTarget target = ((List<TransitionTarget>) transition.getTargets()).get(0);

            for (Map<String, String> p : product) {
                //transition condition satisfied, add to bootstrap list
                if (checkTransaction(p, condition, negativeVariable)) {
                    NegPossibleState result = new NegPossibleState(target, p, negativeVariable);
                    bootStrap.add(result);
                }
            }
        }
    }

    /**
     * Checks if a variable assignment satisfies a condition for a transition
     * Conditions ivolving the negative variable are ignored
     *
     * @param variables the assignments
     * @param condition the condition
     * @param negativeVariable the negative variable
     */
    private Boolean checkTransaction(Map<String, String> variables, String condition, String negativeVariable) {
        Boolean pass;

        //condition must exist and not concern the negative variable
        if (condition == null || (negativeVariable != null && condition.contains(negativeVariable))) {
            pass = true;
        } else {
            //scrub the context clean so we may use it to evaluate transition conditional
            Context context = this.getRootContext();
            context.reset();

            //set up new context
            for (Map.Entry<String, String> e : variables.entrySet()) {
                context.set(e.getKey(), e.getValue());
            }

            //evaluate condition
            try {
                pass = (Boolean) this.getEvaluator().eval(context, condition);
            } catch (SCXMLExpressionException ex) {
                pass = false;
            }
        }

        return pass;
    }

    private void expandPositive(NegPossibleState state, List<NegPossibleState> bootStrap) {
        TransitionTarget nextState = state.nextState;
        OnEntry entry = nextState.getOnEntry();
        List<Action> actions = entry.getActions();

        //set every variable with cartesian product of 'assign' actions
        List<Map<String, String>> product = new LinkedList<>();
        product.add(new HashMap<>(state.variables));

        for (Action action : actions) {
            if (action instanceof Assign) {
                String expr = ((Assign) action).getExpr();
                String variable = ((Assign) action).getName();
                product = takeProduct(product, variable, splitSet(expr));
            }
        }

        checkTransactions(nextState, product, state.negVariable, bootStrap);
    }

    private void expandNegative(NegPossibleState state, List<NegPossibleState> bootStrap) {
        TransitionTarget nextState = state.nextState;
        OnEntry entry = nextState.getOnEntry();
        List<Action> actions = entry.getActions();

        //determine which variables have positive assignments, negative assignments, or both
        Set<String> positiveOnlyVariables = new HashSet<>();
        Set<String> hasNegativeVariables = new HashSet<>();
        Map<String, Assign> positiveAssignments = new HashMap<>();
        Map<String, NegativeAssign> negativeAssignments = new HashMap<>();

        for (Action action : actions) {
            if (action instanceof Assign) {
                String variable = ((Assign) action).getName();
                positiveAssignments.put(variable, (Assign) action);
                positiveOnlyVariables.add(variable);
            } else if (action instanceof NegativeAssign) {
                String variable = ((NegativeAssign) action).getName();
                negativeAssignments.put(variable, (NegativeAssign) action);
                hasNegativeVariables.add(variable);
            }
        }

        for (String variable : hasNegativeVariables) {
            positiveOnlyVariables.remove(variable);
        }

        //produce core cartesian product from variables with only positive assignments
        List<Map<String, String>> productCore = new LinkedList<>();
        productCore.add(new HashMap<>(state.variables));

        for (String variable : positiveOnlyVariables) {
            Assign assign = positiveAssignments.get(variable);
            String expr = assign.getExpr();
            productCore = takeProduct(productCore, variable, splitSet(expr));
        }

        //only one negative assignment per negative scenario
        for (String variable : hasNegativeVariables) {
            //make the negative assignment
            NegativeAssign neg = negativeAssignments.get(variable);
            String expr = neg.getExpr();
            List<Map<String, String>> product = takeProduct(productCore, variable, splitSet(expr));

            //make all other potentially negative assignments positive
            for (String variable2 : hasNegativeVariables) {
                if (!variable2.equals(variable)) {
                    Assign assign = positiveAssignments.get(variable2);
                    expr = assign.getExpr();
                    product = takeProduct(product, variable2, splitSet(expr));
                }
            }

            checkTransactions(nextState, product, variable, bootStrap);
        }
    }

    /**
     * Performs a partial BFS on model until the search frontier reaches the desired bootstrap size
     *
     * @param min the desired bootstrap size
     * @return a list of found PossibleState
     * @throws ModelException if the desired bootstrap can not be reached
     */
    public List<NegPossibleState> bfs(int min) throws ModelException {
        List<NegPossibleState> bootStrap = new LinkedList<>();

        TransitionTarget initial = model.getInitialTarget();
        NegPossibleState initialState = new NegPossibleState(initial, fillInitialVariables(), null);
        bootStrap.add(initialState);

        while (bootStrap.size() < min) {
            NegPossibleState state = bootStrap.remove(0);

            if (state.nextState.getId().equalsIgnoreCase("end")) {
                throw new ModelException("Could not achieve required bootstrap without reaching end state");
            }

            //produce purely positive scenarios
            expandPositive(state, bootStrap);

            //possible state has no negative value yet, produce scenarios with one
            if (state.negVariable == null) {
                expandNegative(state, bootStrap);
            }
        }

        return bootStrap;
    }

    /**
     * Performs the BFS and gives the results to a distributor to distribute
     *
     * @param distributor the distributor
     */
    public void process(SearchDistributor distributor) {
        List<NegPossibleState> bootStrap;
        try {
            bootStrap = bfs(bootStrapMin);
        } catch (ModelException e) {
            bootStrap = new LinkedList<>();
        }

        List<Frontier> frontiers = new LinkedList<>();
        for (NegPossibleState p : bootStrap) {
            NegSCXMLFrontier dge = new NegSCXMLFrontier(p, model);
            frontiers.add(dge);
        }

        distributor.distribute(frontiers);
    }

    private List<CustomAction> customActions() {
        List<CustomAction> actions = new LinkedList<>();
        CustomAction neg = new CustomAction("org.finra.datagenerator", "negative", NegativeAssign.class);
        actions.add(neg);
        CustomAction pos = new CustomAction("org.finra.datagenerator", "positive", Assign.class);
        actions.add(pos);
        return actions;
    }

    /**
     * Sets the SCXML model with an InputStream
     *
     * @param inputFileStream the model input stream
     */
    public void setModelByInputFileStream(InputStream inputFileStream) {
        try {
            this.model = SCXMLParser.parse(new InputSource(inputFileStream), null, customActions());
            this.setStateMachine(this.model);
        } catch (IOException | SAXException | ModelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the SCXML model with a string
     *
     * @param model the model text
     */
    public void setModelByText(String model) {
        try {
            InputStream is = new ByteArrayInputStream(model.getBytes());
            this.model = SCXMLParser.parse(new InputSource(is), null, customActions());
            this.setStateMachine(this.model);
        } catch (IOException | SAXException | ModelException e) {
            e.printStackTrace();
        }
    }

    /**
     * bootstrapMin setter
     *
     * @param min sets the desired bootstrap min
     * @return this
     */
    public Engine setBootstrapMin(int min) {
        bootStrapMin = min;
        return this;
    }
}

