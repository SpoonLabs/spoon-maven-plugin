package fr.inria.gforge.spoon.metrics;

import fr.inria.gforge.spoon.logging.ReportBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import spoon.Launcher;

public class PerformanceDecorator implements SpoonLauncherDecorator {

	private final Launcher launcher;
	private final ReportBuilder reportBuilder;

	public PerformanceDecorator(ReportBuilder reportBuilder, Launcher launcher) {
		this.reportBuilder = reportBuilder;
		this.launcher = launcher;
	}

	@Override
	public void execute() throws MojoExecutionException {
		// Computes performance.
		long startTime = System.currentTimeMillis();
		try {
			launcher.run();
		} catch (Exception e) {
			throw new MojoExecutionException(
					"Exception during the spoonify of the target project.", e);
		}
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;

		// Saves performance.
		reportBuilder.setPerformance(time);
	}
}
