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

import org.finra.datagenerator.writer.DefaultWriter;
import org.finra.datagenerator.writer.JsonWriter;
import org.finra.datagenerator.writer.SqlWriter;
import org.finra.datagenerator.writer.XmlWriter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Mauricio Silva on 12/11/2015.
 */
public class CustomWritersTest {

    /**
     * Tests getPipeDelimited
     */
    @Test
    public void testDefaultWriter() {

        DataFormatter dataFormatter = new DataFormatter();
        dataFormatter.getDataMap().put("var1", "var1val");
        dataFormatter.getDataMap().put("var2", "var2val");
        dataFormatter.getDataMap().put("var3", "var3val");
        dataFormatter.getDataMap().put("var4", "var4val");
        dataFormatter.getDataMap().put("var5", "var5val");

        String[] outTemplate = new String[]{
                "var1", "var2", "var3", "var4", "var5"
        };

        DefaultWriter defaultWriter = new DefaultWriter(System.out, outTemplate);

        Assert.assertEquals(5, dataFormatter.getDataMap().size());

        Assert.assertEquals("var1val|var2val|var3val|var4val|var5val",
                defaultWriter.getPipeDelimited(dataFormatter.getDataMap()));

    }

    /**
     * Tests getJsonFormatted
     */
    @Test
    public void testJsonWriter() {

        DataFormatter dataFormatter = new DataFormatter();
        dataFormatter.getDataMap().put("var1", "var1val");
        dataFormatter.getDataMap().put("var2", "var2val");
        dataFormatter.getDataMap().put("var3", "var3val");
        dataFormatter.getDataMap().put("var4", "var4val");
        dataFormatter.getDataMap().put("var5", "var5val");

        String[] outTemplate = new String[]{
                "var1", "var2", "var3", "var4", "var5"
        };

        JsonWriter jsonWriter = new JsonWriter(System.out, outTemplate);

        String expected = "{\"var5\":\"var5val\",\"var4\":\"var4val\",\"var3\":\"var3val\",\"var2\":\"var2val\","
                + "\"var1\":\"var1val\"}";

        Assert.assertEquals(5, dataFormatter.getDataMap().size());

        Assert.assertEquals(expected, jsonWriter.getJsonFormatted(dataFormatter.getDataMap()).toString());
    }

    /**
     * Tests getSqlFormatted
     */
    @Test
    public void testSqlWriter() {

        DataFormatter dataFormatter = new DataFormatter();
        dataFormatter.getDataMap().put("var1", "var1val");
        dataFormatter.getDataMap().put("var2", "var2val");
        dataFormatter.getDataMap().put("var3", "var3val");
        dataFormatter.getDataMap().put("var4", "var4val");
        dataFormatter.getDataMap().put("var5", "var5val");

        String[] outTemplate = new String[]{
                "var1", "var2", "var3", "var4", "var5"
        };
        String schema = "QC_ADMIN";
        String tableName = "DATA_SERVICE";
        SqlWriter.SqlStatement sqlStatement = SqlWriter.SqlStatement.INSERT;
        SqlWriter sqlWriter = new SqlWriter(System.out, outTemplate, schema, tableName, sqlStatement);

        String expected = sqlStatement + " INTO " + schema + "." + tableName + " (var1,var2,var3,var4,var5) "
                + "VALUES (var1val,var2val,var3val,var4val,var5val);";

        Assert.assertEquals(5, dataFormatter.getDataMap().size());

        Assert.assertEquals(expected, sqlWriter.getSqlFormatted(dataFormatter.getDataMap()));
    }

    /**
     * Tests getXmlFormatted
     */
    @Test
    public void testXmlWriter() {

        DataFormatter dataFormatter = new DataFormatter();
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
