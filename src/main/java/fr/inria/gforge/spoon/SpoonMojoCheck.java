package fr.inria.gforge.spoon;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;


@SuppressWarnings("UnusedDeclaration")
@Mojo(
		name = "check",
		defaultPhase = LifecyclePhase.VERIFY,
		// Use the TEST scope so that both compile and test dependency artifacts are put in the Spoon classloader
		// so that Spoon processors can also analyze test files. Note that the test source folder is not
		// included by default and requires the usage of the includeTest configuration parameter.
		requiresDependencyResolution = ResolutionScope.TEST)

public class SpoonMojoCheck extends SpoonMojoGenerate {
	@Override
	public String getOutputType() {
		return "nooutput";
	}

	@Override
	public boolean isCompileOriginalSources() {
		return true;
	}
}
