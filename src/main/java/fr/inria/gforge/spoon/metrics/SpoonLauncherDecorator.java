package fr.inria.gforge.spoon.metrics;

import org.apache.maven.plugin.MojoExecutionException;

public interface SpoonLauncherDecorator {

	/**
	 * Executes the launcher of spoon with some other stuffs specified on
	 * subclasses of this decorator.
	 */
	void execute() throws MojoExecutionException;

}
