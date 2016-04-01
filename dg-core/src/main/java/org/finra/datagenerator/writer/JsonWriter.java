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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


/**
 * Orders result variables based on a template and writes them in Json format to a given OutputStream.
 *
 * Created by Mauricio Silva on 6/27/2015.
 */
public class JsonWriter implements BulkWriter {


    /**
     * Logger
     */
    protected static final Logger log = Logger.getLogger(JsonWriter.class);
    private final OutputStream os;
    private String[] outTemplate;
    private JSONArray jsonArray;

    /**
     * Constructor
     *
     * @param os          the output stream to use in writing
     * @param outTemplate the output template to format writing
     */
    public JsonWriter(final OutputStream os, final String[] outTemplate) {
        this.os = os;
        this.outTemplate = outTemplate;
        this.jsonArray = new JSONArray();
    }

    @Override
    public void writeOutput(DataPipe cr) {
        jsonArray.put(getJsonFormatted(cr.getDataMap()));
    }

    @Override
    public void finish() {
        try {
            os.write(jsonArray.toString(3).getBytes());
            os.write("\n".getBytes());
        } catch (IOException e) {
            log.error("IOException in JsonWriter", e);
        }
    }

    /**
     * Given an array of variable names, returns a JsonObject
     * of values.
     *
     * @param dataMap an map containing variable names and their corresponding values
     * names.
     * @return a json object of values
     */
    public JSONObject getJsonFormatted(Map<String, String> dataMap) {
        JSONObject oneRowJson = new JSONObject();

        for (int i = 0; i < outTemplate.length; i++) {
            oneRowJson.put(outTemplate[i], dataMap.get(outTemplate[i]));
        }


        return oneRowJson;
    }

}
