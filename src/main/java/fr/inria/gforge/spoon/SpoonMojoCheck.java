package fr.inria.gforge.spoon;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@SuppressWarnings("UnusedDeclaration")
@Mojo(
		name = "check",
		defaultPhase = LifecyclePhase.VERIFY,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class SpoonMojoCheck extends SpoonMojoGenerate {

	@Override
	public String getOutputType() {
		return "nooutput";
	}
}
