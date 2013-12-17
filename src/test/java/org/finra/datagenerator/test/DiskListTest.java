/*
 * (C) Copyright 2013 DataGenerator Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.finra.datagenerator.test;

import org.apache.log4j.Logger;
import org.finra.datagenerator.disklist.DiskList;
import org.finra.datagenerator.generation.DataSet;
import org.finra.datagenerator.input.VariableSpec;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class DiskListTest {

    private static final Logger log = Logger.getLogger(DiskListTest.class);

    @Test
    public void testAddAndGet() {
        DiskList<Integer> dli = new DiskList<>();
        dli.add(1);
        dli.add(2);
        dli.add(3);

        assertEquals(new Integer(1), dli.get(0));
        assertEquals(new Integer(2), dli.get(1));
        assertEquals(new Integer(3), dli.get(2));
    }

    @Test
    public void testSize() {
        DiskList<Integer> dli = new DiskList<>();
        dli.add(1);
        dli.add(2);
        dli.add(3);

        assertEquals(3, dli.size());
    }

    @Test
    public void testDataSet() {
        DataSet ds = new DataSet();
        ds.createVariable(new VariableSpec("var"));
        DiskList<DataSet> dlds = new DiskList<>();

        dlds.add(ds);

        DataSet ret = dlds.get(0);

        assertNotNull(ret.get("var"));
    }

    @Test
    public void testMultipleBlocks() {

        for(int i = 1; i<=100; i = i*10){
            log.debug("Using block size of "+i);
            DiskList<Integer> dli = new DiskList<>(i);

            for(int j = 0; j<i*i; j++){
                dli.add(j);
            }

            assertEquals(i*i, dli.size());

            for(int j = 0; j<i*i; j++){
                log.debug(j);
                assertTrue(dli.get(j).equals(j));
            }
        }

    }
}
