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

import org.apache.commons.scxml.model.ModelException;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;


/**
 * Marshall Peters
 * Date: 9/3/14
 */
public class SCXMLEngineTest {

    /**
     * Multiple variable assignments using set:{}
     */
    @Test
    public void testMultiVariableAssignment() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
        e.setModelByInputFileStream(is);

        try {
            List<PossibleState> bfs = e.bfs(100);
            Assert.assertEquals(343, bfs.size());  //7^3, produced by expanding BULK_ASSIGN

            for (PossibleState p: bfs) {
                Assert.assertEquals(p.nextState.getId(), "ASSIGN_WITH_CONDITIONS");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_4"), "Lorem");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_5"), "Ipsum");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_6"), "Doler");
            }
        } catch (ModelException ex) {
            Assert.fail();
        }
    }


    /**
     * All variables have a default assignment of ""
     */
    @Test
    public void testInitiallyEmptyAssignment() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
        e.setModelByInputFileStream(is);

        try {
            List<PossibleState> bfs = e.bfs(1);
            Assert.assertEquals(1, bfs.size());

            PossibleState p = bfs.get(0);
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
        } catch (ModelException ex) {
            Assert.fail();
        }
    }

    /**
     * Throws exception when reaching end state
     */
    @Test
    public void testExceptionAtEndState() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
        e.setModelByInputFileStream(is);

        try {
            List<PossibleState> bfs = e.bfs(4000); //too many
            Assert.fail();
        } catch (ModelException ex) {
            Assert.assertEquals(ex.getMessage(), "Could not achieve required bootstrap without reaching end state");
        }
    }

}
