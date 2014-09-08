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
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Marshall Peters
 * Date: 9/3/14
 */
public class SCXMLFrontierTest {

    /**
     * Multiple variable assignments using set:{}
     */
    @Test
    public void testMultiVariableAssignment() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
        e.setModelByInputFileStream(is);

        try {
            List<PossibleState> bfs = e.bfs(343);
            PossibleState p = bfs.get(0);

            try {
                is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
                SCXML model = SCXMLParser.parse(new InputSource(is), null);

                SCXMLFrontier frontier = new SCXMLFrontier(p, model);
                Queue<Map<String, String>> queue = new LinkedList<>();
                AtomicBoolean flag = new AtomicBoolean(false);
                frontier.searchForScenarios(queue, flag);

                Assert.assertEquals(queue.size(), 6);
            } catch (IOException | SAXException ex) {
                Assert.fail();
            }
        } catch (ModelException ex) {
            Assert.fail();
        }
    }

    /**
     * Test the ability of the exit flag to stop the DFS in SCXMLFrontier
     */
    @Test
    public void testExitFlag() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
        e.setModelByInputFileStream(is);

        try {
            List<PossibleState> bfs = e.bfs(1);
            PossibleState p = bfs.get(0);

            try {
                is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
                SCXML model = SCXMLParser.parse(new InputSource(is), null);

                SCXMLFrontier frontier = new SCXMLFrontier(p, model);
                Queue<Map<String, String>> queue = new LinkedList<>();
                AtomicBoolean flag = new AtomicBoolean(true);
                frontier.searchForScenarios(queue, flag);

                Assert.assertEquals(queue.isEmpty(), true);
            } catch (IOException | SAXException ex) {
                Assert.fail();
            }
        } catch (ModelException ex) {
            Assert.fail();
        }
    }

}
