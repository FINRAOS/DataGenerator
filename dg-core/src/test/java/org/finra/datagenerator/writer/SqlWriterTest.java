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
public class SqlWriterTest {

    /**
     * Tests getSqlFormatted
     */
    @Test
    public void testSqlWriter() {

        DataPipe dataFormatter = new DataPipe();
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
        SqlWriter.SqlStatementType sqlStatement = SqlWriter.SqlStatementType.INSERT;
        SqlWriter sqlWriter = new SqlWriter(System.out, outTemplate, schema, tableName, sqlStatement);

        String expected = sqlStatement + " INTO " + schema + "." + tableName + " (var1,var2,var3,var4,var5) "
                + "VALUES (var1val,var2val,var3val,var4val,var5val);";

        Assert.assertEquals(5, dataFormatter.getDataMap().size());
        Assert.assertEquals(expected, sqlWriter.getSqlFormatted(dataFormatter.getDataMap()));
    }
}
