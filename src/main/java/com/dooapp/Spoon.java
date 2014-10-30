package com.dooapp;

import com.dooapp.configuration.SpoonConfigurationBuilder;
import com.dooapp.configuration.SpoonConfigurationFactory;
import com.dooapp.logging.ReportBuilder;
import com.dooapp.logging.ReportFactory;
import com.dooapp.metrics.PerformanceDecorator;
import com.dooapp.metrics.SpoonLauncherDecorator;
import com.dooapp.util.ClasspathHacker;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import spoon.Launcher;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created at 07/11/2013 11:39.<br>
 *
 * @author Christophe DUFOUR
 */
@Mojo(
		name = "generate",
		defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class Spoon extends AbstractMojo {

	/**
	 * Input directory for Spoon.
	 */
	@Parameter(
			property = "folder.src")
	private File srcFolder;
	/**
	 * Output directory where Spoon must generate his output (spooned source code).
	 */
	@Parameter(
			property = "folder.out",
			defaultValue = "${project.build.directory}/generated-sources/spoon")
	private File outFolder;
	/**
	 * Tells to spoon that it must preserve formatting of original source code.
	 */
	@Parameter(
			property = "formatting.preserve",
			defaultValue = "false")
	private boolean preserveFormatting;
	/**
	 * List of processors.
	 */
	@Parameter(
			property = "processors")
	private List<Processor> processors;
	/**
	 * Project spooned with maven information.
	 */
	@Parameter(
			defaultValue = "${project}",
			required = true,
			readonly = true)
	private MavenProject project;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			final String resultFilename =
					project.getBuild().getDirectory() + File.separator
							+ "spoon-maven-plugin" + File.separator
							+ "result-spoon.xml";
			final File resultFile = new File(resultFilename);
			if (resultFile.exists()) {
				getLog().warn("Project already spooned.");
				return;
			}

			// Builder for result file.
			final ReportBuilder reportBuilder = ReportFactory.newReportBuilder(
					this);
			// Builder for parameters of Spoon.
			final SpoonConfigurationBuilder spoonBuilder = SpoonConfigurationFactory
					.getConfig(this, reportBuilder);

			// Save project name.
			reportBuilder.setProjectName(project.getName());
			reportBuilder.setModuleName(project.getName());

			// Builds all parameters necessary.
			try {
				spoonBuilder.addInputFolder()
						.addOutputFolder()
						.addCompliance()
						.addPreserveFormatting()
						.addSourceClasspath()
						.addProcessors()
						.addTemplates();
			} catch (RuntimeException e) {
				getLog().warn(e.getMessage());
				return;
			}

			for (Processor processor : processors) {
				ClasspathHacker.addFile(processor.getJarFile());
			}
			// Changes class loader.
			if (project.getArtifacts() == null || project.getArtifacts()
					.isEmpty()) {
				getLog().info("There is not artifact in this project");
			} else {
				for (Artifact artifact : (Set<Artifact>) project
						.getArtifacts()) {
					getLog().info("Add dependency to classpath : " + artifact);
					getLog().info(
							"Add file to classpath : " + artifact.getFile());
					ClasspathHacker.addFile(artifact.getFile());
				}
			}

			// Displays class loader in log of the console.
			getLog().info("Running spoon with classpath : ");
			URL[] urlClassLoader = ((URLClassLoader) ClassLoader
					.getSystemClassLoader()).getURLs();
			for (URL currentURL : urlClassLoader) {
				getLog().info("" + currentURL);
			}

			// Initialize and launch launcher
			Launcher spoonLauncher = new Launcher();
			spoonLauncher.setArgs(spoonBuilder.build());
			final SpoonLauncherDecorator performance = new PerformanceDecorator(
					reportBuilder, spoonLauncher);
			performance.execute();
			reportBuilder.buildReport();
		} catch (Exception e) {
			getLog().warn(e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	public File getSrcFolder() {
		return srcFolder;
	}

	public File getOutFolder() {
		return outFolder;
	}

	public boolean isPreserveFormatting() {
		return preserveFormatting;
	}

	public List<String> getProcessorsPath() {
		return Lists.transform(processors, processorModelToPath);
	}

	public MavenProject getProject() {
		return project;
	}

	/**
	 * Transforms list of ProcessorModel to a list of processors paths.
	 */
	private final Function<Processor, String> processorModelToPath = new Function<Processor, String>() {
		public String apply(Processor i) {
			return i.getPath();
		}
	};
}
