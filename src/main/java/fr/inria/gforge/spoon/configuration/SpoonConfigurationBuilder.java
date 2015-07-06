package fr.inria.gforge.spoon.configuration;

public interface SpoonConfigurationBuilder {

	/**
	 * Adds input folder in parameters of spoon.
	 */
	SpoonConfigurationBuilder addInputFolder();

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
	SpoonConfigurationBuilder addSourceClasspath();

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
	 * Adds not assume a build only outdated files in parameters of spoon.
	 */
	SpoonConfigurationBuilder addBuildOnlyOutdatedFiles();

	/**
	 * Adds no copy resources in parameters of spoon
	 */
	SpoonConfigurationBuilder addNoCopyResources();

	/**
	 * Builds parameters for spoon.
	 */
	String[] build();

}
