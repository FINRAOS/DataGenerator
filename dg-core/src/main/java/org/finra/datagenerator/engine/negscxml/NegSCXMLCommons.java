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
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.Assign;
import org.apache.commons.scxml.model.OnEntry;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Marshall Peters
 * Date: 9/10/14
 */
public class NegSCXMLCommons extends SCXMLExecutor {

    /**
     * Expands a list of current variable assignments, adding on an additional variable which is assigned values
     * from a String array; produced all possible combinations of current variable assignments and values
     * for the new variable
     * <p/>
     * A cartesian product of core with set in essence
     *
     * @param core     the set of current variable assignments
     * @param variable the new variable
     * @param set      the set of values variable will take on
     * @return the product
     */
    public List<Map<String, String>> takeProduct(List<Map<String, String>> core, String variable, String[] set) {
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
    public String[] splitSet(String expr) {
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
     * @param nextState         the state whose transitions are checked and which was
     *                          expanded to get the variable assignments
     * @param product           a list of variable assignment maps
     * @param negativeVariables the variables with a negative value, or an empty set
     * @param bootStrap         the bootstrap list
     */
    public void checkTransactions(TransitionTarget nextState, List<Map<String, String>> product,
                                   Set<String> negativeVariables, List<NegPossibleState> bootStrap) {
        //go through every transition and see which of the products are valid, adding them to the list
        List<Transition> transitions = nextState.getTransitionsList();

        for (Transition transition : transitions) {
            String condition = transition.getCond();
            TransitionTarget target = ((List<TransitionTarget>) transition.getTargets()).get(0);

            for (Map<String, String> p : product) {
                //transition condition satisfied, add to bootstrap list
                if (checkTransaction(p, condition, negativeVariables)) {
                    NegPossibleState result = new NegPossibleState(target, p, negativeVariables);
                    bootStrap.add(result);
                }
            }
        }
    }

    /**
     * Checks if a variable assignment satisfies a condition for a transition
     * Conditions involving the negative variables are auto satisfied/ignored
     *
     * @param variables         the assignments
     * @param condition         the condition
     * @param negativeVariables the negative variables
     * @return Boolean true if condition passes, false otherwise
     */
    public Boolean checkTransaction(Map<String, String> variables, String condition, Set<String> negativeVariables) {
        Boolean pass;

        //condition must exist
        if (condition == null) {
            pass = true;
        } else {
            //condition must not concern one of the negative variables
            for (String negativeVariable : negativeVariables) {
                if (condition.contains(negativeVariable)) {
                    pass = true;
                    return pass;
                }
            }

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


    /**
     * Expands a NegPossibleState using only the positive scenarios/assign statements
     *
     * @param state the NegPossibleState to expand
     * @param bootStrap the list to add new NegPossibleStates found by expansion
     */
    public void expandPositive(NegPossibleState state, List<NegPossibleState> bootStrap) {
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

    /**
     * Expands a NegPossibleState using the negative scenarios as well, multiple times for different numbers
     * and combinations of negative scenarios
     *
     * @param state the NegPossibleState to expand
     * @param bootStrap the list to add new NegPossibleStates found by expansion
     * @param negDegreesFreedom the max number of negative scenarios this expansion can add
     */
    public void expandNegative(NegPossibleState state, List<NegPossibleState> bootStrap, int negDegreesFreedom) {
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

        //produce the negative assignments from the positive core, from 1 assignment to negDegreesFreedom assignments
        for (int n = 1; n <= negDegreesFreedom; n++) {
            arbitraryExpandedNegative(nextState, n, productCore, negativeAssignments, positiveAssignments,
                    state.negVariable, bootStrap);
        }
    }

    /**
     * Finds all the ways to choose n variables from a list of variables with negative assignments
     * For each way to choose it finds all possible assignments, checks against transitions, and adds to bootstrap
     *
     * @param nextState the state being expanded
     * @param n the number of negative assignments to make
     * @param productCore the cartesian product of assignments on purely positive variables
     * @param negativeAssignments negative assignments of variables that have negative assignments
     * @param positiveAssignments positive assignments of variables
     * @param negativeVariables set of variables already set to negative by prior searching
     * @param bootStrap the list of bootstrap NegPossibleStates
     */
    public void arbitraryExpandedNegative(TransitionTarget nextState, int n, List<Map<String, String>> productCore,
                                           Map<String, NegativeAssign> negativeAssignments,
                                           Map<String, Assign> positiveAssignments, Set<String> negativeVariables,
                                           List<NegPossibleState> bootStrap) {

        //find all combinations of choosing n negative variables in negativeAssignments
        List<Set<String>> completeChoices = new LinkedList<Set<String>>();
        List<Set<String>> incompleteChoices = new LinkedList<Set<String>>();
        Set<String> emptyChoice = new HashSet<String>();
        incompleteChoices.add(emptyChoice);

        for (String variable : negativeAssignments.keySet()) {
            List<Set<String>> newChoices = new LinkedList<Set<String>>();

            for (Set<String> choice : incompleteChoices) {
                Set<String> newChoice = new HashSet<>(choice);
                newChoice.add(variable);

                if (newChoice.size() == n) {
                    completeChoices.add(newChoice);
                } else {
                    newChoices.add(newChoice);
                }
            }

            for (Set<String> choice : newChoices) {
                incompleteChoices.add(choice);
            }
        }

        //for each choice of n negative variables, make the needed variable assignments
        for (Set<String> choice : completeChoices) {
            List<Map<String, String>> product = new LinkedList<>(productCore);
            Set<String> newlyNegativeVariables = new HashSet<>(negativeVariables);

            //make the negative assignments
            for (String variable : choice) {
                NegativeAssign neg = negativeAssignments.get(variable);
                String expr = neg.getExpr();
                product = takeProduct(product, variable, splitSet(expr));
                newlyNegativeVariables.add(variable);
            }

            //make all other potentially negative assignments positive
            for (String variable2 : negativeAssignments.keySet()) {
                if (!choice.contains(variable2)) {
                    Assign assign = positiveAssignments.get(variable2);
                    String expr = assign.getExpr();
                    product = takeProduct(product, variable2, splitSet(expr));
                }
            }

            checkTransactions(nextState, product, newlyNegativeVariables, bootStrap);
        }
    }
}
