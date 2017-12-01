package fr.inria.gforge.spoon.configuration;

import fr.inria.gforge.spoon.SpoonMojoGenerate;
import fr.inria.gforge.spoon.logging.ReportBuilder;
import fr.inria.gforge.spoon.util.LogWrapper;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

abstract class AbstractSpoonConfigurationBuilder
		implements SpoonConfigurationBuilder {

	protected final List<String> parameters = new LinkedList<String>();
	protected final SpoonMojoGenerate spoon;
	protected final ReportBuilder reportBuilder;

	protected AbstractSpoonConfigurationBuilder(SpoonMojoGenerate spoon,
			ReportBuilder reportBuilder) {
		this.spoon = spoon;
		this.reportBuilder = reportBuilder;
		if (this.spoon.getLog().isInfoEnabled()) {
			parameters.add("--level");
			parameters.add("INFO");
		}
		if (this.spoon.getLog().isDebugEnabled()) {
			parameters.add("--level");
			parameters.add("DEBUG");
		}
	}

	@Override
	public SpoonConfigurationBuilder addInputFolder() {
		if (spoon.getSrcFolders().length > 0) {
			parameters.add("-i");
			String inputs = "";
			for (int i = 0; i < spoon.getSrcFolders().length; i++) {
				final File input = spoon.getSrcFolders()[i];
				if (!input.exists()) {
					throw new RuntimeException(input.getName() + " don't exist.");
				}
				inputs += input.getAbsolutePath();
				if (i != spoon.getSrcFolders().length - 1) {
					inputs += File.pathSeparatorChar;
				}
			}
			parameters.add(inputs);
			reportBuilder.setInput(inputs);
			return this;
		}
		final String srcDir = spoon.getProject().getBuild().getSourceDirectory();
		final File srcDirFile = new File(srcDir);
		if (spoon.getSrcFolder() != null && spoon.getSrcFolder().exists()) {
			parameters.add("-i");
			parameters.add(spoon.getSrcFolder().getAbsolutePath());
			reportBuilder.setInput(spoon.getSrcFolder().getAbsolutePath());
			return this;
		} else if (srcDirFile.exists()) {
			parameters.add("-i");
			parameters.add(srcDir);
			reportBuilder.setInput(srcDir);
			return this;
		}
		throw new RuntimeException(String.format("No source directory for %s project.", spoon.getProject().getName()));
	}

	@Override
	public SpoonConfigurationBuilder addOutputFolder() {
		// Create output folder if it doesn't exist.
		if (!spoon.getOutFolder().exists()) {
			spoon.getOutFolder().mkdirs();
		}

		parameters.add("-o");
		parameters.add(spoon.getOutFolder().getAbsolutePath());
		if (!spoon.isCompileOriginalSources()) {
			spoon.getProject().getCompileSourceRoots().clear();
			spoon.getProject().addCompileSourceRoot(spoon.getOutFolder().getAbsolutePath());
		}
		reportBuilder.setOutput(spoon.getOutFolder().getAbsolutePath());
		return this;
	}
	
	@Override
	public SpoonConfigurationBuilder addEnableComments() {
		if (spoon.isEnableComments()){
			parameters.add("--enable-comments");
		}
		return this;
	}


	@Override
	public SpoonConfigurationBuilder addCompliance() {
		parameters.add("--compliance");
		parameters.add("" + spoon.getCompliance());
		return this;
	}

	@Override
	public SpoonConfigurationBuilder addSourceClasspath() {
		final MavenProject project = spoon.getProject();
		List<String> compileClasspath;
		try {
			compileClasspath = project.getCompileClasspathElements();
		} catch (DependencyResolutionRequiredException e) {
			throw new RuntimeException("Cannot get compile classpath elements.", e);
		}
		if (compileClasspath.size() > 1) {
			final StringBuilder classpath = new StringBuilder();
			// Start at one because we don't would like the first compile classpath.
			for (int i = 1; i < compileClasspath.size(); i++) {
				classpath.append(compileClasspath.get(i)).append(File.pathSeparatorChar);
			}
			LogWrapper.debug(spoon, String.format("Source classpath: %s", classpath.toString()));
			parameters.add("--source-classpath");
			parameters.add(classpath.toString());
			reportBuilder.setSourceClasspath(classpath.toString());
		}
		return this;
	}

	@Override
	public SpoonConfigurationBuilder addNoClasspath() {
		if (spoon.isNoClasspath()) {
			parameters.add("-x");
		}
		return this;
	}

	@Override
	public SpoonConfigurationBuilder addWithImports() {
		if (spoon.isWithImports()) {
			parameters.add("--with-imports");
		}
		return this;
	}

	@Override
	public SpoonConfigurationBuilder addBuildOnlyOutdatedFiles() {
		if (spoon.isBuildOnlyOutdatedFiles()) {
			parameters.add("--buildOnlyOutdatedFiles");
		}
		return this;
	}

	@Override
	public SpoonConfigurationBuilder addNoCopyResources() {
		if (spoon.isNoCopyResources()) {
			parameters.add("--no-copy-resources");
		}
		return this;
	}

	@Override
	public SpoonConfigurationBuilder addOutputType() {
		parameters.add("--output-type");
		parameters.add(spoon.getOutputType());
		return this;
	}

	@Override
	public String[] build() {
		LogWrapper.info(spoon, "Running spoon with parameters:");
		LogWrapper.info(spoon, parameters.toString());
		return parameters.toArray(new String[parameters.size()]);
	}

	/**
	 * Concatenates a tab in a string with a path separator given.
	 */
	protected String implode(String[] tabToConcatenate, String pathSeparator) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tabToConcatenate.length; i++) {
			builder.append(tabToConcatenate[i]);
			if (i < tabToConcatenate.length - 1) {
				builder.append(pathSeparator);
			}
		}
		return builder.toString();
	}
}
