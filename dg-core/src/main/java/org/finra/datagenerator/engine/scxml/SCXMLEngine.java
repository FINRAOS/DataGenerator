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
import org.finra.datagenerator.engine.scxml.tags.CustomTagExtension;
import org.finra.datagenerator.engine.scxml.tags.SetAssignExtension;
import org.finra.datagenerator.engine.scxml.tags.SingleValueAssignExtension;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Engine implementation for generating data with SCXML state machine models.
 * <p/>
 * Marshall Peters
 * Date: 8/25/14
 */
public class SCXMLEngine extends SCXMLExecutor implements Engine {

    private SCXML model;
    private int bootStrapMin;
    private List<CustomTagExtension> tagExtensionList;

    /**
     * Constructor
     */
    public SCXMLEngine() {
        super();

        tagExtensionList = new LinkedList<>();
        tagExtensionList.add(new SetAssignExtension());
        tagExtensionList.add(new SingleValueAssignExtension());

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
                } else if (action instanceof SetAssignExtension.SetAssignTag) {
                    String variable = ((SetAssignExtension.SetAssignTag) action).getName();
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
     * Performs a partial BFS on model until the search frontier reaches the desired bootstrap size
     *
     * @param min the desired bootstrap size
     * @return a list of found PossibleState
     * @throws ModelException if the desired bootstrap can not be reached
     */
    public List<PossibleState> bfs(int min) throws ModelException {
        List<PossibleState> bootStrap = new LinkedList<>();

        TransitionTarget initial = model.getInitialTarget();
        PossibleState initialState = new PossibleState(initial, fillInitialVariables());
        bootStrap.add(initialState);

        while (bootStrap.size() < min) {
            PossibleState state = bootStrap.remove(0);
            TransitionTarget nextState = state.nextState;

            if (nextState.getId().equalsIgnoreCase("end")) {
                throw new ModelException("Could not achieve required bootstrap without reaching end state");
            }

            //run every action in series
            List<Map<String, String>> product = new LinkedList<>();
            product.add(new HashMap<>(state.variables));

            OnEntry entry = nextState.getOnEntry();
            List<Action> actions = entry.getActions();

            for (Action action : actions) {
                for (CustomTagExtension tagExtension : tagExtensionList) {
                    if (tagExtension.getTagActionClass().isInstance(action)) {
                        product = tagExtension.pipelinePossibleStates(action, product);
                    }
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

    /**
     * Performs the BFS and gives the results to a distributor to distribute
     *
     * @param distributor the distributor
     */
    public void process(SearchDistributor distributor) {
        List<PossibleState> bootStrap;
        try {
            bootStrap = bfs(bootStrapMin);
        } catch (ModelException e) {
            bootStrap = new LinkedList<>();
        }

        List<Frontier> frontiers = new LinkedList<>();
        for (PossibleState p : bootStrap) {
            SCXMLFrontier dge = new SCXMLFrontier(p, model, tagExtensionList);
            frontiers.add(dge);
        }

        distributor.distribute(frontiers);
    }

    private List<CustomAction> customActionsFromTagExtensions() {
        List<CustomAction> customActions = new ArrayList<>();

        for (CustomTagExtension tagExtension : tagExtensionList) {
            if (!tagExtension.getTagName().equals("assign")) {
                CustomAction action = new CustomAction(tagExtension.getTagNameSpace(), tagExtension.getTagName(),
                        tagExtension.getTagActionClass());
                customActions.add(action);
            }
        }

        return customActions;
    }

    /**
     * Sets the SCXML model with an InputStream
     *
     * @param inputFileStream the model input stream
     */
    public void setModelByInputFileStream(InputStream inputFileStream) {
        try {
            this.model = SCXMLParser.parse(new InputSource(inputFileStream), null, customActionsFromTagExtensions());
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
            this.model = SCXMLParser.parse(new InputSource(is), null, customActionsFromTagExtensions());
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

    /**
     * Adds a custom tag extension to this engine for use in model parsing and processing. Custom tags should be added
     * before the model is set.
     *
     * @param tagExtension the extension to add
     */
    public void addTagExtension(CustomTagExtension tagExtension) {
        this.tagExtensionList.add(tagExtension);
    }
}
