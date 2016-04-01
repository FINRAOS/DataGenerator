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

import org.apache.commons.scxml.model.Action;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @param <A> the action class handled by this class' filerPossibleStates logic
 */
public interface CustomTagExtension<A extends Action> extends Serializable {

    /**
     * Returns a class object representing the Action class handled by this class' pipelinePossibleStates logic
     *
     * @return a class object
     */
    Class<A> getTagActionClass();

    /**
     * Returns the name of this custom tag, as would be used in the model
     *
     * @return the custom tag name
     */
    String getTagName();

    /**
     * Returns the name space of this custom tag, as would be used in the model
     *
     * @return the custom tag name space
     */
    String getTagNameSpace();

    /**
     * Given an appropriate Action object for a tag in an scxml model, performs arbitrary logic on a list of
     * possible states.
     *
     * @param action an Action of the type handled by this class
     * @param possibleStateList a current list of possible states produced so far from expanding a model state
     *
     * @return a new list of possible states
     */
    List<Map<String, String>> pipelinePossibleStates(A action, List<Map<String, String>> possibleStateList);
}
