/**
 * fr.inria.gforge.spoon.configuration.PropertiesDaoImpl.java
 * <p/>
 * Copyright (c) 2007-2015 UShareSoft SAS, All rights reserved
 *
 * @author UShareSoft
 */
package fr.inria.gforge.spoon.properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

public class PropertiesDaoImpl implements PropertiesDao {

	private final File processorPropertiesFile;

	public PropertiesDaoImpl(File processorPropertiesFile) {
		this.processorPropertiesFile = processorPropertiesFile;
	}

	@Override
	public void setProperties(Map<String, String> properties) {
		try {
			if (!processorPropertiesFile.getParentFile().exists()) {
				processorPropertiesFile.getParentFile().mkdirs();
				processorPropertiesFile.createNewFile();
			}
			save(properties);
		} catch (Exception e) {
			throw new RuntimeException("Error to save result of the plugin", e);
		}
	}

	private void save(Map<String, String> properties) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory bFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = bFactory.newDocumentBuilder();

		// Adds all elements.
		final Document doc = docBuilder.newDocument();
		final Element root = addProperties(doc, properties);

		// write the content into xml file
		TransformerFactory transfFactory = TransformerFactory.newInstance();
		Transformer transformer = transfFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(processorPropertiesFile);
		transformer.transform(source, result);
	}

	/**
	 * Adds root element.
	 */
	private Element addProperties(Document document, Map<String, String> properties) {
		Element rootElement = document.createElement("properties");
		document.appendChild(rootElement);
		for (Map.Entry<String, String> stringStringEntry : properties.entrySet()) {
			addProperty(document, rootElement, stringStringEntry.getKey(), stringStringEntry.getValue());
		}

		return rootElement;
	}

	private void addProperty(Document document, Element element, String name, String value) {
		Element property = document.createElement("property");
		element.appendChild(property);
		property.setAttribute("name", name);
		property.setAttribute("value", value);
	}
}
