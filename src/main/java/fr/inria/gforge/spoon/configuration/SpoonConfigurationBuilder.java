package fr.inria.gforge.spoon.configuration;

/**
 * Created by gerard on 15/10/2014.
 */
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
	 * Adds preserve formatting information in parameters of spoon.
	 */
	SpoonConfigurationBuilder addPreserveFormatting();

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
	 * Builds parameters for spoon.
	 */
	String[] build();

}
