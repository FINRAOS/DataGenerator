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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.ModelException;
import org.finra.datagenerator.exceptions.NullActionException;
import org.finra.datagenerator.exceptions.NullSetException;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Implementation of dg:assign tag
 */
public class SetAssignExtension implements CustomTagExtension<SetAssignExtension.SetAssignTag> {

    public Class<SetAssignTag> getTagActionClass() {
        return SetAssignTag.class;
    }

    public String getTagName() {
        return "assign";
    }

    public String getTagNameSpace() {
        return "org.finra.datagenerator";
    }

    /**
     * Performs variable assignments from a set of values
     *
     * @param action            a SetAssignTag Action
     * @param possibleStateList a current list of possible states produced so far from expanding a model state
     * @return the cartesian product of every current possible state and the set of values specified by action
     */
    public List<Map<String, String>> pipelinePossibleStates(SetAssignTag action,
                                                            List<Map<String, String>> possibleStateList) {
        if (action == null) {
            throw new NullActionException("Called with a null action, and possibleStateList = "
                    + possibleStateList.toString());
        }

        String variable = action.getName();
        String set = action.getSet();

        String[] domain;

        if (set == null) {
            throw new NullSetException("Called with a null set, action name=" + action.getName()
                    + " and possibleStateList = " + possibleStateList.toString());
        }

        if (StringUtils.splitByWholeSeparator(set, action.getSeparator()).length == 0) {
            domain = new String[]{""};
        } else {
            domain = StringUtils.splitByWholeSeparator(set, action.getSeparator());
        }

        //take the product
        List<Map<String, String>> productTemp = new LinkedList<>();
        for (Map<String, String> p : possibleStateList) {
            for (String value : domain) {
                HashMap<String, String> n = new HashMap<>(p);
                n.put(variable, value);
                productTemp.add(n);
            }
        }

        return productTemp;
    }

    /**
     * A custom Action for the 'dg:assign' tag inside models
     */
    public static class SetAssignTag extends Action {

        private String name;
        private String set;
        private String separator = ",";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSet() {
            return set;
        }

        public void setSet(String set) {
            this.set = set;
        }

        public String getSeparator() {
            return this.separator;
        }

        public void setSeparator(String sep) {
            this.separator = sep;
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
        public void execute(EventDispatcher eventDispatcher, ErrorReporter errorReporter, SCInstance scInstance,
                            Log log, Collection collection) throws ModelException, SCXMLExpressionException {
            //Handled manually
        }
    }

}
