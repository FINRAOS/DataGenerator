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


/**
 * Orders result variables based on a template and writes them in Xml format to a given OutputStream.
 *
 * Created by Mauricio Silva on 6/27/2015.
 */
public class XmlWriter implements NonStreamDataWriter {
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
        xmlRecords.add(cr.getXmlFormatted(outTemplate));
    }

    @Override
    public void finish() {
        try {
            writeOutputXmlStart(rootNode);
            for (String xml : xmlRecords) {
                writeOutputXmlStart(recordNode);
                os.write(xml.getBytes());
                writeOutputXmlEnd(recordNode);
            }
            writeOutputXmlEnd(rootNode);
        } catch (IOException e) {
            log.error("IOException in XmlWriter", e);
        }
    }

    /**
     * Helper xml start tag writer
     *
     * @param value the output stream to use in writing
     */
    private void writeOutputXmlStart(String value) throws IOException {
        os.write("<".getBytes());
        os.write(value.getBytes());
        os.write(">".getBytes());
    }

    /**
     * Helper xml end tag writer
     *
     * @param value the output stream to use in writing
     */
    private void writeOutputXmlEnd(String value) throws IOException {
        os.write("</".getBytes());
        os.write(value.getBytes());
        os.write(">".getBytes());
    }
}
