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

public class ScXmlUtils {

    protected static final Logger log = Logger.getLogger(ScXmlUtils.class);

    public static Set<String> getAttributesValues(String documentXml, String nodeName, String propertyName) {
        Set<String> values = new HashSet<String>();
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
                        Node attributeName = attributes.getNamedItem("name");
                        if (null != attributeName) {
                            values.add(attributeName.getNodeValue());
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Error while parsing the SCXML file", e);
        } catch (SAXException e) {
            throw new RuntimeException("Error while parsing the SCXML file", e);
        } catch (IOException e) {
            throw new RuntimeException("Error while parsing the SCXML file", e);
        } catch (DOMException e) {
            throw new RuntimeException("Error while parsing the SCXML file", e);
        }

        return values;
    }
}
