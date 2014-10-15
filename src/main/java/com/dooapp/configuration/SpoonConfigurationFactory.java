package com.dooapp.configuration;

import com.dooapp.Spoon;
import com.dooapp.util.XMLLoader;

import java.io.InputStream;

/**
 * Created by gerard on 15/10/2014.
 */
public class SpoonConfigurationFactory {
	private static final String SPOON_CONFIGURATION_FILENAME = "spoon.xml";

	public SpoonConfigurationBuilder getConfig(Spoon spoon) throws Exception {
		final InputStream in = Spoon.class.getClassLoader()
				.getResourceAsStream(SPOON_CONFIGURATION_FILENAME);
		if (in != null) {
			// Spoon configuration file is in the classpath.
			spoon.getLog().info("Generate spoon with a spoon.xml file.");
			return new XMLSpoonConfiguration(spoon, XMLLoader.load(in));
		}
		// There aren't spoon configuration file, we use data given at the plugin.
		spoon.getLog().info("Generate spoon without a spoon.xml file.");
		return new SimpleSpoonConfiguration(spoon);
	}
}
