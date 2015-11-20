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
public class PositiveBoundHiveVarcharTest {

    //TODO tests for min and max length

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag pos = new PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag();

        pos.setName("varchar_test");
        Assert.assertEquals(pos.getName(), "varchar_test");

        pos.setName("varchar_test2");
        Assert.assertEquals(pos.getName(), "varchar_test2");
    }

    /**
     *
     */
    @Test
    public void lengthTest() {
        PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag pos = new PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag();

        pos.setLength("18");
        Assert.assertEquals(pos.getLength(), "18");

        pos.setLength("10");
        Assert.assertEquals(pos.getLength(), "10");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag pos = new PositiveBoundHiveVarchar.PositiveBoundHiveVarcharTag();

        pos.setNullable("true");
        Assert.assertEquals(pos.getNullable(), "true");

        pos.setNullable("false");
        Assert.assertEquals(pos.getNullable(), "false");
    }
}
