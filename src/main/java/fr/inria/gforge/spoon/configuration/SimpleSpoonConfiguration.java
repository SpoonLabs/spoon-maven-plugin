package fr.inria.gforge.spoon.configuration;

import fr.inria.gforge.spoon.Spoon;
import fr.inria.gforge.spoon.logging.ReportBuilder;

import java.io.File;

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
