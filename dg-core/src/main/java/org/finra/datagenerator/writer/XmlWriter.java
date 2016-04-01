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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Orders result variables based on a template and writes them in Xml format to a given OutputStream.
 *
 * Created by Mauricio Silva on 6/27/2015.
 */
public class XmlWriter implements BulkWriter {
    /**
     * Logger
     */
    protected static final Logger log = Logger.getLogger(XmlWriter.class);
    private final OutputStream os;
    private String[] outTemplate;
    private String rootNode;
    private String recordNode;
    private List<String> xmlRecords;

    /**
     * Constructor
     *
     * @param os          the output stream to use in writing
     * @param outTemplate the output template to format writing
     * @param rootNode    name of the root Node of the Xml output
     * @param recordNode  name of an individual record Node of the Xml output
     */
    public XmlWriter(final OutputStream os, final String[] outTemplate,
                     final String rootNode, final String recordNode) {
        this.os = os;
        this.outTemplate = outTemplate;
        this.rootNode = rootNode;
        this.recordNode = recordNode;
        this.xmlRecords = new ArrayList<String>();
    }

    @Override
    public void writeOutput(DataPipe cr) {
        xmlRecords.add(getXmlFormatted(cr.getDataMap()));
    }

    @Override
    public void finish() {
        try {
            os.write(appendXmlStartTag(rootNode).getBytes());
            for (String xml : xmlRecords) {
                os.write(appendXmlStartTag(recordNode).getBytes());
                os.write(xml.getBytes());
                os.write(appendXmlEndingTag(rootNode).getBytes());
            }
            os.write(appendXmlEndingTag(rootNode).getBytes());
            os.write("\n".getBytes());
        } catch (IOException e) {
            log.error("IOException in XmlWriter", e);
        }
    }

    /**
     * Given an array of variable names, returns an Xml String
     * of values.
     *
     * @param dataMap an map containing variable names and their corresponding values
     * names.
     * @param dataMap
     * @return values in Xml format
     */
    public String getXmlFormatted(Map<String, String> dataMap) {
        StringBuilder sb = new StringBuilder();
        for (String var : outTemplate) {
            sb.append(appendXmlStartTag(var));
            sb.append(dataMap.get(var));
            sb.append(appendXmlEndingTag(var));
        }
        return sb.toString();
    }

    /**
     * Helper xml start tag writer
     *
     * @param value the output stream to use in writing
     */
    private String appendXmlStartTag(String value)  {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(value).append(">");

        return sb.toString();
    }

    /**
     * Helper xml end tag writer
     *
     * @param value the output stream to use in writing
     */
    private String appendXmlEndingTag(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("</").append(value).append(">");

        return sb.toString();
    }
}
