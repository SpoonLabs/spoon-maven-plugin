package fr.inria.gforge.spoon;

import fr.inria.gforge.spoon.configuration.SpoonConfigurationBuilder;
import fr.inria.gforge.spoon.configuration.SpoonConfigurationFactory;
import fr.inria.gforge.spoon.logging.ReportBuilder;
import fr.inria.gforge.spoon.logging.ReportFactory;
import fr.inria.gforge.spoon.metrics.PerformanceDecorator;
import fr.inria.gforge.spoon.metrics.SpoonLauncherDecorator;
import fr.inria.gforge.spoon.util.ClasspathHacker;
import fr.inria.gforge.spoon.util.LogWrapper;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.processing.ProcessorPropertiesImpl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedDeclaration")
@Mojo(
		name = "generate",
		defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class Spoon extends AbstractMojo {
	
	
	/**
	 * Tells to spoon that it should copy comments
	 */
	@Parameter(
			property = "enableComments",
			defaultValue = "false")
	private boolean enableComments;

	/**
	 * Input directory for Spoon.
	 */
	@Parameter(property = "folder.src")
	private File srcFolder;
	/**
	 * Input directories for Spoo,.
	 */
	@Parameter(property = "folder.src")
	private File[] srcFolders;
	/**
	 * Output directory where Spoon must generate his output (spooned source code).
	 */
	@Parameter(
			property = "folder.out",
			defaultValue = "${project.build.directory}/generated-sources/spoon")
	private File outFolder;
	/**
	 * Tells to spoon that it must not assume a full classpath.
	 */
	@Parameter(
			property = "noClasspath",
			defaultValue = "false")
	private boolean noClasspath;
	/**
	 * Tells to spoon we shouldn't rewrite source code in fully qualified mode.
	 */
	@Parameter(
			property = "withImports",
			defaultValue = "false")
	private boolean withImports;
	/**
	 * Tells to spoon that it must not assume a full classpath.
	 */
	@Parameter(
			property = "buildOnlyOutdatedFiles",
			defaultValue = "false")
	private boolean buildOnlyOutdatedFiles;
	/**
	 * Tells to spoon that it must not assume a full classpath.
	 */
	@Parameter(
			property = "noCopyResources",
			defaultValue = "false")
	private boolean noCopyResources;
	/**
	 * List of processors.
	 */
	@Parameter(property = "processors")
	private String[] processors;
	/**
	 * Active debug mode to see logs.
	 */
	@Parameter(
			property = "Debug mode",
			defaultValue = "false")
	private boolean debug;
	/**
	 * Active the compilation of original sources.
	 */
	@Parameter(
			property = "Compile original sources and not source spooned",
			defaultValue = "false")
	private boolean compileOriginalSources;
	/**
	 * Specify the java version for spoon.
	 */
	@Parameter(
			property = "Java version for spoon",
			defaultValue = "8")
	private int compliance;

	@Parameter
	private ProcessorProperties[] processorProperties;


    /**
     * Skip execution.
     * 
     * @since 2.6
     */
    @Parameter(
            property = "spoon.skip",
            defaultValue = "false")
    private boolean skip;

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
	    if (this.skip) {
	        return ;
	    }

		try {
			// Initializes builders for report and config of spoon.
			ReportBuilder reportBuilder;
			try {
				reportBuilder = ReportFactory.newReportBuilder(this);
			} catch (RuntimeException e) {
				LogWrapper.warn(this, e.getMessage(), e);
				return;
			}
			final SpoonConfigurationBuilder spoonBuilder = SpoonConfigurationFactory.getConfig(this, reportBuilder);

			// Saves project name.
			reportBuilder.setProjectName(project.getName());
			reportBuilder.setModuleName(project.getName());

			// Builds all parameters necessary.
			try {
				spoonBuilder.addInputFolder()
						.addOutputFolder()
						.addCompliance()
						.addNoClasspath()
						.addWithImports()
						.addBuildOnlyOutdatedFiles()
						.addNoCopyResources()
						.addSourceClasspath()
						.addEnableComments()
						.addProcessors()
						.addTemplates();
			} catch (RuntimeException e) {
				LogWrapper.warn(this, e.getMessage(), e);
				return;
			}
			addArtifactsInClasspathOfTargetClassLoader();

			// Initializes and launch launcher of spoon.
			final Launcher spoonLauncher = new Launcher();
			spoonLauncher.setArgs(spoonBuilder.build());

			if (processorProperties != null) {
				this.initSpoonProperties(spoonLauncher);
			}

			final SpoonLauncherDecorator performance = new PerformanceDecorator(reportBuilder, spoonLauncher);
			performance.execute();
			reportBuilder.buildReport();
		} catch (Exception e) {
			LogWrapper.error(this, e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void initSpoonProperties(Launcher launcher) throws MojoExecutionException {
		Environment environment = launcher.getEnvironment();

		for (ProcessorProperties processorProperties : this.getProcessorProperties()) {
			spoon.processing.ProcessorProperties properties = new ProcessorPropertiesImpl();

			Properties xmlProperties = processorProperties.getProperties();

			for (Object key : xmlProperties.keySet()) {
				String sKey = (String) key;
				String value = (String) xmlProperties.get(key);

				// split in order to capture list or maps
				// code inspired by: https://stackoverflow.com/a/1757107/750142
				String[] tokens = value.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

				Object content;
				if (tokens.length == 1) {
					content = tokens[0].replace("\"", "");
				} else {
					String firstContent = tokens[0];

					String regExSepMap = "^.[^\"]=.*$";
					boolean isMap = Pattern.matches(regExSepMap, firstContent);

					if (isMap) {
						Map tempContent = new HashMap();
						for (int i = 0; i < tokens.length; i++) {
							int equalsIndex = tokens[i].indexOf("=");
							if (equalsIndex == -1) {
								throw new MojoExecutionException("Error when parsing the following map property content: "+tokens[i]+". Please read the documentation.");
							}

							String mapKey = tokens[i].substring(0, equalsIndex);
							String mapValue = tokens[i].substring(equalsIndex+1);

							if (StringUtils.isNumeric(mapValue)) {
								try {
									int mapIntvalue = Integer.parseInt(mapValue);
									tempContent.put(mapKey, mapIntvalue);
								} catch (NumberFormatException e) {
									throw new MojoExecutionException("Error while reading numeric value: "+mapValue);
								}
							} else {
								mapValue = mapValue.replace("\"", "");
								tempContent.put(mapKey, mapValue);
							}
						}

						content = tempContent;
					} else {
						List tempContent = new ArrayList();
						for (int i = 0; i < tokens.length; i++) {
							if (StringUtils.isNumeric(tokens[i])) {
								try {
									int intValue = Integer.parseInt(tokens[i]);
									tempContent.add(intValue);
								} catch (NumberFormatException e) {
									throw new MojoExecutionException("Error while reading numeric value: "+tokens[i]);
								}
							} else {
								String listValue = tokens[i].replace("\"", "");
								tempContent.add(listValue);
							}
						}

						content = tempContent;
					}
				}

				properties.set(sKey, content);
			}

			environment.setProcessorProperties(processorProperties.getName(), properties);
		}
	}

	private void addArtifactsInClasspathOfTargetClassLoader() throws IOException {
		// Changes classpath of the target class loader.
		if (project.getArtifacts() == null || project.getArtifacts().isEmpty()) {
			LogWrapper.info(this, "There is not artifact in this project.");
		} else {
			for (Artifact artifact : project.getArtifacts()) {
				LogWrapper.debug(this, artifact.toString());
				ClasspathHacker.addFile(artifact.getFile());
			}
		}

		// Displays final classpath of the target classloader.
		LogWrapper.info(this, "Running spoon with classpath:");
		final URL[] urlClassLoader = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
		for (URL currentURL : urlClassLoader) {
			LogWrapper.info(this, currentURL.toString());
		}
	}

	public File getSrcFolder() {
		return srcFolder;
	}

	public File[] getSrcFolders() {
		return srcFolders;
	}

	public File getOutFolder() {
		return outFolder;
	}

	public boolean isNoClasspath() {
		return noClasspath;
	}

	public boolean isWithImports() {
		return withImports;
	}

	public boolean isBuildOnlyOutdatedFiles() {
		return buildOnlyOutdatedFiles;
	}

	public boolean isNoCopyResources() {
		return noCopyResources;
	}

	public String[] getProcessorsPath() {
		return processors;
	}

	public boolean isDebug() {
		return debug;
	}

	public boolean isCompileOriginalSources() {
		return compileOriginalSources;
	}

	public int getCompliance() {
		return compliance;
	}

	public MavenProject getProject() {
		return project;
	}

	public void setEnableComments(boolean enableComments) {
		this.enableComments = enableComments;
	}

	public boolean isEnableComments() {
		return enableComments;
	}

	public ProcessorProperties[] getProcessorProperties() {
		return processorProperties;
	}
}
