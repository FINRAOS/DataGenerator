package org.finra.datagenerator.csp;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.env.jsp.ELContext;
import org.apache.commons.scxml.env.jsp.ELEvaluator;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.Assign;
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
import java.lang.Boolean;import java.lang.String;import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Marshall Peters
 * Date: 8/25/14
 */
public class SCXMLEngine extends SCXMLExecutor implements Engine {

    private SCXML model;
    private int bootStrapMin;

    public SCXMLEngine() {
        super();

        ELEvaluator elEvaluator = new ELEvaluator();
        ELContext context = new ELContext();

        this.setEvaluator(elEvaluator);
        this.setRootContext(context);
    }

    public List<PossibleState> bfs(int min) throws ModelException {
        List<PossibleState> bootStrap = new LinkedList<>();

        TransitionTarget initial = model.getInitialTarget();
        PossibleState initialState = new PossibleState(initial, new HashMap<String, String>());
        bootStrap.add(initialState);

        while (bootStrap.size() < min) {
            PossibleState state = bootStrap.remove(0);
            TransitionTarget nextState = state.nextState;

            if (nextState.getId().equalsIgnoreCase("end")) {
                throw new ModelException("Could not achieve required bootstrap without reaching end state");
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

            //go through every transition and see which of the products are valid, adding them to the list
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

                    //transition condition satisfied, add to bootstrap list
                    if (pass) {
                        PossibleState result = new PossibleState(target, p);
                        bootStrap.add(result);
                    }
                }
            }
        }

        return bootStrap;
    }

    public void process(SearchDistributor distributor) {
        List<PossibleState> bootStrap;
        try {
            bootStrap = bfs(bootStrapMin);
        } catch (ModelException e) {
            bootStrap = new LinkedList<>();
        }

        List<Frontier> frontiers = new LinkedList<>();
        for (PossibleState p: bootStrap) {
            SCXMLFrontier dge = new SCXMLFrontier(p, model);
            frontiers.add(dge);
        }

        distributor.distribute(frontiers);
    }

    public void setModelByInputFileStream(InputStream inputFileStream) {
        try {
            this.model = SCXMLParser.parse(new InputSource(inputFileStream), null);
            this.setStateMachine(this.model);
        } catch (IOException|SAXException|ModelException e) {
            e.printStackTrace();
        }
    }

    public void setModelByText(String model) {
        try {
            InputStream is = new ByteArrayInputStream(model.getBytes());
            this.model = SCXMLParser.parse(new InputSource(is), null);
            this.setStateMachine(this.model);
        } catch (IOException|SAXException|ModelException e) {
            e.printStackTrace();
        }
    }

    public Engine setBootstrapMin(int min) {
        bootStrapMin = min;
        return this;
    }
}
