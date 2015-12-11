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
package org.finra.datagenerator.consumer;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by RobbinBr on 7/2/2014.
 * Updated by Mauricio Silva on 8/16/2015.
 */
public class DataFormatterTest {

    /**
     * Tests the DataFormatter's variable map
     */
    @Test
    public void testDataMap() {
        DataFormatter thePipe = new DataFormatter();
        thePipe.getDataMap().put("var1", "var1val");
        thePipe.getDataMap().put("var2", "var2val");
        thePipe.getDataMap().put("var3", "var3val");
        thePipe.getDataMap().put("var4", "var4val");
        thePipe.getDataMap().put("var5", "var5val");

        Assert.assertEquals(5, thePipe.getDataMap().size());

        Assert.assertEquals("var1val", thePipe.getDataMap().get("var1"));
        Assert.assertEquals("var2val", thePipe.getDataMap().get("var2"));
        Assert.assertEquals("var3val", thePipe.getDataMap().get("var3"));
        Assert.assertEquals("var4val", thePipe.getDataMap().get("var4"));
        Assert.assertEquals("var5val", thePipe.getDataMap().get("var5"));
    }

    /**
     * Tests flag access
     */
    @Test
    public void testDefaultDataConsumerAccess() {
        DataFormatter thePipe = new DataFormatter();
        DataConsumer dc = thePipe.getDataConsumer();
        dc.setExitFlag(new AtomicBoolean(false));

        Assert.assertNotNull(dc);
        Assert.assertNotNull(dc.getExitFlag());
        Assert.assertEquals(10000, dc.getMaxNumberOfLines());
    }

    /**
     * Tests access of custom consumers to DefaultConsumer methods
     */
    @Test
    public void testCustomDataConsumerAccess() {
        DataConsumer dc = new DataConsumer();
        DataFormatter thePipe = new DataFormatter(dc);

        dc.setReportingHost("localhost:8080");
        dc.setMaxNumberOfLines(100000);

        Assert.assertEquals("localhost:8080", thePipe.getDataConsumer().getReportingHost());
        Assert.assertEquals(100000, thePipe.getDataConsumer().getMaxNumberOfLines());
    }

}
