package com.dooapp.metrics;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Created by gerard on 20/10/2014.
 */
public interface SpoonLauncherDecorator {

	/**
	 * Executes the launcher of spoon with some other stuffs specified on
	 * subclasses of this decorator.
	 */
	void execute() throws MojoExecutionException;

}
