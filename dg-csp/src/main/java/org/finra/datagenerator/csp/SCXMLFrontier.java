package org.finra.datagenerator.csp;

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

import java.lang.Boolean;
import java.lang.String;
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

    private PossibleState root;

    public SCXMLFrontier(PossibleState possibleState, SCXML model) {
        root = possibleState;

        this.setStateMachine(model);

        ELEvaluator elEvaluator = new ELEvaluator();
        ELContext context = new ELContext();

        this.setEvaluator(elEvaluator);
        this.setRootContext(context);
    }

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

}
