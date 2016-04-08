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
import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.consumer.DataTransformer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Implementation of dg:transform tag
 */
public class InLineTransformerExtension implements CustomTagExtension<InLineTransformerExtension.TransformTag> {

    private Map<String, DataTransformer> transformers;

    /**
     * Constructor
     *
     * @param transformers a map of DataTransformers from their names in the model
     */
    public InLineTransformerExtension(final Map<String, DataTransformer> transformers) {
        this.transformers = transformers;
    }

    public Class<TransformTag> getTagActionClass() {
        return TransformTag.class;
    }

    public String getTagName() {
        return "transform";
    }

    public String getTagNameSpace() {
        return "org.finra.datagenerator";
    }

    /**
     * Applies a stated DataTransformer (given by name in a TransformTag Action) against every possible state
     *
     * @param action a TransformTag Action
     * @param possibleStateList a current list of possible states produced so far from expanding a model state
     *
     * @return the same list of possible states, each processed with the stated DataTransformer
     */
    public List<Map<String, String>> pipelinePossibleStates(TransformTag action,
                                                            List<Map<String, String>> possibleStateList) {
        DataTransformer tr = transformers.get(action.getName());
        DataPipe pipe = new DataPipe(0, null);

        for (Map<String, String> possibleState : possibleStateList) {
            pipe.getDataMap().putAll(possibleState);
            tr.transform(pipe);
            possibleState.putAll(pipe.getDataMap());
        }

        return possibleStateList;
    }

    /**
     * A custom Action for the 'transform' tag inside models
     */
    public static class TransformTag extends Action {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
        public void execute(EventDispatcher eventDispatcher, ErrorReporter errorReporter, SCInstance scInstance, Log log,
                            Collection collection) throws ModelException, SCXMLExpressionException {
            //Handled manually
        }
    }

}
