package fr.inria.gforge.spoon.util;

import fr.inria.gforge.spoon.SpoonMojoGenerate;

public final class LogWrapper {

	private LogWrapper() {
	}

	public static void error(SpoonMojoGenerate mojo, String message, Throwable exception) {
		if (mojo.isDebug()) {
			mojo.getLog().error(message, exception);
		}
	}

	public static void warn(SpoonMojoGenerate mojo, String message) {
		if (mojo.isDebug()) {
			mojo.getLog().warn(message);
		}
	}

	public static void warn(SpoonMojoGenerate mojo, String message, Throwable exception) {
		if (mojo.isDebug()) {
			mojo.getLog().warn(message, exception);
		}
	}

	public static void info(SpoonMojoGenerate mojo, String message) {
		if (mojo.isDebug()) {
			mojo.getLog().info(message);
		}
	}

	public static void debug(SpoonMojoGenerate mojo, String message) {
		if (mojo.isDebug()) {
			mojo.getLog().debug(message);
		}
	}
}
