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

import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.CustomAction;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.TransitionTarget;
import org.finra.datagenerator.consumer.DataTransformer;
import org.finra.datagenerator.engine.Frontier;
import org.finra.datagenerator.engine.scxml.Transform;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
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
public class NegSCXMLGapper {

    private SCXML model;

    private List<CustomAction> customActions() {
        List<CustomAction> actions = new LinkedList<>();
        CustomAction tra = new CustomAction("org.finra.datagenerator", "transform", Transform.class);
        actions.add(tra);
        CustomAction neg = new CustomAction("org.finra.datagenerator", "negative", NegativeAssign.class);
        actions.add(neg);
        return actions;
    }

    private void setModel(String model) {
        try {
            InputStream is = new ByteArrayInputStream(model.getBytes());
            this.model = SCXMLParser.parse(new InputSource(is), null, customActions());
        } catch (IOException | SAXException | ModelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes a model and a NegSCXMLFrontier and decomposes the Frontier into a Map of Strings to Strings
     * These strings can be sent over a network to get a Frontier past a 'gap'
     *
     * @param frontier  the Frontier
     * @param modelText the model
     * @return the map of strings representing a decomposition
     */
    public Map<String, String> decompose(Frontier frontier, String modelText) {
        if (!(frontier instanceof NegSCXMLFrontier)) {
            return null;
        }

        setModel(modelText);

        TransitionTarget target = ((NegSCXMLFrontier) frontier).getRoot().nextState;
        Map<String, String> variables = ((NegSCXMLFrontier) frontier).getRoot().variables;
        Set<String> negVariables = ((NegSCXMLFrontier) frontier).getRoot().negVariable;
        int negative = ((NegSCXMLFrontier) frontier).getNegative();

        Map<String, String> decomposition = new HashMap<String, String>();
        decomposition.put("negative", String.valueOf(negative));
        decomposition.put("target", target.getId());

        StringBuilder packedVariables = new StringBuilder();
        for (Map.Entry<String, String> variable : variables.entrySet()) {
            packedVariables.append(variable.getKey());
            packedVariables.append("::");
            packedVariables.append(variable.getValue());
            packedVariables.append(";");
        }
        decomposition.put("variables", packedVariables.toString());

        packedVariables = new StringBuilder();
        for (String variable : negVariables) {
            packedVariables.append(variable);
            packedVariables.append(";");
        }
        decomposition.put("negVariables", packedVariables.toString());

        decomposition.put("model", modelText);

        return decomposition;
    }

    /**
     * Produces a NegSCXMLFrontier by reversing a decomposition; the model text is bundled into the decomposition.
     *
     * @param decomposition the decomposition, assembled back into a map
     * @param transformers in model DataTransformers
     * @return a rebuilt NegSCXMLFrontier
     */
    public Frontier reproduce(Map<String, String> decomposition, Map<String, DataTransformer> transformers) {
        setModel(decomposition.get("model"));
        int negative = Integer.valueOf(decomposition.get("negative"));
        TransitionTarget target = (TransitionTarget) model.getTargets().get(decomposition.get("target"));

        Map<String, String> variables = new HashMap<>();
        String[] assignments = decomposition.get("variables").split(";");
        for (String assignment : assignments) {
            String[] a = assignment.split("::");
            if (a.length == 2) {
                variables.put(a[0], a[1]);
            } else {
                variables.put(a[0], "");
            }
        }

        Set<String> negVariables = new HashSet<>();
        String[] negList = decomposition.get("negVariables").split(";");
        Collections.addAll(negVariables, negList);

        return new NegSCXMLFrontier(new NegPossibleState(target, variables, negVariables),
                model, negative, transformers);
    }

    /**
     * Produces a NegSCXMLFrontier by reversing a decomposition; the model text is bundled into the decomposition.
     *
     * @param decomposition the decomposition, assembled back into a map
     * @return a rebuilt NegSCXMLFrontier
     */
    public Frontier reproduce(Map<String, String> decomposition) {
        return this.reproduce(decomposition, new HashMap<String, DataTransformer>());
    }
}
