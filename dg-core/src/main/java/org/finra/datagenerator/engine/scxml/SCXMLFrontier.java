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

package org.finra.datagenerator.engine.scxml;

import com.google.gson.Gson;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.env.jsp.ELContext;
import org.apache.commons.scxml.env.jsp.ELEvaluator;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.Assign;
import org.apache.commons.scxml.model.OnEntry;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.finra.datagenerator.engine.Frontier;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Marshall Peters
 * Date: 8/26/14
 */
public class SCXMLFrontier extends SCXMLExecutor implements Frontier {

    private final PossibleState root;

    private final Gson gson;

    /**
     * Constructor
     *
     * @param possibleState the root node of the model and partial variable assignment to start a dfs from
     * @param model the model text
     */
    public SCXMLFrontier(final PossibleState possibleState, final SCXML model) {
        root = possibleState;

        this.setStateMachine(model);

        ELEvaluator elEvaluator = new ELEvaluator();
        ELContext context = new ELContext();

        this.setEvaluator(elEvaluator);
        this.setRootContext(context);

        gson = new Gson();
    }

    /**
     * Performs a DFS on the model, starting from root, placing results in the queue
     * Just a public wrapper for private dfs function
     *
     * @param queue the results queue
     * @param flag used to stop the search before completion
     */
    public void searchForScenarios(Queue<Map<String, String>> queue, AtomicBoolean flag) {
        dfs(queue, flag, root);
    }

    private void dfs(Queue<Map<String, String>> queue, AtomicBoolean flag, PossibleState state) {
        if (flag.get()) {
            return;
        }

        TransitionTarget nextState = state.nextState;

        //reached end of chart, valid assignment found
        if (nextState.getId().equalsIgnoreCase("end")) {
            queue.add(state.variables);
            return;
        }

        //set every variable with cartesian product of 'assign' actions
        List<Map<String, String>> product = new LinkedList<>();
        product.add(new HashMap<>(state.variables));

        OnEntry entry = nextState.getOnEntry();
        List<Action> actions = entry.getActions();

        for (Action action : actions) {
            if (action instanceof Assign) {
                String expr = ((Assign) action).getExpr();
                String variable = ((Assign) action).getName();

                String[] set;

                if (expr.contains("set:{")) {
                    expr = expr.substring(5, expr.length() - 1);
                    set = expr.split(",");
                } else {
                    set = new String[]{expr};
                }

                //take the product
                List<Map<String, String>> productTemp = new LinkedList<>();
                for (Map<String, String> p : product) {
                    for (String s : set) {
                        HashMap<String, String> n = new HashMap<>(p);
                        n.put(variable, s);
                        productTemp.add(n);
                    }
                }
                product = productTemp;
            }
        }

        //go through every transition and see which of the products are valid, recursive searching on them
        List<Transition> transitions = nextState.getTransitionsList();

        for (Transition transition : transitions) {
            String condition = transition.getCond();
            TransitionTarget target = ((List<TransitionTarget>) transition.getTargets()).get(0);

            for (Map<String, String> p : product) {
                Boolean pass;

                if (condition == null) {
                    pass = true;
                } else {
                    //scrub the context clean so we may use it to evaluate transition conditional
                    Context context = this.getRootContext();
                    context.reset();

                    //set up new context
                    for (Map.Entry<String, String> e : p.entrySet()) {
                        context.set(e.getKey(), e.getValue());
                    }

                    //evaluate condition
                    try {
                        pass = (Boolean) this.getEvaluator().eval(context, condition);
                    } catch (SCXMLExpressionException ex) {
                        pass = false;
                    }
                }

                //transition condition satisfied, continue search recursively
                if (pass) {
                    PossibleState result = new PossibleState(target, p);
                    dfs(queue, flag, result);
                }
            }
        }
    }

    /**
     * Produces a new SCXMLFrontier from a json string
     *
     * @param json the frontier json description
     * @return the new SCXMLFrontier
     */
    public Frontier fromJson(String json) {
        return gson.fromJson(json, SCXMLFrontier.class);
    }

    /**
     * Produces a json description of this frontier
     *
     * @return the frontier json description
     */
    public String toJson() {
        return gson.toJson(this);
    }
}
