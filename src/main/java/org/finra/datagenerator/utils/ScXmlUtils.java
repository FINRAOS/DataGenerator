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
package org.finra.datagenerator.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * SCXML utility class
 */
public final class ScXmlUtils {

    /**
     * Logger
     */
    protected static final Logger log = Logger.getLogger(ScXmlUtils.class);

    private ScXmlUtils() {

    }

    /**
     * Returns a set of attribute values appearing inside or under a given node
     *
     * @param documentXml a {@link String} containing an XML document
     * @param nodeName the name of the node under which to start searching for
     * the propertyName
     * @param propertyName a String containing the property name to search for
     * @return a {@link Set} of {@link String}s containing the values of the
     * propertyName searched for
     */
    public static Set<String> getAttributesValues(String documentXml, String nodeName, String propertyName) {
        Set<String> values = new HashSet<>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document scDocument = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(documentXml)));

            NodeList elements = scDocument.getElementsByTagName(nodeName);
            for (int i = 0; i < elements.getLength(); i++) {
                Node element = elements.item(i);
                if (null != element) {
                    NamedNodeMap attributes = element.getAttributes();
                    if (null != attributes) {
                        Node attributeName = attributes.getNamedItem(propertyName);
                        if (null != attributeName) {
                            values.add(attributeName.getNodeValue());
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            throw new RuntimeException("Error while parsing the SCXML file", e);
        }

        return values;
    }
}
