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
package org.finra.datagenerator.engine.scxml.tags.boundary;

import org.junit.Assert;
import org.junit.Test;

/**
 * Nathaniel Lee
 * Date: 10/27/15
 */
public class NegativeBoundHiveVarcharTest {

    //TODO:tests for min and max Length

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag neg = new NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag();

        neg.setName("decimal_test");
        Assert.assertEquals(neg.getName(), "decimal_test");

        neg.setName("decimal_test2");
        Assert.assertEquals(neg.getName(), "decimal_test2");
    }

    /**
     * test for setLength() and getLength()
     */
    @Test
    public void lengthTest() {
        NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag neg = new NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag();

        neg.setLength("18");
        Assert.assertEquals(neg.getLength(), "18");

        neg.setLength("38");
        Assert.assertEquals(neg.getLength(), "38");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag neg = new NegativeBoundHiveVarchar.NegativeBoundHiveVarcharTag();

        neg.setNullable("true");
        Assert.assertEquals(neg.getNullable(), "true");

        neg.setNullable("false");
        Assert.assertEquals(neg.getNullable(), "false");
    }
}
