package org.finra.scxmlexec;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by robbinbr on 3/14/14.
 */


public class SearchProblem {

    protected static final Logger log = Logger.getLogger(SearchProblem.class);
    private static final Gson gson = new Gson();

    private PossibleState initialState;
    private Set<String> varsOut;
    private Map<String, String> initialVariablesMap;
    private List<String> initialEventsList;

    public static SearchProblem fromJson(String json){
        return gson.fromJson(json, SearchProblem.class);
    }

    public SearchProblem(PossibleState initialState, Set<String> varsOut,
                         Map<String, String> initialVariablesMap, List<String> initialEventsList) {
        this.initialState = initialState;
        this.varsOut = varsOut;
        this.initialVariablesMap = initialVariablesMap;
        this.initialEventsList = initialEventsList;
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

    public String toJson(){
        return gson.toJson(this);
    }
}