package fr.inria.gforge.spoon.util;

import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;

public final class ClasspathHacker {

	private static final Unsafe unsafeOp;

	static {
		Unsafe unsafe = null;
		for (Field f : Unsafe.class.getDeclaredFields()) {
			try {
				if (f.getType() == Unsafe.class && Modifier.isStatic(f.getModifiers())) {
					f.setAccessible(true);
					unsafe = (Unsafe) f.get(null);
				}
			} catch (Exception ignored) {
			}
		}
		unsafeOp = unsafe;
	}

	private ClasspathHacker() {
	}

	public static void addURL(URLClassLoader urlClassLoader, URL u) throws IOException {
		Class<URLClassLoader> classLoaderClass = URLClassLoader.class;
		try {
			Method method = classLoaderClass.getDeclaredMethod("addURL", URL.class);
			try {
				method.setAccessible(true);
				method.invoke(urlClassLoader, u);
			} catch (InaccessibleObjectException e) {
				if (unsafeOp != null)
					try {
						getPrivilegedMethodHandle(method).bindTo(urlClassLoader);
					} catch (Exception ex) {
						throw new IOException("Error, could not add URL to system classloader", e);
					}
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new IOException("Error, could not add URL to system classloader", e);
			}
		} catch (NoSuchMethodException e) {
			throw new IOException("Error, could not add URL to system classloader", e);
		}
	}

	// Get a privileged method handle for a given method to provide support for java 16+.
	private static MethodHandle getPrivilegedMethodHandle(Method method) throws IllegalAccessException {
		for (Field trustedLookup : MethodHandles.Lookup.class.getDeclaredFields()) {
			if (trustedLookup.getType() != MethodHandles.Lookup.class || !Modifier.isStatic(trustedLookup.getModifiers())
					|| trustedLookup.isSynthetic())
				continue;
			MethodHandles.Lookup lookup = (MethodHandles.Lookup) unsafeOp
					.getObject(unsafeOp.staticFieldBase(trustedLookup), unsafeOp.staticFieldOffset(trustedLookup));
			return lookup.unreflect(method);
		}
		throw new RuntimeException("Error, could not get privileged method handle.");
	}
}
