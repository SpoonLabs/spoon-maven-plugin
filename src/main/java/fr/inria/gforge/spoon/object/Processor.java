/**
 * fr.inria.gforge.spoon.object.Processor.java
 * <p/>
 * Copyright (c) 2007-2015 UShareSoft SAS, All rights reserved
 *
 * @author UShareSoft
 */
package fr.inria.gforge.spoon.object;

import java.util.Map;

public class Processor {

	private String name;
	private Map<String, String> properties;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
