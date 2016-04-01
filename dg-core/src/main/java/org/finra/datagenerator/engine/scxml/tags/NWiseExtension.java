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
package org.finra.datagenerator.engine.scxml.tags;

import org.apache.commons.logging.Log;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.ModelException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of dg:nwise tag
 */
public class NWiseExtension implements CustomTagExtension<NWiseExtension.NWiseAction> {

    public Class<NWiseAction> getTagActionClass() {
        return NWiseAction.class;
    }

    public String getTagName() {
        return "nwise";
    }

    public String getTagNameSpace() {
        return "org.finra.datagenerator";
    }

    private void makeNWiseTuplesHelper(List<Set<String>> resultTuples, String[] variables,
                                       int startVariable, Set<String> preGivenVariables, int nWise) {
        if (nWise <= 0) {
            Set<String> result = new HashSet<>(preGivenVariables);
            resultTuples.add(result);
            return;
        }

        for (int varIndex = startVariable; varIndex <= variables.length - nWise; varIndex++) {
            preGivenVariables.add(variables[varIndex]);
            makeNWiseTuplesHelper(resultTuples, variables, varIndex + 1, preGivenVariables, nWise - 1);
            preGivenVariables.remove(variables[varIndex]);
        }
    }

    /**
     * Produces all tuples of size n chosen from a list of variable names
     *
     * @param variables the list of variable names to make tuples of
     * @param nWise     the size of the desired tuples
     * @return all tuples of size nWise
     */
    public List<Set<String>> makeNWiseTuples(String[] variables, int nWise) {
        List<Set<String>> completeTuples = new ArrayList<>();
        makeNWiseTuplesHelper(completeTuples, variables, 0, new HashSet<String>(), nWise);

        return completeTuples;
    }

    private void expandTupleHelper(List<Map<String, String>> resultExpansions, String[] variables,
                                   Map<String, String[]> variableDomains, int nextVariable,
                                   Map<String, String> partialAssignment) {
        if (nextVariable >= variables.length) {
            Map<String, String> resultAssignment = new HashMap<>(partialAssignment);
            resultExpansions.add(resultAssignment);
            return;
        }

        String variable = variables[nextVariable];
        for (String domainValue : variableDomains.get(variable)) {
            partialAssignment.put(variable, domainValue);
            expandTupleHelper(resultExpansions, variables, variableDomains, nextVariable + 1, partialAssignment);
        }
    }

    /**
     * Expands a tuple of variable names into every combination of assignments to those variables
     *
     * @param tuple           a list of variables
     * @param variableDomains a map defining the domain for each variable
     * @return the cartesian product of the domains of each variable in the tuple
     */
    public List<Map<String, String>> expandTupleIntoTestCases(Set<String> tuple, Map<String, String[]> variableDomains) {
        List<Map<String, String>> expandedTestCases = new ArrayList<>();
        String[] variables = tuple.toArray(new String[tuple.size()]);
        expandTupleHelper(expandedTestCases, variables, variableDomains, 0, new HashMap<String, String>());

        return expandedTestCases;
    }

    /**
     * Finds all nWise combinations of a set of variables, each with a given domain of values
     *
     * @param nWise           the number of variables in each combination
     * @param coVariables     the varisbles
     * @param variableDomains the domains
     * @return all nWise combinations of the set of variables
     */
    public List<Map<String, String>> produceNWise(int nWise, String[] coVariables, Map<String, String[]> variableDomains) {
        List<Set<String>> tuples = makeNWiseTuples(coVariables, nWise);

        List<Map<String, String>> testCases = new ArrayList<>();
        for (Set<String> tuple : tuples) {
            testCases.addAll(expandTupleIntoTestCases(tuple, variableDomains));
        }

        return testCases;
    }

    /**
     * Uses current variable assignments and info in an NWiseActionTag to expand on an n wise combinatorial set
     *
     * @param action            an NWiseAction Action
     * @param possibleStateList a current list of possible states produced so far from expanding a model state
     * @return every input possible state expanded on an n wise combinatorial set defined by that input possible state
     */
    public List<Map<String, String>> pipelinePossibleStates(NWiseAction action, List<Map<String, String>> possibleStateList) {
        String[] coVariables = action.getCoVariables().split(",");
        int n = Integer.valueOf(action.getN());

        List<Map<String, String>> newPossibleStateList = new ArrayList<>();

        for (Map<String, String> possibleState : possibleStateList) {
            Map<String, String[]> variableDomains = new HashMap<>();
            Map<String, String> defaultVariableValues = new HashMap<>();
            for (String variable : coVariables) {
                String variableMetaInfo = possibleState.get(variable);
                String[] variableDomain = variableMetaInfo.split(",");
                variableDomains.put(variable, variableDomain);
                defaultVariableValues.put(variable, variableDomain[0]);
            }

            List<Map<String, String>> nWiseCombinations = produceNWise(n, coVariables, variableDomains);
            for (Map<String, String> nWiseCombination : nWiseCombinations) {
                Map<String, String> newPossibleState = new HashMap<>(possibleState);
                newPossibleState.putAll(defaultVariableValues);
                newPossibleState.putAll(nWiseCombination);
                newPossibleStateList.add(newPossibleState);
            }
        }

        return newPossibleStateList;
    }

    /**
     * A custom Action for the 'dg:nwise' tag inside models
     */
    public static class NWiseAction extends Action {
        private String coVariables;
        private String n;

        public String getCoVariables() {
            return coVariables;
        }

        public void setCoVariables(String coVariables) {
            this.coVariables = coVariables;
        }

        public String getN() {
            return n;
        }

        public void setN(String n) {
            this.n = n;
        }

        /**
         * Required implementation of an abstract method in Action
         *
         * @param eventDispatcher unused
         * @param errorReporter   unused
         * @param scInstance      unused
         * @param log             unused
         * @param collection      unused
         * @throws ModelException           never
         * @throws SCXMLExpressionException never
         */
        public void execute(EventDispatcher eventDispatcher, ErrorReporter errorReporter, SCInstance scInstance,
                            Log log, Collection collection) throws ModelException, SCXMLExpressionException {

        }
    }
}
