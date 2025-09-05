package fr.inria.gforge.spoon;

import fr.inria.gforge.spoon.configuration.SpoonConfigurationBuilder;
import fr.inria.gforge.spoon.configuration.SpoonConfigurationFactory;
import fr.inria.gforge.spoon.configuration.SpoonMavenPluginException;
import fr.inria.gforge.spoon.logging.ReportBuilder;
import fr.inria.gforge.spoon.logging.ReportFactory;
import fr.inria.gforge.spoon.metrics.PerformanceDecorator;
import fr.inria.gforge.spoon.metrics.SpoonLauncherDecorator;
import fr.inria.gforge.spoon.util.LogWrapper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.processing.ProcessorPropertiesImpl;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Mojo(
		name = "generate",
		defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		// Use the TEST scope so that both compile and test dependency artifacts are put in the Spoon classloader
		// so that Spoon processors can also analyze test files. Note that the test source folder is not
		// included by default and requires the usage of the includeTest configuration parameter.
		requiresDependencyResolution = ResolutionScope.TEST)
public class SpoonMojoGenerate extends AbstractMojo {
	/**
	 * Tells to spoon that it should copy comments
	 */
	@Parameter(
			property = "enableComments",
			defaultValue = "true")
	private boolean enableComments;

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

	@Parameter(
			property = "Output type",
			defaultValue = "classes"
	)
	private String outputType;

	@Parameter(
			property = "Skip the generated sources as source input for Spoon",
			defaultValue = "false"
	)
	private boolean skipGeneratedSources;

	@Parameter(
			property = "Prevent the build from crashing in case of Spoon error: a warning will only be triggered",
			defaultValue = "false"
	)
	private boolean skipSpoonErrors;

	@Parameter
	private ProcessorProperties[] processorProperties;

	@Parameter(
		property = "Include test directories as input. Test directories are resolved by maven.",
		defaultValue =  "false"
	)
	private boolean includeTest;

	@Parameter(
		property = "Include source directories as input. Source directories are resolved by maven.",
		defaultValue =  "true"
	)
	private boolean includeSource;

    /**
     * A list of inclusion filters for Spoon input.
     */
    @Parameter
    private Set<String> includes = new HashSet<>();

    /**
     * A list of exclusion filters for Spoon input.
     */
    @Parameter
    private Set<String> excludes = new HashSet<>();

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

	protected ReportBuilder reportBuilder;
	protected Launcher spoonLauncher;

	protected String[] buildArguments(SpoonConfigurationBuilder spoonConfigurationBuilder) throws SpoonMavenPluginException {
		spoonConfigurationBuilder.addInputFolder()
					.addOutputFolder()
					.addCompliance()
					.addNoClasspath()
					.addWithImports()
					.addBuildOnlyOutdatedFiles()
					.addNoCopyResources()
					.addSourceClasspath()
					.addEnableComments()
					.addProcessors()
					.addTemplates()
					.addOutputType();

		return spoonConfigurationBuilder.build();
	}

	protected void initMojo() throws Exception {
		// Initializes builders for report and config of spoon.
		try {
			this.reportBuilder = ReportFactory.newReportBuilder(this);
		} catch (RuntimeException e) {
			LogWrapper.warn(this, e.getMessage(), e);
			return;
		}
		final SpoonConfigurationBuilder spoonBuilder = SpoonConfigurationFactory.getConfig(this, this.reportBuilder);

		// Saves project name.
		this.reportBuilder.setProjectName(project.getName());
		this.reportBuilder.setModuleName(project.getName());

		// Initializes and launch launcher of spoon.
		this.spoonLauncher = new Launcher();

		this.spoonLauncher.setArgs(this.buildArguments(spoonBuilder));

		if (processorProperties != null) {
			this.initSpoonProperties(spoonLauncher);
		}
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
	    if (this.skip) {
	        return ;
	    }

		if (project.getPackaging().equals("pom")) {
			return ;
		}

		try {
			this.initMojo();
			final SpoonLauncherDecorator performance = new PerformanceDecorator(this.reportBuilder, this.spoonLauncher);
			performance.execute();
			reportBuilder.buildReport();
		} catch (SpoonMavenPluginException e) {
	    	if (this.getSkipSpoonErrors()) {
				LogWrapper.warn(this, e.getMessage()+"\n This project will be ignored.", e);
			} else {
				throw new MojoExecutionException(e.getMessage(), e);
			}
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

				properties.set(sKey, value);
			}

			environment.setProcessorProperties(processorProperties.getName(), properties);
		}
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

	public String getOutputType() {
		return outputType;
	}

	public boolean getSkipGeneratedSources() {
		return skipGeneratedSources;
	}

	/**
	 * @return the includeSrcDirectories
	 */
	public boolean isIncludeSrcDirectories() {
		return includeSource;
	}

    public Set<String> getIncludes() {
        return includes;
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    /**
	 * @return the includeTestDirectories
	 */
	public boolean isIncludeTestDirectories() {
		return includeTest;
	}

	public boolean getSkipSpoonErrors() {
		return skipSpoonErrors;
	}
	
}
