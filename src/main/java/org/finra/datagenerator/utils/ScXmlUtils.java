package org.finra.datagenerator.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
						if (null != attributeName ) {
							values.add(attributeName.getNodeValue());
						}	
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Oops! Can't parse scxml file... Fix your test case, please!");
		}

		return values;
	}
	
	public static Map<String, Set<String>> getAttributesValuesForNodes(String documentXml, String nodeName, String propertyName) {
		Map<String, Set<String>> values = new HashMap<String, Set<String>>();
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
						if (null != attributeName ) {
							String grandParenAttribute = getGrandParentValue(element, "id");
							if (values.containsKey(grandParenAttribute)) {
								Set<String> valueSet = values.get(grandParenAttribute);
								valueSet.add(attributeName.getNodeValue());
							} else {
								Set<String> valueSet = new HashSet<String>();
								valueSet.add(attributeName.getNodeValue());
								values.put(grandParenAttribute, valueSet);
							}
						}	
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Oops! Can't parse scxml file... Fix your test case, please!");
		}

		return values;
	}
	
	public static String getGrandParentValue(Node element, String attributeName) {
		if (null != element) {
			Node parentNode = element.getParentNode();
			if (null != parentNode) {
				Node grandParentNode = parentNode.getParentNode();
				if (null != grandParentNode) {
					NamedNodeMap grandParentNodeAttributes = grandParentNode.getAttributes();
					if (null != grandParentNodeAttributes) {
						Node grandParentNodeAttribute = grandParentNodeAttributes.getNamedItem(attributeName);
						if (null != grandParentNodeAttribute ) {
							return grandParentNodeAttribute.getNodeValue();
						}
					}
				}
			}
		}
		return null;
	}
	
	public static Set<String> mapSetToSet(Map<String, Set<String>> input) {
		Set<String> result = new HashSet<String>();
		for (String mapSetKey : input.keySet()) {
			result.addAll(input.get(mapSetKey));
		}
		return result;
	}
}