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

import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataPipe;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


/**
 * Orders result variables based on a template and writes them in sql format to a given OutputStream.
 *
 * Created by Mauricio Silva on 6/27/2015.
 */
public class SqlWriter implements DataWriter {
    /**
     * Logger
     */
    protected static final Logger log = Logger.getLogger(SqlWriter.class);
    private final OutputStream os;
    private String[] outTemplate;
    private String schema;
    private String tableName;
    private SqlStatementType sqlStatement;

    /**
     * Sql statement types
     */
    public enum SqlStatementType {
        /**
         * Insert Statement
         */
        INSERT,
        /**
         * Update Statement
         */
        UPDATE
    }

    /**
     * Constructor
     *
     * @param os           the output stream to use in writing
     * @param outTemplate  the output template to format writing
     * @param schema       indicates schema of db
     * @param tableName    indicates table name of db
     * @param sqlStatement insert/update
     */
    public SqlWriter(final OutputStream os, final String[] outTemplate, final String schema,
                     final String tableName, final SqlStatementType sqlStatement) {
        this.os = os;
        this.outTemplate = outTemplate;
        this.schema = schema;
        this.tableName = tableName;
        this.sqlStatement = sqlStatement;
    }

    @Override
    public void writeOutput(DataPipe cr) {
        try {
            os.write(getSqlFormatted(cr.getDataMap()).getBytes());
            os.write("\n".getBytes());
        } catch (IOException e) {
            log.error("IOException in SqlWriter", e);
        }
    }

    /**
     * Given an array of variable names, returns a sql statement {@link String}
     * of values.
     *
     * @param dataMap an map containing variable names and their corresponding values
     * names.
     * @return values in Sql format
     */
    //INSERT/UPDATE INTO schema.table (key1, key2) VALUES ("value1","valu2");
    public String getSqlFormatted(Map<String, String> dataMap) {
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
            values.append(dataMap.get(var));
        }
        return query.append(sqlStatement).append(" INTO ").append(schema).append(".")
                .append(tableName).append(" (").append(keys).append(") ").append("VALUES")
                .append(" (").append(values).append(");").toString();
    }
}
