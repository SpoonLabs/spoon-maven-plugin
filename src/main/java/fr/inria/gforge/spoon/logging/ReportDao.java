package fr.inria.gforge.spoon.logging;

import java.util.Map;

interface ReportDao {

	/**
	 * Saves results.
	 */
	void save(Map<ReportBuilderImpl.ReportKey, Object> reportsData);

}
