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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Logger;

/**
 * Yankop Yuriy
 */
public class RangeExtension implements CustomTagExtension<RangeExtension.SetAssignTag> {
    private static final Logger log = Logger.getLogger(RangeExtension.class);

    public Class<SetAssignTag> getTagActionClass() {
        return SetAssignTag.class;
    }

    public String getTagName() {
        return "range";
    }

    public String getTagNameSpace() {
        return "org.finra.datagenerator";
    }

    /**
     * Performs variable assignments from a set of values
     *
     * @param action a SetAssignTag Action
     * @param possibleStateList a current list of possible states produced so far from expanding a model state
     *
     * @return the cartesian product of every current possible state and the set of values specified by action
     */
    public List<Map<String, String>> pipelinePossibleStates(SetAssignTag action,
                                                            List<Map<String, String>> possibleStateList) {
        String variable = action.getName();
        String from = action.getFrom();
        String to = action.getTo();
        String step = action.getStep();

        Set<String> domain = new HashSet<String>();
        if (from != null && from.length() > 0 && to != null && to.length() > 0) {
            generateRangeValues(domain, from, to, step);
        } else {
            log.error("Oops! 'range' parameter isn't setup properly (from = '" + from + "'). ; to = '"  + to + "'; step = '" + step + "'"
                    + "It has be in '<first value>:<last value>[:<optional 'step' value. Default value is '1.0'>]' format. "
                    + "For example, '0.1:2:0.4' or '2:18'");
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

    private void generateRangeValues(Set<String> domain, String from, String to, String step) {
        float currentValue = Float.valueOf(from);
        float lastValue = Float.valueOf(to);
        float stepValue;
        if (step != null && step.length() > 0) {
            stepValue = Float.valueOf(step);
        } else {
            stepValue = 1;
        }
        
        if ((lastValue > currentValue && stepValue > 0) || (lastValue < currentValue && stepValue < 0)) {
            while (currentValue <= lastValue) {
                if (currentValue == (int) currentValue) {
                    domain.add(String.valueOf((int) currentValue));
                } else if (currentValue == (long) currentValue) {
                    domain.add(String.valueOf((long) currentValue));
                } else {
                    domain.add(String.valueOf(currentValue));
                }
                currentValue += stepValue;
            }
        } else {
            log.error("Oops! Not valid 'range' parameters. "
                    + "It's not possible to come from '" + currentValue + "' to '" + lastValue + "' with '"  + stepValue + "' step!");
        }
    
    }


    /**
     * A custom Action for the 'dg:assign' tag inside models
     */
    public static class SetAssignTag extends Action {
        private String name;
        private String from;
        private String to;
        private String step;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getStep() {
            return step;
        }

        public void setStep(String step) {
            this.step = step;
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
