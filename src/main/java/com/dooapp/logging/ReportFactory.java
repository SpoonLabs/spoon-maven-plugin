package com.dooapp.logging;

import com.dooapp.Spoon;
import org.apache.maven.project.MavenProject;

/**
 * Created by gerard on 27/10/2014.
 */
public final class ReportFactory {

	private ReportFactory() {
	}

	/**
	 * Get a {@link com.dooapp.logging.ReportBuilder} to build a report.
	 */
	public static ReportBuilder newReportBuilder(Spoon spoon) {
		return new ReportBuilderImpl(spoon);
	}

}
