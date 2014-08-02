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
package org.finra.datagenerator.distributor;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.finra.datagenerator.scxml.PossibleState;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by robbinbr on 3/14/14.
 */
public class SearchProblem {

    /**
     * Logger
     */
    protected static final Logger log = Logger.getLogger(SearchProblem.class);

    /**
     * Used for json encoding & decoding
     */
    private static final Gson GSON = new Gson();

    private final PossibleState initialState;
    private final Set<String> varsOut;
    private final Map<String, String> initialVariablesMap;
    private final List<String> initialEventsList;
    private final int totalNumberOfProblems;
    private final int thisProblemIndex;

    /**
     * Decodes a json string and returns a SearchProblem
     *
     * @param json a String containing a json encoded SearchProblem
     * @return a SearchProblem
     */
    public static SearchProblem fromJson(String json) {
        return GSON.fromJson(json, SearchProblem.class);
    }

    /**
     * Constructor
     *
     * @param initialState a PossibleState representing the initial state of the search
     * @param varsOut a set of strings containing the names of the output variables
     * @param initialVariablesMap a map containing the initial values of variables
     * @param initialEventsList a list of events to be fired upon initialization of the state machine
     * @param totalNumberOfProblems if this search problem was the result of a BFS search, this variable should contain
     * the total number of problems found
     * @param thisProblemIndex a zero index of this problem
     */
    public SearchProblem(final PossibleState initialState, final Set<String> varsOut,
            final Map<String, String> initialVariablesMap, final List<String> initialEventsList,
            final int totalNumberOfProblems, final int thisProblemIndex) {
        this.initialState = initialState;
        this.varsOut = varsOut;
        this.initialVariablesMap = initialVariablesMap;
        this.initialEventsList = initialEventsList;
        this.totalNumberOfProblems = totalNumberOfProblems;
        this.thisProblemIndex = thisProblemIndex;
    }

    public int getTotalNumberOfProblems() {
        return totalNumberOfProblems;
    }

    public int getThisProblemIndex() {
        return thisProblemIndex;
    }

    public PossibleState getInitialState() {
        return initialState;
    }

    public Set<String> getVarsOut() {
        return varsOut;
    }

    public Map<String, String> getInitialVariablesMap() {
        return initialVariablesMap;
    }

    public List<String> getInitialEventsList() {
        return initialEventsList;
    }

    /**
     * Searializes this class into json
     *
     * @return a String containing the serialized class
     */
    public String toJson() {
        return GSON.toJson(this);
    }
}
