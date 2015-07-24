package fr.inria.gforge.spoon.properties;

import fr.inria.gforge.spoon.Spoon;
import fr.inria.gforge.spoon.logging.ReportBuilder;
import fr.inria.gforge.spoon.util.LogWrapper;

import java.io.File;
import java.util.Calendar;

public final class PropertiesFactory {

	private PropertiesFactory() {
	}

	/**
	 * Get a {@link ReportBuilder} to build a report.
	 */
	public static PropertiesBuilder newPropertiesBuilder(Spoon spoon) {
		final long timestamp = Calendar.getInstance().getTimeInMillis();
		final String resultFilename = spoon.getProject().getBuild().getDirectory()
				+ File.separator + "spoon-maven-plugin"
				+ File.separator + "properties-" + timestamp + File.separator;
		final File resultFile = new File(resultFilename);
		LogWrapper.info(spoon, String.format("Spoon properties directory: %s", resultFile.getParentFile().getAbsolutePath()));
		return new PropertiesBuilderImpl(resultFile);
	}

}
