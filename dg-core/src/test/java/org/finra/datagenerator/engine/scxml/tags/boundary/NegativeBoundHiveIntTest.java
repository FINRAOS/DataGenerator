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
public class NegativeBoundHiveIntTest {

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        NegativeBoundHiveInt.NegativeBoundHiveIntTag neg = new NegativeBoundHiveInt.NegativeBoundHiveIntTag();

        neg.setName("hive_int_test");
        Assert.assertEquals(neg.getName(), "hive_int_test");

        neg.setName("hive_int_test2");
        Assert.assertEquals(neg.getName(), "hive_int_test2");
    }

    /**
     * test for setNullable() and getNullable()
     */
    @Test
    public void nullTest() {
        NegativeBoundHiveInt.NegativeBoundHiveIntTag neg = new NegativeBoundHiveInt.NegativeBoundHiveIntTag();

        neg.setNullable("true");
        Assert.assertEquals(neg.getNullable(), "true");

        neg.setNullable("false");
        Assert.assertEquals(neg.getNullable(), "false");
    }

    /**
     * test for setMin() and getMin()
     */
    @Test
    public void minTest() {
        NegativeBoundHiveInt.NegativeBoundHiveIntTag neg = new NegativeBoundHiveInt.NegativeBoundHiveIntTag();

        neg.setMin("10");
        Assert.assertEquals(neg.getMin(), "10");

        neg.setMin("100");
        Assert.assertEquals(neg.getMin(), "100");
    }

    /**
     * test for setMax() and getMax()
     */
    @Test
    public void maxTest() {
        NegativeBoundHiveInt.NegativeBoundHiveIntTag neg = new NegativeBoundHiveInt.NegativeBoundHiveIntTag();

        neg.setMax("10");
        Assert.assertEquals(neg.getMax(), "10");

        neg.setMax("100");
        Assert.assertEquals(neg.getMax(), "100");
    }
}
