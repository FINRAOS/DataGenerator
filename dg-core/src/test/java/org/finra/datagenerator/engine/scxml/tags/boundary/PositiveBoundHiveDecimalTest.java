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
public class PositiveBoundHiveDecimalTest {

    //TODO tests for min and max length
    //TODO tests for min and max

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag pos = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        pos.setName("decimal_test");
        Assert.assertEquals(pos.getName(), "decimal_test");

        pos.setName("decimal_test2");
        Assert.assertEquals(pos.getName(), "decimal_test2");
    }

    /**
     * test for setLength() and getLength()
     */
    @Test
    public void lengthTest() {
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag pos = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        pos.setLength("18,8");
        Assert.assertEquals(pos.getLength(), "18,8");

        pos.setLength("38,10");
        Assert.assertEquals(pos.getLength(), "38,10");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag pos = new PositiveBoundHiveDecimal.PositiveBoundHiveDecimalTag();

        pos.setNullable("true");
        Assert.assertEquals(pos.getNullable(), "true");

        pos.setNullable("false");
        Assert.assertEquals(pos.getNullable(), "false");
    }
}
