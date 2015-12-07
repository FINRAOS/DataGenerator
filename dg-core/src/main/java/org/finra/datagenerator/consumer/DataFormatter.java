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

import org.finra.datagenerator.writer.SqlWriter;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Wrapper for search results.
 *
 * Created by RobbinBr on 5/18/2014.
 * Updated by Mauricio Silva on 4/24/2015
 */
public class DataFormatter {

    private final Map<String, String> dataMap = new HashMap<>();
    private final DataConsumer dataConsumer;

    /**
     * Default constructor. Initializes the dataConsumer to {@link DataConsumer}
     */
    public DataFormatter() {
        dataConsumer = new DataConsumer();
    }

    /**
     * Constructor sets a user given {@link DataConsumer}
     *
     * @param dataConsumer a reference to {@link DataConsumer}
     */
    public DataFormatter(final DataConsumer dataConsumer) {
        this.dataConsumer = dataConsumer;
    }

    /**
     * Constructor sets a max number of lines and shares an exit flag with the
     * DataFormatter
     *
     * @param maxNumberOfLines a long containing the maximum number of lines
     * expected to flow through this pipe
     * @param flag an AtomicBoolean exit flag
     */
    public DataFormatter(final long maxNumberOfLines, final AtomicBoolean flag) {
        this.dataConsumer = new DataConsumer().setMaxNumberOfLines(maxNumberOfLines).setExitFlag(flag);
    }

    public DataConsumer getDataConsumer() {
        return this.dataConsumer;
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    /**
     * Given an array of variable names, returns a pipe delimited {@link String}
     * of values.
     *
     * @param outTemplate an array of {@link String}s containing the variable
     * names.
     * @return a pipe delimited {@link String} of values
     */
    public String getPipeDelimited(String[] outTemplate) {
        StringBuilder b = new StringBuilder(1024);

        for (String var : outTemplate) {
            if (b.length() > 0) {
                b.append('|');
            }
            b.append(getDataMap().get(var));
        }

        return b.toString();
    }

    /**
     * Given an array of variable names, returns a JsonObject
     * of values.
     *
     * @param outTemplate an array of {@link String}s containing the variable
     * names.
     * @return a json object of values
     */
    public JSONObject getJsonFormatted(String [] outTemplate) {
        JSONObject oneRowJson = new JSONObject();

        for (String var : outTemplate) {
            oneRowJson.put(var, getDataMap().get(var));
        }

        return oneRowJson;
    }

    /**
     * Given an array of variable names, returns a sql statement {@link String}
     * of values.
     *
     * @param outTemplate an array of {@link String}s containing the variable
     * names.
     * @param schema of data base
     * @param tableName tableName
     * @param sqlStatement update/insert
     * @return query statement
     */
    //INSERT/UPDATE INTO schema.table (key1, key2) VALUES ("value1","valu2");
    public String getSqlFormatted(String[] outTemplate, String schema, String tableName,
                                        SqlWriter.SqlStatement sqlStatement) {
        StringBuilder keys = new StringBuilder();
        StringBuilder values = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String var : outTemplate) {
            if (keys.length() > 0) {
                keys.append(',');
            }
            if (values.length() > 0) {
                values.append(',');
            }
            keys.append(var);
            values.append(getDataMap().get(var));
        }
        return query.append(sqlStatement).append(" INTO ").append(schema).append(".")
                .append(tableName).append(" (").append(keys).append(") ").append("VALUES")
                .append(" (").append(values).append(");").toString();
    }

    /**
     * Given an array of variable names, returns an Xml String
     * of values.
     *
     * @param outTemplate an array of {@link String}s containing the variable
     * names.
     * @return values in Xml format
     */
    public String getXmlFormatted(String[] outTemplate)  {
        StringBuilder sb = new StringBuilder();
        for (String var : outTemplate) {
            //Tag and value
            sb.append("<"); sb.append(var); sb.append(">");
            sb.append(getDataMap().get(var));
            sb.append("</"); sb.append(var); sb.append(">");
        }
        return sb.toString();
    }
}
