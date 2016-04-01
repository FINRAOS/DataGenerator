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
public class DGFileTest {
    private String[] setVarDomain1 = new String[]{"a1", "b1"};
    private String[] setVarDomain2 = new String[]{"a2", "b2", "c2"};
    

    /**
     * Test multiple variable assignments using 'set' and 'range' inside 'dg:assign' tag
     */
    @Test
    public void verifyDGFileExention() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = DGFileTest.class.getResourceAsStream("/DGFileTest.xml");
        e.setModelByInputFileStream(is);

        try {
            Set<List<String>> allPossibleValues = generateAllPossibleValues();

            List<PossibleState> bfs = e.bfs(allPossibleValues.size());

            Assert.assertEquals("Oops! We have '" + bfs.size() + "' record(s), but we wait for " + allPossibleValues.size() + " record(s)!",
                    allPossibleValues.size(), bfs.size());

            for (PossibleState p : bfs) {
                // let's remove this generated record from list of all necessary records for this test
                List<String> recordType12 = new ArrayList<String>();
                recordType12.add(p.variables.get("var_1"));
                recordType12.add(p.variables.get("var_2"));
                if (allPossibleValues.contains(recordType12)) {
                    allPossibleValues.remove(recordType12);
                } else {
                    Assert.fail("Oops! '" + recordType12 + "' combination was generated, but we don't wait for it!");
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
                List<String> record = new ArrayList<String>();
                record.add(val1);
                record.add(val2);
                result.add(record);
            }
        }
        return result;
    }

}
