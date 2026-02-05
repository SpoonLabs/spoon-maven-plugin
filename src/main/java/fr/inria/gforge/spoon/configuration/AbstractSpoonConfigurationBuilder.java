package fr.inria.gforge.spoon.configuration;

import fr.inria.gforge.spoon.SpoonMojoGenerate;
import fr.inria.gforge.spoon.logging.ReportBuilder;
import fr.inria.gforge.spoon.util.LogWrapper;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

abstract class AbstractSpoonConfigurationBuilder
		implements SpoonConfigurationBuilder {

    private static final String[] EMPTY_STRING_ARRAY = {};
    private static final String[] DEFAULT_INCLUDES = {"**/**"};

	protected final List<String> parameters = new LinkedList<String>();
	protected final SpoonMojoGenerate spoon;
	protected final ReportBuilder reportBuilder;
    private DirectoryScanner directoryScanner = null;

	protected AbstractSpoonConfigurationBuilder(SpoonMojoGenerate spoon,
			ReportBuilder reportBuilder) {
		this.spoon = spoon;
		this.reportBuilder = reportBuilder;
		if (this.spoon.getLog().isDebugEnabled()) {
			parameters.add("--level");
			parameters.add("DEBUG");
		} else if (this.spoon.getLog().isInfoEnabled()) {
			parameters.add("--level");
			parameters.add("INFO");
		}
	}

	@Override
	public SpoonConfigurationBuilder addInputFolder() throws SpoonMavenPluginException {
		final List<File> inputs = new ArrayList<>();
		if (spoon.isIncludeSrcDirectories()) {
            final File sourceDirectory = new File(spoon.getProject().getBuild().getSourceDirectory());
            if (sourceDirectory.exists()) {
                for (final File input : filterDirectory(sourceDirectory)) {
                    inputs.add(input);
                }
            }

            if (!spoon.getSkipGeneratedSources()) {
				for (final String compileSourceRootStr : spoon.getProject().getCompileSourceRoots()) {
                    final File compileSourceRoot = new File(compileSourceRootStr);
                    if (compileSourceRoot.exists()) {
                        for (final File input : filterDirectory(compileSourceRoot)) {
                            inputs.add(input);
                        }
                    }
				}
			}
		}

		if (spoon.isIncludeTestDirectories()) {
            final File testSourceDirectory = new File(spoon.getProject().getBuild().getTestSourceDirectory());
            if (testSourceDirectory.exists()) {
                for (final File input : filterDirectory(testSourceDirectory)) {
                    inputs.add(input);
                }
            }

            if (!spoon.getSkipGeneratedSources()) {
				for (final String testCompileSourceRootStr : spoon.getProject().getTestCompileSourceRoots()) {
                    final File testCompileSourceRoot = new File(testCompileSourceRootStr);
                    if (testCompileSourceRoot.exists()) {
                        for (final File input : filterDirectory(testCompileSourceRoot)) {
                            inputs.add(input);
                        }
                    }
				}
			}
		}

		if (inputs.isEmpty()) {
			throw new SpoonMavenPluginException(String.format("No input sources for %s project.", spoon.getProject().getName()));
		}

		final String inputsString = inputs.stream().map(File::getAbsolutePath).collect(Collectors.joining(File.pathSeparator));

		parameters.add("-i");
		parameters.add(inputsString);
		reportBuilder.setInput(inputsString);
		return this;
	}

    private File[] filterDirectory(final File dir) {
        if (spoon.getIncludes().isEmpty() && spoon.getExcludes().isEmpty()) {
            return new File[] { dir };
        }

        if (directoryScanner == null) {
            directoryScanner = setupScanner();
        }

        directoryScanner.setBasedir(dir);
        directoryScanner.scan();

        final String[] includedFiles = directoryScanner.getIncludedFiles();
        final File[] filtered = new File[includedFiles.length];
        for (int i = 0; i < includedFiles.length; i++) {
            filtered[i] = new File(dir, includedFiles[i]);
        }
        return filtered;
    }

    private DirectoryScanner setupScanner() {
        final DirectoryScanner scanner = new DirectoryScanner();

        if (!spoon.getIncludes().isEmpty()) {
            scanner.setIncludes(spoon.getIncludes().toArray(EMPTY_STRING_ARRAY));
        } else {
            scanner.setIncludes(DEFAULT_INCLUDES);
        }


        if (!spoon.getExcludes().isEmpty()) {
            String[] excludes = spoon.getExcludes().toArray(EMPTY_STRING_ARRAY);
            scanner.setExcludes(excludes);
        }

        return scanner;
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
		if (!spoon.isEnableComments()){
			parameters.add("--disable-comments");
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
	public SpoonConfigurationBuilder addSourceClasspath() throws SpoonMavenPluginException {
		final MavenProject project = spoon.getProject();
		List<String> compileClasspath;
		List<String> testClasspath;
		try {
			compileClasspath = project.getCompileClasspathElements();
			testClasspath = project.getTestClasspathElements();
		} catch (DependencyResolutionRequiredException e) {
			throw new SpoonMavenPluginException("Cannot get compile classpath elements.", e);
		}
		final StringBuilder classpath = new StringBuilder();
		if (compileClasspath.size() > 1) {
			// Start at one because we don't would like the first compile classpath.
			for (int i = 1; i < compileClasspath.size(); i++) {
				classpath.append(compileClasspath.get(i)).append(File.pathSeparatorChar);
			}
		}
		if (testClasspath.size() > 2) {
				for (int i = 2; i < testClasspath.size(); i++) {
				// start at two because 1 is target/test-classes and 2 is target/classes
				classpath.append(testClasspath.get(i)).append(File.pathSeparatorChar);
				}
		}
		if (classpath.length() != 0) {
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
			parameters.add("--cpmode");
			parameters.add("NOCLASSPATH");
		} else {
			parameters.add("--cpmode");
			parameters.add("FULLCLASSPATH");
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
