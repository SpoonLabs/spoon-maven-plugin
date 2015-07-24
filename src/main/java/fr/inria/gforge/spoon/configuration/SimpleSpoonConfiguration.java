package fr.inria.gforge.spoon.configuration;

import fr.inria.gforge.spoon.Spoon;
import fr.inria.gforge.spoon.properties.PropertiesBuilder;
import fr.inria.gforge.spoon.logging.ReportBuilder;
import fr.inria.gforge.spoon.object.Processor;

import java.io.File;

class SimpleSpoonConfiguration extends AbstractSpoonConfigurationBuilder {

	protected SimpleSpoonConfiguration(Spoon spoon,
			ReportBuilder reportBuilder, PropertiesBuilder propertiesBuilder) {
		super(spoon, reportBuilder, propertiesBuilder);
	}

	@Override
	public SpoonConfigurationBuilder addProcessors() {
		Processor[] processorsPath = spoon.getProcessorsPath();
		if (processorsPath != null && processorsPath.length != 0) {
			final String[] processors = new String[processorsPath.length];
			for(int i = 0; i < processorsPath.length; ++i) {
				processors[i] = processorsPath[i].getName();
			}
			parameters.add("-p");
			parameters.add(implode(processors, File.pathSeparator));
			reportBuilder.setProcessors(processorsPath);
		}
		return this;
	}

	@Override
	public SpoonConfigurationBuilder addTemplates() {
		return this;
	}
}
