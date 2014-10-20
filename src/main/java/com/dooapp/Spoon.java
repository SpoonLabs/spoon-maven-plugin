package com.dooapp;

import com.dooapp.configuration.SpoonConfigurationBuilder;
import com.dooapp.configuration.SpoonConfigurationFactory;
import com.dooapp.logging.PerformanceDecorator;
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

import java.beans.IntrospectionException;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
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
			property = "folder.src",
			defaultValue = "${basedir}/src/main/java")
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
	private String[] processors;
	/**
	 * Project spooned with maven information.
	 */
	@Parameter(
			defaultValue = "${project}",
			required = true,
			readonly = true)
	private MavenProject project;
	/**
	 * Factory to get a spoon configuration to build parameters for spoon.
	 */
	private final SpoonConfigurationFactory factory = new SpoonConfigurationFactory();

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			final SpoonConfigurationBuilder builder = factory.getConfig(this);

			// Create output folder if it doesn't exist.
			if (!outFolder.exists()) {
				outFolder.mkdirs();
			}

			// Builds all parameters necessary.
			builder.addInputFolder()
					.addOutputFolder()
					.addCompliance()
					.addPreserveFormatting()
					.addSourceClasspath()
					.addProcessors()
					.addTemplates();

			// Changes class loader.
			if (project.getArtifacts() == null || project.getArtifacts()
					.isEmpty()) {
				getLog().info("There is not artifact in this project");
			} else {
				for (Artifact artifact : (Set<Artifact>) project
						.getArtifacts()) {
					getLog().debug("Add dependency to classpath : " + artifact);
					getLog().debug(
							"Add file to classpath : " + artifact.getFile());
					addURLToSystemClassLoader(
							artifact.getFile().toURI().toURL());
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
			spoonLauncher.setArgs(builder.build());
			new PerformanceDecorator(spoonLauncher).execute();
		} catch (Exception e) {
			getLog().warn(e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	public static void addURLToSystemClassLoader(URL url)
			throws IntrospectionException {
		URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class<URLClassLoader> classLoaderClass = URLClassLoader.class;
		try {
			Method method = classLoaderClass
					.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(systemClassLoader, new Object[] { url });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IntrospectionException(
					"Error when adding url to system ClassLoader ");
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

	public String[] getProcessors() {
		return processors;
	}

	public MavenProject getProject() {
		return project;
	}
}
