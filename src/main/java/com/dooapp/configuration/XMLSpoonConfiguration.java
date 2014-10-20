package com.dooapp.configuration;

import com.dooapp.Spoon;
import com.dooapp.SpoonModel;
import com.dooapp.util.TemplateLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gerard on 15/10/2014.
 */
class XMLSpoonConfiguration extends AbstractSpoonConfigurationBuilder {
	/**
	 * Used when the plugin is launched with a xml file for the configuration,
	 * a spoon.xml file.
	 */
	private SpoonModel model;

	public XMLSpoonConfiguration(Spoon spoon, SpoonModel model) {
		super(spoon);
		this.model = model;
	}

	@Override
	public SpoonConfigurationBuilder addProcessors() {
		if (model != null && model.getProcessors() != null && !model
				.getProcessors().isEmpty()) {
			parameters.add("-p");
			parameters.add(buildProcessors());
		}
		return this;
	}

	/**
	 * Builds the path for the list of processors.
	 */
	private String buildProcessors() {
		return implode(
				model.getProcessors().toArray(
						new String[model.getProcessors().size()]),
				File.pathSeparator);
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
		String packageName = templateName.substring(0,
				templateName.lastIndexOf('.'));
		String fileName =
				templateName.substring(templateName.lastIndexOf('.') + 1)
						+ ".java";
		try {
			return TemplateLoader.loadToTmpFolder(in, packageName, fileName)
					.getAbsolutePath();
		} catch (IOException e) {
			spoon.getLog()
					.warn("Template " + templateName + " cannot be loaded.");
			return null;
		}
	}

}
