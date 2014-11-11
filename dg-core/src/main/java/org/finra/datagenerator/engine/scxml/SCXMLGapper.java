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

import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.CustomAction;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.TransitionTarget;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Provides serialization tools for SCXMLFrontier.
 *
 * Marshall Peters
 * Date: 8/28/14
 */
public class SCXMLGapper {

    private SCXML model;

    private void setModel(String model, List<CustomTagExtension> tagExtensionList) {
        List<CustomAction> customActions = new ArrayList<>();

        for (CustomTagExtension tagExtension : tagExtensionList) {
            if (!tagExtension.getTagName().equals("assign")) {
                CustomAction action = new CustomAction(tagExtension.getTagNameSpace(), tagExtension.getTagName(),
                        tagExtension.getTagActionClass());
                customActions.add(action);
            }
        }

        try {
            InputStream is = new ByteArrayInputStream(model.getBytes());
            this.model = SCXMLParser.parse(new InputSource(is), null, customActions);
        } catch (IOException | SAXException | ModelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes a model and an SCXMLFrontier and decomposes the Frontier into a Map of Strings to Strings
     * These strings can be sent over a network to get a Frontier past a 'gap'
     *
     * @param frontier  the Frontier
     * @param modelText the model
     * @return the map of strings representing a decomposition
     */
    public Map<String, String> decompose(Frontier frontier, String modelText) {
        if (!(frontier instanceof SCXMLFrontier)) {
            return null;
        }

        TransitionTarget target = ((SCXMLFrontier) frontier).getRoot().nextState;
        Map<String, String> variables = ((SCXMLFrontier) frontier).getRoot().variables;

        Map<String, String> decomposition = new HashMap<String, String>();
        decomposition.put("target", target.getId());

        StringBuilder packedVariables = new StringBuilder();
        for (Map.Entry<String, String> variable : variables.entrySet()) {
            packedVariables.append(variable.getKey());
            packedVariables.append("::");
            packedVariables.append(variable.getValue());
            packedVariables.append(";");
        }

        decomposition.put("variables", packedVariables.toString());
        decomposition.put("model", modelText);

        return decomposition;
    }

    /**
     * Produces an SCXMLFrontier by reversing a decomposition; the model text is bundled into the decomposition.
     *
     * @param decomposition the decomposition, assembled back into a map
     * @return a rebuilt SCXMLFrontier
     */
    public Frontier reproduce(Map<String, String> decomposition) {
        return reproduce(decomposition, new LinkedList<CustomTagExtension>());
    }

    /**
     * Produces an SCXMLFrontier by reversing a decomposition; the model text is bundled into the decomposition.
     *
     * @param decomposition the decomposition, assembled back into a map
     * @param tagExtensionList custom tags to use in the model
     * @return a rebuilt SCXMLFrontier
     */
    public Frontier reproduce(Map<String, String> decomposition, List<CustomTagExtension> tagExtensionList) {
        tagExtensionList = new LinkedList<>(tagExtensionList);
        tagExtensionList.add(new SetAssignExtension());
        tagExtensionList.add(new SingleValueAssignExtension());

        setModel(decomposition.get("model"), tagExtensionList);
        TransitionTarget target = (TransitionTarget) model.getTargets().get(decomposition.get("target"));

        Map<String, String> variables = new HashMap<>();
        String[] assignments = decomposition.get("variables").split(";");
        for (int i = 0; i < assignments.length; i++) {
            String[] a = assignments[i].split("::");
            if (a.length == 2) {
                variables.put(a[0], a[1]);
            } else {
                variables.put(a[0], "");
            }
        }

        return new SCXMLFrontier(new PossibleState(target, variables), model, tagExtensionList);
    }
}
