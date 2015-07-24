/**
 * fr.inria.gforge.spoon.configuration.PropertiesBuilderImpl.java
 * <p/>
 * Copyright (c) 2007-2015 UShareSoft SAS, All rights reserved
 *
 * @author UShareSoft
 */
package fr.inria.gforge.spoon.properties;

import fr.inria.gforge.spoon.object.Processor;
import spoon.compiler.Environment;
import spoon.support.StandardEnvironment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class PropertiesBuilderImpl implements PropertiesBuilder {


	private final File propertiesDirectory;
	private final Map<String, String> commonProperties = new HashMap<String, String>();
	private final Map<String, Map<String, String>> processorProperties = new HashMap<String, Map<String, String>>();

	public PropertiesBuilderImpl(File propertiesDirectory) {
		this.propertiesDirectory = propertiesDirectory;
	}

	@Override
	public PropertiesBuilder setProcessors(Processor[] processors) {
		for (Processor processor : processors) {
			HashMap<String, String> properties = new HashMap<String, String>();
			if(processor.getProperties() != null) {
				properties.putAll(processor.getProperties());
			}
			processorProperties.put(processor.getName(), properties);
		}
		return this;
	}

	@Override
	public PropertiesBuilder setProperties(Map<String, String> properties) {
		commonProperties.putAll(properties);
		return this;
	}

	@Override
	public String getOutputDirectory() {
		return propertiesDirectory.getAbsolutePath();
	}

	@Override
	public void buildProperties() {
		for (Map.Entry<String, Map<String, String>> stringMapEntry : processorProperties.entrySet()) {
			File processorPropertiesFile = new File(propertiesDirectory, stringMapEntry.getKey() + StandardEnvironment.PROPERTIES_EXT);
			PropertiesDao propertiesDao = new PropertiesDaoImpl(processorPropertiesFile);
			Map<String, String> properties = new HashMap<String, String>();
			properties.putAll(commonProperties);
			properties.putAll(stringMapEntry.getValue());
			propertiesDao.setProperties(properties);
		}
	}
}
