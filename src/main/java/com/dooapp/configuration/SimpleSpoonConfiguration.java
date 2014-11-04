package com.dooapp.configuration;

import com.dooapp.Spoon;
import com.dooapp.logging.ReportBuilder;

import java.io.File;
import java.util.List;

/**
 * Created by gerard on 15/10/2014.
 */
class SimpleSpoonConfiguration extends AbstractSpoonConfigurationBuilder {

	protected SimpleSpoonConfiguration(Spoon spoon,
			ReportBuilder reportBuilder) {
		super(spoon, reportBuilder);
	}

	@Override
	public SpoonConfigurationBuilder addProcessors() {
		final String[] processors = spoon.getProcessorsPath();
		if (processors != null && processors.length != 0) {
			parameters.add("-p");
			parameters.add(implode(processors, File.pathSeparator));
			reportBuilder.setProcessors(processors);
		}
		return this;
	}

	@Override
	public SpoonConfigurationBuilder addTemplates() {
		return this;
	}
}
