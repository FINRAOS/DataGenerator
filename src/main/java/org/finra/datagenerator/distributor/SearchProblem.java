package org.finra.datagenerator.distributor;

import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.finra.datagenerator.scxml.PossibleState;

/**
 * Created by robbinbr on 3/14/14.
 */
public class SearchProblem {

    protected static final Logger log = Logger.getLogger(SearchProblem.class);
    private static final Gson gson = new Gson();

    private PossibleState initialState;
    private Map<String, Set<String>> varsOut;
    private Map<String, String> initialVariablesMap;
    private List<String> initialEventsList;
    private int totalNumberOfProblems;
    private int thisProblemIndex;

    public static SearchProblem fromJson(String json) {
        return gson.fromJson(json, SearchProblem.class);
    }

    public SearchProblem(PossibleState initialState, Map<String, Set<String>> varsOut, Map<String, String> initialVariablesMap, List<String> initialEventsList,
            int totalNumberOfProblems, int thisProblemIndex) {
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

    public Map<String, Set<String>> getVarsOut() {
        return varsOut;
    }

    public Map<String, String> getInitialVariablesMap() {
        return initialVariablesMap;
    }

    public List<String> getInitialEventsList() {
        return initialEventsList;
    }

    public String toJson() {
        return gson.toJson(this);
    }
}
