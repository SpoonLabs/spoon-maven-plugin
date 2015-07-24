/**
 * fr.inria.gforge.spoon.configuration.PropertiesBuilder.java
 * <p/>
 * Copyright (c) 2007-2015 UShareSoft SAS, All rights reserved
 *
 * @author UShareSoft
 */
package fr.inria.gforge.spoon.properties;

import fr.inria.gforge.spoon.object.Processor;

import java.util.Map;

public interface PropertiesBuilder {

	/**
	 * Sets processors.
	 */
	PropertiesBuilder setProcessors(Processor[] processors);

	/**
	 * Sets common properties.
	 */
	PropertiesBuilder setProperties(Map<String, String> properties);

	/**
	 * Gets output directory.
	 */
	String getOutputDirectory();

	/**
	 * Builds the report in a XML file.
	 */
	void buildProperties();
}
