package com.dooapp.logging;

import java.util.Map;

/**
 * Created by gerard on 27/10/2014.
 */
interface ReportDao {

	/**
	 * Saves results.
	 */
	void save(Map<ReportBuilderImpl.ReportKey, Object> reportsData);

}
