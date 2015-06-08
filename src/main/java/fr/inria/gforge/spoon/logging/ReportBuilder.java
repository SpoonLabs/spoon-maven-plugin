package fr.inria.gforge.spoon.logging;

public interface ReportBuilder {

	/**
	 * Sets project name.
	 */
	ReportBuilder setProjectName(String name);

	/**
	 * Sets processors.
	 */
	ReportBuilder setProcessors(String[] processors);

	/**
	 * Sets name of the module.
	 */
	ReportBuilder setModuleName(String name);

	/**
	 * Sets input directory.
	 */
	ReportBuilder setInput(String input);

	/**
	 * Sets output directory.
	 */
	ReportBuilder setOutput(String output);

	/**
	 * Sets source classpath.
	 */
	ReportBuilder setSourceClasspath(String sourceClasspath);

	/**
	 * Sets performance metric.
	 */
	ReportBuilder setPerformance(long performance);

	/**
	 * Builds the report in a XML file.
	 */
	void buildReport();
}
