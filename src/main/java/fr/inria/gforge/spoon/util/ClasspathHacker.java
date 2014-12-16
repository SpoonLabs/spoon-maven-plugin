package fr.inria.gforge.spoon.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public final class ClasspathHacker {
	private ClasspathHacker() {
	}

	public static void addFile(String filename) throws IOException {
		addFile(new File(filename));
	}

	public static void addFile(File file) throws IOException {
		addURL(file.toURI().toURL());
	}

	public static void addURL(URL u) throws IOException {
		URLClassLoader systemClassLoader = (URLClassLoader) Thread
				.currentThread().getContextClassLoader();
		Class<URLClassLoader> classLoaderClass = URLClassLoader.class;

		try {
			Method method = classLoaderClass.getDeclaredMethod("addURL",
					new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(systemClassLoader, new Object[] { u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException(
					"Error, could not add URL to system classloader");
		}
	}
}