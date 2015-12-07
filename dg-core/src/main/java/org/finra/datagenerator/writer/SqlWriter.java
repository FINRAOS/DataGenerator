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
import org.finra.datagenerator.consumer.DataFormatter;

import java.io.IOException;
import java.io.OutputStream;


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
    private SqlStatement sqlStatement;

    /**
     * Sql statement types
     */
    public enum SqlStatement {
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
                     final String tableName, final SqlStatement sqlStatement) {
        this.os = os;
        this.outTemplate = outTemplate;
        this.schema = schema;
        this.tableName = tableName;
        this.sqlStatement = sqlStatement;
    }

    @Override
    public void writeOutput(DataFormatter cr) {
        try {
            os.write(cr.getSqlFormatted(outTemplate, schema, tableName, sqlStatement).getBytes());
            os.write("\n".getBytes());
        } catch (IOException e) {
            log.error("IOException in SqlWriter", e);
        }
    }
}
