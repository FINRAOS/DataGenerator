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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of dg:range tag
 */
public class RangeExtension implements CustomTagExtension<RangeExtension.RangeTag> {

    public Class<RangeTag> getTagActionClass() {
        return RangeTag.class;
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
     * @param action            a RangeTag Action
     * @param possibleStateList a current list of possible states produced so far from expanding a model state
     * @return the cartesian product of every current possible state and the set of values specified by action
     */
    public List<Map<String, String>> pipelinePossibleStates(RangeTag action,
                                                            List<Map<String, String>> possibleStateList) {
        String variable = action.getName();
        BigDecimal from = new BigDecimal(action.getFrom());
        BigDecimal to = new BigDecimal(action.getTo());
        BigDecimal step;
        if (action.getStep() != null) {
            step = new BigDecimal(action.getStep());
        } else {
            step = BigDecimal.ONE;
        }

        List<BigDecimal> rangeValues = new ArrayList<>();
        if (step.signum() == 1) {
            for (BigDecimal current = from; current.compareTo(to) <= 0; current = current.add(step)) {
                rangeValues.add(current);
            }
        } else if (step.signum() == -1) {
            for (BigDecimal current = from; current.compareTo(to) >= 0; current = current.add(step)) {
                rangeValues.add(current);
            }
        } else {
            rangeValues.add(from);
        }

        //take the product
        List<Map<String, String>> productTemp = new LinkedList<>();
        for (Map<String, String> p : possibleStateList) {
            for (BigDecimal value : rangeValues) {
                HashMap<String, String> n = new HashMap<>(p);
                n.put(variable, value.toString());
                productTemp.add(n);
            }
        }

        return productTemp;
    }

    /**
     * A custom Action for the 'dg:range' tag inside models
     */
    public static class RangeTag extends Action {
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
