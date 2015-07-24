package fr.inria.gforge.spoon.configuration;

import fr.inria.gforge.spoon.Spoon;
import fr.inria.gforge.spoon.properties.PropertiesBuilder;
import fr.inria.gforge.spoon.logging.ReportBuilder;
import fr.inria.gforge.spoon.util.LogWrapper;
import fr.inria.gforge.spoon.util.XMLLoader;

import java.io.InputStream;

public final class SpoonConfigurationFactory {
	private static final String SPOON_CONFIGURATION_FILENAME = "spoon.xml";

	private SpoonConfigurationFactory() {
	}

	public static SpoonConfigurationBuilder getConfig(Spoon spoon, ReportBuilder reportBuilder, PropertiesBuilder propertiesBuilder) throws Exception {
		final InputStream in = Spoon.class.getClassLoader().getResourceAsStream(SPOON_CONFIGURATION_FILENAME);
		if (in != null) {
			// Spoon configuration file is in the classpath.
			LogWrapper.info(spoon, "Generate spoon with a spoon.xml file.");
			return new XMLSpoonConfiguration(spoon, reportBuilder, propertiesBuilder, XMLLoader.load(in));
		}
		// There aren't spoon configuration file, we use data given at the plugin.
		LogWrapper.info(spoon, "Generate spoon without a spoon.xml file.");
		return new SimpleSpoonConfiguration(spoon, reportBuilder, propertiesBuilder);
	}
}
