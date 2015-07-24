package fr.inria.gforge.spoon.configuration;

import fr.inria.gforge.spoon.Spoon;
import fr.inria.gforge.spoon.SpoonModel;
import fr.inria.gforge.spoon.properties.PropertiesBuilder;
import fr.inria.gforge.spoon.logging.ReportBuilder;
import fr.inria.gforge.spoon.object.Processor;
import fr.inria.gforge.spoon.util.LogWrapper;
import fr.inria.gforge.spoon.util.TemplateLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class XMLSpoonConfiguration extends AbstractSpoonConfigurationBuilder {
	/**
	 * Used when the plugin is launched with a xml file for the configuration,
	 * a spoon.xml file.
	 */
	private SpoonModel model;

	public XMLSpoonConfiguration(Spoon spoon, ReportBuilder reportBuilder, PropertiesBuilder propertiesBuilder,
			SpoonModel model) {
		super(spoon, reportBuilder, propertiesBuilder);
		this.model = model;
	}

	@Override
	public SpoonConfigurationBuilder addProcessors() {
		if (model != null && model.getProcessors() != null && !model
				.getProcessors().isEmpty()) {
			Processor[] processors = new Processor[model.getProcessors().size()];
			int i = 0;
			for (SpoonModel.Processor processor : model.getProcessors()) {
				Processor newProcessor = new Processor();
				newProcessor.setName(processor.getName());
				processors[i++] = newProcessor;
			}
			parameters.add("-p");
			parameters.add(buildProcessors(processors));
			reportBuilder.setProcessors(processors);
		}
		return this;
	}

	/**
	 * Builds the path for the list of processors.
	 */
	private String buildProcessors(Processor[] processors) {
		String[] processorNames = new String[processors.length];
		for(int i = 0; i < processors.length; i++) {
			processorNames[i] = processors[i].getName();
		}
		return implode(processorNames, File.pathSeparator);
	}

	@Override
	public SpoonConfigurationBuilder addTemplates() {
		if (model != null && model.getTemplates() != null && !model
				.getTemplates().isEmpty()) {
			parameters.add("-t");
			parameters.add(buildTemplates());
		}
		return this;
	}

	/**
	 * Builds the path for the list of templates.
	 */
	private String buildTemplates() {
		String[] templateString = new String[model.getTemplates().size()];
		for (int i = 0; i < (model.getTemplates().size()); i++) {
			String templateLoaded = loadTemplateFile(
					model.getTemplates().get(i));
			if (templateLoaded != null) {
				templateString[i] = templateLoaded;
			}
		}
		return implode(templateString, File.pathSeparator);
	}

	/**
	 * Loads the template file at the path given.
	 */
	private String loadTemplateFile(String templateName) {
		String name = templateName.replace('.', '/') + ".java";
		InputStream in = Spoon.class.getClassLoader().getResourceAsStream(name);
		String packageName = templateName.substring(0, templateName.lastIndexOf('.'));
		String fileName = templateName.substring(templateName.lastIndexOf('.') + 1) + ".java";
		try {
			return TemplateLoader.loadToTmpFolder(in, packageName, fileName)
					.getAbsolutePath();
		} catch (IOException e) {
			LogWrapper.warn(spoon, String.format("Template %s cannot be loaded.", templateName), e);
			return null;
		}
	}

}
