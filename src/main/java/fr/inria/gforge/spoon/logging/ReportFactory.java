package fr.inria.gforge.spoon.logging;

import fr.inria.gforge.spoon.Spoon;

/**
 * Created by gerard on 27/10/2014.
 */
public final class ReportFactory {

	private ReportFactory() {
	}

	/**
	 * Get a {@link fr.inria.gforge.spoon.logging.ReportBuilder} to build a report.
	 */
	public static ReportBuilder newReportBuilder(Spoon spoon) {
		return new ReportBuilderImpl(spoon);
	}

}
