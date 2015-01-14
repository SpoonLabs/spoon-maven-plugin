package fr.inria.gforge.spoon.logging;

import fr.inria.gforge.spoon.Spoon;
import fr.inria.gforge.spoon.util.LogWrapper;

import java.io.File;
import java.util.Calendar;

public final class ReportFactory {

	private ReportFactory() {
	}

	/**
	 * Get a {@link fr.inria.gforge.spoon.logging.ReportBuilder} to build a report.
	 */
	public static ReportBuilder newReportBuilder(Spoon spoon) {
		final long timestamp = Calendar.getInstance().getTimeInMillis();
		final String resultFilename = spoon.getProject().getBuild().getDirectory()
				+ File.separator + "spoon-maven-plugin"
				+ File.separator + "result-spoon-" + timestamp + ".xml";
		final File resultFile = new File(resultFilename);
		LogWrapper.info(spoon, String.format("Spoon report directory: %s", resultFile.getParentFile().getAbsolutePath()));
		return new ReportBuilderImpl(resultFile);
	}

}
