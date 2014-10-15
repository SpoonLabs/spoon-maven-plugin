package com.dooapp.configuration;

import com.dooapp.Spoon;

import java.io.File;

/**
 * Created by gerard on 15/10/2014.
 */
public class SimpleSpoonConfiguration
		extends AbstractSpoonConfigurationBuilder {

	protected SimpleSpoonConfiguration(Spoon spoon) {
		super(spoon);
	}

	@Override
	public SpoonConfigurationBuilder addProcessors() {
		final String[] processors = spoon.getProcessors();
		if (processors != null && processors.length != 0) {
			parameters.add("-p");
			parameters.add(implode(processors, File.pathSeparator));
		}
		return this;
	}

	@Override
	public SpoonConfigurationBuilder addTemplates() {
		return this;
	}
}
