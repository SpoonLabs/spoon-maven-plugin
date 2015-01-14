package fr.inria.gforge.spoon.util;

import fr.inria.gforge.spoon.Spoon;

public final class LogWrapper {

	private LogWrapper() {
	}

	public static void error(Spoon mojo, String message, Throwable exception) {
		if (mojo.isDebug()) {
			mojo.getLog().error(message, exception);
		}
	}

	public static void warn(Spoon mojo, String message) {
		if (mojo.isDebug()) {
			mojo.getLog().warn(message);
		}
	}

	public static void warn(Spoon mojo, String message, Throwable exception) {
		if (mojo.isDebug()) {
			mojo.getLog().warn(message, exception);
		}
	}

	public static void info(Spoon mojo, String message) {
		if (mojo.isDebug()) {
			mojo.getLog().info(message);
		}
	}

	public static void debug(Spoon mojo, String message) {
		if (mojo.isDebug()) {
			mojo.getLog().debug(message);
		}
	}
}
