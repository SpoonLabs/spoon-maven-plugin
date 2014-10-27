package com.dooapp.logging;

import com.dooapp.Spoon;
import org.apache.maven.plugin.MojoExecutionException;
import spoon.Launcher;

import java.io.*;

/**
 * Created by gerard on 20/10/2014.
 */
public class PerformanceDecorator implements SpoonLauncherDecorator {

	private final Launcher launcher;
	private final Spoon spoon;

	public PerformanceDecorator(Spoon spoon, Launcher launcher) {
		this.spoon = spoon;
		this.launcher = launcher;
	}

	@Override
	public void execute() throws MojoExecutionException {
		PrintWriter writer = null;
		try {
			// Creates output file for performance.
			final String directories =
					spoon.getProject().getBuild().getDirectory()
							+ File.separator + "spoon-maven-plugin";
			System.err.println(directories);
			final String file =
					directories + File.separator + "spoon-performance.txt";
			final File directoryForPerformanceFile = new File(directories);
			final File performanceFile = new File(file);
			directoryForPerformanceFile.mkdirs();
			performanceFile.createNewFile();
			writer = new PrintWriter(performanceFile,
					"UTF-8");

			// Computes performance.
			long startTime = System.currentTimeMillis();
			launcher.run();
			long endTime = System.currentTimeMillis();
			long time = endTime - startTime;

			// Saves performance.
			writer.println(time);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("File not found.", e);
		} catch (UnsupportedEncodingException e) {
			throw new MojoExecutionException("Encoding unsupported.", e);
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Cannot create file to write performances.", e);
		} catch (Exception e) {
			throw new MojoExecutionException(
					"Exception during the spoonify of the target project.", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
