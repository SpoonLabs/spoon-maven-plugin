package fr.inria.gforge.spoon.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public final class ClasspathHacker {
	private ClasspathHacker() {
	}

	public static void addURL(URLClassLoader urlClassLoader, URL u) throws IOException {
		Class<URLClassLoader> classLoaderClass = URLClassLoader.class;

		try {
			Method method = classLoaderClass.getDeclaredMethod("addURL",
					new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(urlClassLoader, new Object[] { u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException(
					"Error, could not add URL to system classloader");
		}
	}
}