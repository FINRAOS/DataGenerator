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
import java.util.List;

/**
 * Marshall Peters
 * Date: 9/3/14
 */
public class SCXMLGapperTest {
    static String bigTest = "<scxml xmlns=\"http://www.w3.org/2005/07/scxml\"\n"
            + "       xmlns:dg=\"org.finra.datagenerator\""
            + "       version=\"1.0\"\n"
            + "       initial=\"start\">\n"
            + "\n"
            + "    <state id=\"start\">\n"
            + "        <transition event=\"BULK_ASSIGN\" target=\"BULK_ASSIGN\"/>\n"
            + "    </state>\n"
            + "\n"
            + "    <state id=\"BULK_ASSIGN\">\n"
            + "        <onentry>\n"
            + "            <dg:assign name=\"var_out_RECORD_TYPE\" set=\"a,b,c,d,e,f,g\"/>\n"
            + "            <dg:assign name=\"var_out_RECORD_TYPE_2\" set=\"a,b,c,d,e,f,g\"/>\n"
            + "            <dg:assign name=\"var_out_RECORD_TYPE_3\" set=\"a,b,c,d,e,f,g\"/>\n"
            + "            <assign name=\"var_out_RECORD_TYPE_4\" expr=\"Lorem\"/>\n"
            + "            <assign name=\"var_out_RECORD_TYPE_5\" expr=\"Ipsum\"/>\n"
            + "            <assign name=\"var_out_RECORD_TYPE_6\" expr=\"Doler\"/>\n"
            + "        </onentry>\n"
            + "        <transition event=\"ASSIGN_WITH_CONDITIONS\" target=\"ASSIGN_WITH_CONDITIONS\"/>\n"
            + "    </state>\n"
            + "\n"
            + "    <state id=\"ASSIGN_WITH_CONDITIONS\">\n"
            + "        <onentry>\n"
            + "            <dg:assign name=\"var_out_RECORD_TYPE_7\" set=\"1,2,3\"/>\n"
            + "            <dg:assign name=\"var_out_RECORD_TYPE_8\" set=\"1,2,3\"/>\n"
            + "        </onentry>\n"
            + "        <transition event=\"end\" target=\"end\" cond=\"${var_out_RECORD_TYPE_7"
            + " != var_out_RECORD_TYPE_8}\"/>\n"
            + "    </state>\n"
            + "    \n"
            + "<state id=\"end\">\n"
            + "    </state>\n"
            + "</scxml>";

    /**
     * Test SCXMLGapper's ability to decompose and recompose an SCXMLFrontier with none of the variables set
     */
    @Test
    public void testDecomposeAndReproduceInitial() {
        SCXMLEngine e = new SCXMLEngine();
        e.setModelByText(bigTest);

        try {
            List<PossibleState> bfs = e.bfs(1);
            PossibleState p = bfs.get(0);

            try {
                InputStream is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
                SCXML model = SCXMLParser.parse(new InputSource(is), null);

                SCXMLFrontier frontier = new SCXMLFrontier(p, model);
                SCXMLGapper gapper = new SCXMLGapper();
                frontier = (SCXMLFrontier) gapper.reproduce(gapper.decompose(frontier, bigTest));

                p = frontier.getRoot();
                Assert.assertEquals(p.nextState.getId(), "start");

                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE"), "");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_2"), "");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_3"), "");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_4"), "");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_5"), "");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_6"), "");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_7"), "");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_8"), "");
                Assert.assertEquals(p.variables.keySet().size(), 8);
            } catch (IOException | SAXException ex) {
                Assert.fail();
            }
        } catch (ModelException ex) {
            Assert.fail();
        }
    }

    /**
     * Test SCXMLGapper's ability to decompose and recompose an SCXMLFrontier
     */
    @Test
    public void testDecomposeAndRecompose() {
        SCXMLEngine e = new SCXMLEngine();
        e.setModelByText(bigTest);

        try {
            List<PossibleState> bfs = e.bfs(343);
            PossibleState p = bfs.get(0);

            try {
                InputStream is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
                SCXML model = SCXMLParser.parse(new InputSource(is), null);

                SCXMLFrontier frontier = new SCXMLFrontier(p, model);
                SCXMLGapper gapper = new SCXMLGapper();

                PossibleState before = frontier.getRoot();
                frontier = (SCXMLFrontier) gapper.reproduce(gapper.decompose(frontier, bigTest));
                PossibleState after = frontier.getRoot();

                Assert.assertEquals(after.nextState.getId(), before.nextState.getId());

                for (String key: before.variables.keySet()) {
                    Assert.assertEquals(before.variables.get(key), after.variables.get(key));
                }

                for (String key: after.variables.keySet()) {
                    Assert.assertEquals(before.variables.get(key), after.variables.get(key));
                }
            } catch (IOException | SAXException ex) {
                Assert.fail();
            }
        } catch (ModelException ex) {
            Assert.fail();
        }
    }

}
