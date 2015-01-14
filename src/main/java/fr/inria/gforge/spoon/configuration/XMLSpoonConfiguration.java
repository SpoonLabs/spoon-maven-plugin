package fr.inria.gforge.spoon.configuration;

import fr.inria.gforge.spoon.Spoon;
import fr.inria.gforge.spoon.SpoonModel;
import fr.inria.gforge.spoon.logging.ReportBuilder;
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

	public XMLSpoonConfiguration(Spoon spoon, ReportBuilder reportBuilder,
			SpoonModel model) {
		super(spoon, reportBuilder);
		this.model = model;
	}

	@Override
	public SpoonConfigurationBuilder addProcessors() {
		if (model != null && model.getProcessors() != null && !model
				.getProcessors().isEmpty()) {
			String[] processors = model.getProcessors().toArray(
					new String[model.getProcessors().size()]);
			parameters.add("-p");
			parameters.add(buildProcessors(processors));
			reportBuilder.setProcessors(processors);
		}
		return this;
	}

	/**
	 * Builds the path for the list of processors.
	 */
	private String buildProcessors(String[] processors) {
		return implode(processors, File.pathSeparator);
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
