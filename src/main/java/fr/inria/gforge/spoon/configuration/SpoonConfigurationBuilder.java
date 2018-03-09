package fr.inria.gforge.spoon.configuration;

public interface SpoonConfigurationBuilder {
	
	
	/**
	 * Enables Comment generation
	 */

	SpoonConfigurationBuilder addEnableComments();

	/**
	 * Adds input folder in parameters of spoon.
	 */
	SpoonConfigurationBuilder addInputFolder() throws SpoonMavenPluginException;

	/**
	 * Adds output folder in parameters of spoon.
	 */
	SpoonConfigurationBuilder addOutputFolder();

	/**
	 * Adds compliance in parameters of spoon.
	 */
	SpoonConfigurationBuilder addCompliance();

	/**
	 * Adds source classpath in parameters of spoon.
	 */
	SpoonConfigurationBuilder addSourceClasspath() throws SpoonMavenPluginException;

	/**
	 * Adds processors in parameters of spoon.
	 */
	SpoonConfigurationBuilder addProcessors();

	/**
	 * Adds templates in parameters of spoon.
	 */
	SpoonConfigurationBuilder addTemplates();

	/**
	 * Adds not assume a full classpath in parameters of spoon.
	 */
	SpoonConfigurationBuilder addNoClasspath();

	/**
	 * Shouldn't rewrite source code in fully qualified mode.
	 */
	SpoonConfigurationBuilder addWithImports();

	/**
	 * Adds not assume a build only outdated files in parameters of spoon.
	 */
	SpoonConfigurationBuilder addBuildOnlyOutdatedFiles();

	/**
	 * Adds no copy resources in parameters of spoon
	 */
	SpoonConfigurationBuilder addNoCopyResources();

	/**
	 * Adds the output type
	 */
	SpoonConfigurationBuilder addOutputType();

	/**
	 * Builds parameters for spoon.
	 */
	String[] build();

}
