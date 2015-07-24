package fr.inria.gforge.spoon;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Simple class to store data loaded from XML config file
 */
@XmlType(name = "spoonModel")
@XmlRootElement(name = "spoonModel")
public class SpoonModel {
	private List<String> templates;
	private List<Processor> processors;
	private String fileGenerator;

	public String getFileGenerator() {
		return fileGenerator;
	}

	@XmlElement(name = "fileGenerator")
	public void setFileGenerator(String fileGenerator) {
		this.fileGenerator = fileGenerator;
	}

	public List<Processor> getProcessors() {
		return processors;
	}

	@XmlElementWrapper(name = "processors")
	@XmlElement(name = "processor")
	public void setProcessors(List<Processor> processors) {
		this.processors = processors;
	}

	public List<String> getTemplates() {
		return templates;
	}

	@XmlElementWrapper(name = "templates")
	@XmlElement(name = "template")
	public void setTemplates(List<String> templates) {
		this.templates = templates;
	}

	@XmlType(name = "processor")
	public static class Processor {
		private String name;

		public String getName() {
			return name;
		}

		@XmlElement(name = "name")
		public void setName(String name) {
			this.name = name;
		}
	}
}
