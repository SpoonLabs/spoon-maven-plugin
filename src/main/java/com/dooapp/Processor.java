package com.dooapp;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Created by gerard on 28/10/2014.
 */
public class Processor {
	@Parameter
	private String jarFile;
	@Parameter
	private String path;

	public String getJarFile() {
		return jarFile;
	}

	public String getPath() {
		return path;
	}
}
