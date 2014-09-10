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

import org.apache.commons.scxml.env.jsp.ELContext;
import org.apache.commons.scxml.env.jsp.ELEvaluator;
import org.apache.commons.scxml.model.SCXML;
import org.apache.log4j.Logger;
import org.finra.datagenerator.engine.Frontier;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Marshall Peters
 * Date: 9/4/14
 */
public class NegSCXMLFrontier extends NegSCXMLCommons implements Frontier {

    private final NegPossibleState root;
    private static final Logger log = Logger.getLogger(NegSCXMLFrontier.class);
    private int negative;

    /**
     * Constructor
     *
     * @param possibleState the root node of the model and partial variable assignment to start a dfs from
     * @param negative the required number of negative variable assignments
     * @param model         the model text
     */
    public NegSCXMLFrontier(final NegPossibleState possibleState, final SCXML model, final int negative) {
        root = possibleState;

        this.setStateMachine(model);

        ELEvaluator elEvaluator = new ELEvaluator();
        ELContext context = new ELContext();

        this.setEvaluator(elEvaluator);
        this.setRootContext(context);

        this.negative = negative;
    }

    /**
     * Performs a DFS on the model, starting from root, placing results in the queue
     * Just a public wrapper for private dfs function
     *
     * @param queue the results queue
     * @param flag  used to stop the search before completion
     */
    public void searchForScenarios(Queue<Map<String, String>> queue, AtomicBoolean flag) {
        dfs(queue, flag, root);
    }

    private void dfs(Queue<Map<String, String>> queue, AtomicBoolean flag, NegPossibleState state) {
        if (flag.get()) {
            return;
        }

        //reached end of chart, valid assignment found only if a negative value is set
        if (state.nextState.getId().equalsIgnoreCase("end")) {
            if (state.negVariable.size() == negative) {
                queue.add(state.variables);

                if (queue.size() > 10000) {
                    log.info("Queue size " + queue.size() + ", waiting");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        log.info("Interrupted ", ex);
                    }
                }

                return;
            }
        }

        List<NegPossibleState> expand = new LinkedList<>();

        expandPositive(state, expand);
        if (state.negVariable.size() < negative) {
            expandNegative(state, expand, negative - state.negVariable.size());
        }

        for (NegPossibleState e : expand) {
            dfs(queue, flag, e);
        }
    }

    public NegPossibleState getRoot() {
        return root;
    }
}