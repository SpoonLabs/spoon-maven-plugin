package com.dooapp.logging;

import com.dooapp.Spoon;
import org.apache.maven.project.MavenProject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gerard on 27/10/2014.
 */
class ReportBuilderImpl implements ReportBuilder {

	enum ReportKey {
		PROJECT_NAME, FRAGMENT_MODE, PROCESSORS, MODULE_NAME, INPUT, OUTPUT, SOURCE_CLASSPATH, PERFORMANCE
	}

	private final Map<ReportKey, Object> reportsData = new HashMap<ReportKey, Object>();
	private final ReportDao reportDao;

	ReportBuilderImpl(Spoon spoon) {
		reportDao = new ReportDaoImpl(spoon);
	}

	@Override
	public ReportBuilder setProjectName(String name) {
		reportsData.put(ReportKey.PROJECT_NAME, name);
		return this;
	}

	@Override
	public ReportBuilder setFragmentMode(boolean fragmentMode) {
		reportsData.put(ReportKey.FRAGMENT_MODE, fragmentMode);
		return this;
	}

	@Override
	public ReportBuilder setProcessors(String[] processors) {
		reportsData.put(ReportKey.PROCESSORS, processors);
		return this;
	}

	@Override
	public ReportBuilder setModuleName(String name) {
		reportsData.put(ReportKey.MODULE_NAME, name);
		return this;
	}

	@Override
	public ReportBuilder setInput(String input) {
		reportsData.put(ReportKey.INPUT, input);
		return this;
	}

	@Override
	public ReportBuilder setOutput(String output) {
		reportsData.put(ReportKey.OUTPUT, output);
		return this;
	}

	@Override
	public ReportBuilder setSourceClasspath(String sourceClasspath) {
		reportsData.put(ReportKey.SOURCE_CLASSPATH, sourceClasspath);
		return this;
	}

	@Override
	public ReportBuilder setPerformance(long performance) {
		reportsData.put(ReportKey.PERFORMANCE, performance);
		return this;
	}

	@Override
	public void buildReport() {
		reportDao.save(reportsData);
	}
}
