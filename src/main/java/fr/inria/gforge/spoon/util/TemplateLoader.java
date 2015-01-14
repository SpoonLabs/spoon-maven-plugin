package fr.inria.gforge.spoon.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class TemplateLoader {
	private static File tmpFolder;

	private TemplateLoader() {
	}

	private static File getTmpFolder() {
		if (tmpFolder == null) {
			tmpFolder = new File("Spoon");
			if (!tmpFolder.exists()) {
				tmpFolder.mkdir();
			}
		}
		return tmpFolder;
	}

	public static File loadToTmpFolder(InputStream in, String packageName,
			String fileName) throws IOException {
		File tmpFolder = getTmpFolder();
		String[] packageData = packageName.split("\\.");
		File f = tmpFolder;
		for (String p : packageData) {
			f = new File(f, p);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		File javaFile = new File(f, fileName);
		IOUtils.copy(in, new FileOutputStream(javaFile));
		return javaFile;
	}
}
