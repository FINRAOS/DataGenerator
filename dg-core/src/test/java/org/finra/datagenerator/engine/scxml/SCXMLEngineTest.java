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

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.scxml.model.ModelException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Marshall Peters Date: 9/3/14
 */
public class SCXMLEngineTest {
    private String[] setRecordType1 = new String[]{"a1", "b1", "c1", "d1", "e1", "f1", "g1"};
    private String[] setRecordType2 = new String[]{"a2", "b2", "c2", "d2", "e2", "f2", "g2"};
    private String[] setRecordType3 = new String[]{"a3", "b3", "c3", "d3", "e3", "f3", "g3"};

    /**
     * Multiple variable assignments using 'set' inside 'dg:assign' tag
     */
    @Test
    public void testMultiVariableAssignment() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = SCXMLEngineTest.class.getResourceAsStream("/bigtest.xml");
        e.setModelByInputFileStream(is);

        try {
            List<PossibleState> bfs = e.bfs(100);
            Assert.assertEquals("Oops! We have '" + bfs.size() + "' records, but we wait for 7^3, "
                    + "produced by expanding BULK_ASSIGN", 343, bfs.size());

            Set<Set<String>> allPossibleValuesForRecordType123 = generateAllPossibleValues();
            for (PossibleState p : bfs) {
                // let's remove this generated record from list of all necessary records for this test
                Set<String> recordType123 = new HashSet<String>();
                recordType123.add(p.variables.get("var_out_RECORD_TYPE"));
                recordType123.add(p.variables.get("var_out_RECORD_TYPE_2"));
                recordType123.add(p.variables.get("var_out_RECORD_TYPE_3"));
                if (allPossibleValuesForRecordType123.contains(recordType123)) {
                    allPossibleValuesForRecordType123.remove(recordType123);
                } else {
                    Assert.fail("Oops! We wait for '" + recordType123 + "' combination for RECORD_TYPE_1, _2 and _3, but it wasn't generated!");
                }

                Assert.assertEquals(p.nextState.getId(), "ASSIGN_WITH_CONDITIONS");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_4"), "Lorem");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_5"), "Ipsum");
                Assert.assertEquals(p.variables.get("var_out_RECORD_TYPE_6"), "Doler");
            }

            // we have removed all generated values for RECORD_TYPE_1, _2 and _3, so 'allPossibleValuesForRecordType123' has be empty now
            for (Set<String> valuesForRecordType123 : allPossibleValuesForRecordType123) {
                Assert.fail("Oops! We have not generated combanation for for RECORD_TYPE_1, _2 and _3 - '" + valuesForRecordType123 + "'!");
            }
        } catch (ModelException ex) {
            Assert.fail("Oops! ModelException = " + e);
        }
    }

    private Set<Set<String>> generateAllPossibleValues() {
        Set<Set<String>> result = new HashSet<Set<String>>();

        for (String val1 : setRecordType1) {
            for (String val2 : setRecordType2) {
                for (String val3 : setRecordType3) {
                    Set<String> record = new HashSet<String>();
                    record.add(val1);
                    record.add(val2);
                    record.add(val3);
                    result.add(record);
                }
            }
        }
        return result;
    }

    /**
     * All variables have a default assignment of "" (empty string)
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
            Assert.fail("Oops! ModelException = " + e);
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

        String errorMessageExpected = "Could not achieve required bootstrap without reaching end state";
        try {
            e.bfs(4000); // too many
            Assert.fail("Oops! We wait for '" + errorMessageExpected + "' error message here "
                    + "(bootstrap size is too big for this model), but don't have it!");
        } catch (ModelException ex) {
            Assert.assertEquals("Oops! We have '"  + ex.getMessage() + "' modelException error message, but we wait for "
                    + "'" + errorMessageExpected + "' here!", ex.getMessage(), errorMessageExpected);
        }
    }
}
