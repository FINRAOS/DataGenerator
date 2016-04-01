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
package org.finra.datagenerator.writer;

import org.finra.datagenerator.consumer.DataPipe;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Mauricio Silva on 3/16/2016.
 */
public class XmlWriterTest {

    /**
     * Tests getXmlFormatted
     */
    @Test
    public void testXmlWriter() {

        DataPipe dataFormatter = new DataPipe();
        dataFormatter.getDataMap().put("var1", "var1val");
        dataFormatter.getDataMap().put("var2", "var2val");
        dataFormatter.getDataMap().put("var3", "var3val");
        dataFormatter.getDataMap().put("var4", "var4val");
        dataFormatter.getDataMap().put("var5", "var5val");

        String[] outTemplate = new String[]{
                "var1", "var2", "var3", "var4", "var5"
        };

        XmlWriter xmlWriter = new XmlWriter(System.out, outTemplate, "root", "record");

        String expected = "<var1>var1val</var1><var2>var2val</var2><var3>var3val</var3>"
                + "<var4>var4val</var4><var5>var5val</var5>";

        Assert.assertEquals(5, dataFormatter.getDataMap().size());
        Assert.assertEquals(expected, xmlWriter.getXmlFormatted(dataFormatter.getDataMap()));
    }
}
