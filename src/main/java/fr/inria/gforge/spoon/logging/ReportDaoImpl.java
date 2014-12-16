package fr.inria.gforge.spoon.logging;

import fr.inria.gforge.spoon.Spoon;
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

import static fr.inria.gforge.spoon.logging.ReportBuilderImpl.ReportKey;
import static fr.inria.gforge.spoon.logging.ReportBuilderImpl.ReportKey.*;

class ReportDaoImpl implements ReportDao {
	private final File resultFile;
	private final Spoon spoon;

	ReportDaoImpl(Spoon spoon) {
		final String resultFilename =
				spoon.getProject().getBuild().getDirectory()
						+ File.separator + "spoon-maven-plugin" + File.separator
						+ "result-spoon.xml";
		this.resultFile = new File(resultFilename);
		this.spoon = spoon;
	}

	@Override
	public void save(Map<ReportBuilderImpl.ReportKey, Object> reportsData) {
		try {
			if (!resultFile.getParentFile().exists()) {
				resultFile.getParentFile().mkdirs();
				resultFile.createNewFile();
			}
			report(reportsData);
			spoon.getLog().info(
					"Spoon report directory: " + resultFile.getParentFile()
							.getAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException("Error to save result of the plugin", e);
		}
	}

	private void report(Map<ReportBuilderImpl.ReportKey, Object> data)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory bFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = bFactory.newDocumentBuilder();

		// Adds all elements.
		final Document doc = docBuilder.newDocument();
		final Element root = addRoot(doc, data);
		addProcessors(doc, root, data);

		addElement(doc, root, data, INPUT, (String) data.get(INPUT));
		addElement(doc, root, data, OUTPUT, (String) data.get(OUTPUT));
		addElement(doc, root, data, SOURCE_CLASSPATH, (String) data.get(SOURCE_CLASSPATH));
		addElement(doc, root, data, PERFORMANCE, Long.toString((Long) data.get(PERFORMANCE)));

		// write the content into xml file
		TransformerFactory transfFactory = TransformerFactory.newInstance();
		Transformer transformer = transfFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(resultFile);
		transformer.transform(source, result);
	}

	/**
	 * Adds root element.
	 */
	private Element addRoot(Document document,
			Map<ReportBuilderImpl.ReportKey, Object> reportsData) {
		Element rootElement = document.createElement("project");
		if (reportsData.containsKey(ReportKey.PROJECT_NAME)) {
			rootElement.setAttribute("name",
					(String) reportsData.get(ReportKey.PROJECT_NAME));
		}
		document.appendChild(rootElement);
		return rootElement;
	}

	/**
	 * Adds processors, child of root element.
	 */
	private Element addProcessors(Document document, Element root,
			Map<ReportBuilderImpl.ReportKey, Object> reportsData) {
		if (reportsData.containsKey(ReportKey.PROCESSORS)) {
			// Adds root tag "processors".
			Element processors = document.createElement("processors");
			root.appendChild(processors);

			// Adds all processors in child of "processors" tag.
			String[] tabProcessors = (String[]) reportsData
					.get(ReportKey.PROCESSORS);
			for (String processor : tabProcessors) {
				Element current = document.createElement("processor");
				current.appendChild(document.createTextNode(processor));
				processors.appendChild(current);
			}
			return processors;
		}
		return null;
	}

	/**
	 * Generic method to add a element for a parent element given.
	 */
	private Element addElement(Document document, Element parent,
			Map<ReportKey, Object> reportsData, ReportKey key, String value) {
		if (reportsData.containsKey(key)) {
			Element child = document.createElement(key.name().toLowerCase());
			child.appendChild(document.createTextNode(value));
			parent.appendChild(child);
			return child;
		}
		return null;
	}
}
