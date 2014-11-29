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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Testing dg:assign custom tag with 'set' and 'range' methods 
 */
public class DGAssignTest {
    private String[] setVarDomain1 = new String[]{"a1", "b1", "c1"};
    private String[] setVarDomain2 = new String[]{"0", "1", "2"};
    private String[] setVarDomain3 = new String[]{"a3", "b3", "c3", "0", "0.5", "1"};
    private String[] setVarDomain4 = new String[]{"a4", "b4"};
    private String[] setVarDomain5 = new String[]{"a5", "b5", "c5", "0", "0.01", "0.02", "0.03"};
    

    /**
     * Test multiple variable assignments using 'set' and 'range' inside 'dg:assign' tag
     */
    @Test
    public void setAndRangeAssignment() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = DGAssignTest.class.getResourceAsStream("/DGAssignTest.xml");
        e.setModelByInputFileStream(is);

        try {
            Set<List<String>> allPossibleValues = generateAllPossibleValues();

            List<PossibleState> bfs = e.bfs(allPossibleValues.size());

            Assert.assertEquals("Oops! We have '" + bfs.size() + "' record(s), but we wait for " + allPossibleValues.size() + " record(s)!",
                    allPossibleValues.size(), bfs.size());

            for (PossibleState p : bfs) {
                // let's remove this generated record from list of all necessary records for this test
                List<String> recordType12345 = new ArrayList<String>();
                recordType12345.add(p.variables.get("var_1"));
                recordType12345.add(p.variables.get("var_2"));
                recordType12345.add(p.variables.get("var_3"));
                recordType12345.add(p.variables.get("var_4"));
                recordType12345.add(p.variables.get("var_5"));
                if (allPossibleValues.contains(recordType12345)) {
                    allPossibleValues.remove(recordType12345);
                } else {
                    Assert.fail("Oops! '" + recordType12345 + "' combination was generated, but we don't wait for it!");
                }
            }

            // we have removed all generated values, so 'allPossibleValues' has be empty now
            for (List<String> value : allPossibleValues) {
                Assert.fail("Oops! We have not generated this combanation: '" + value + "'!");
            }
        } catch (ModelException ex) {
            Assert.fail("Oops! ModelException = " + ex);
        }
    }

    private Set<List<String>> generateAllPossibleValues() {
        Set<List<String>> result = new HashSet<List<String>>();

        for (String val1 : setVarDomain1) {
            for (String val2 : setVarDomain2) {
                for (String val3 : setVarDomain3) {
                    for (String val4 : setVarDomain4) {
                        for (String val5 : setVarDomain5) {
                            List<String> record = new ArrayList<String>();
                            record.add(val1);
                            record.add(val2);
                            record.add(val3);
                            record.add(val4);
                            record.add(val5);
                            result.add(record);
                        }
                    }
                }
            }
        }
        return result;
    }

}
