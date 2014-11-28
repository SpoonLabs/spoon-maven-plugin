package fr.inria.gforge.spoon;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public final class SpoonMojoTest {
	@Rule
	public MojoRule rule = new MojoRule();
	@Rule
	public TestResources resources = new TestResources();

	@Test
	public void testSpoonGoalGenerateResultFileForSimpleProject() throws Exception {
		File basedir = resources.getBasedir("hello-world");
		rule.executeMojo(basedir, "generate");

		File resultFile = new File(basedir, "target/spoon-maven-plugin/result-spoon.xml");
		assertThat(resultFile).exists();
	}

	@Test
	public void testSpoonGoalGenerateResultFileForMultiModuleProject() throws Exception {
		File basedir = resources.getBasedir("multi-module/module1");
		rule.executeMojo(basedir, "generate");

		File resultFile = new File(basedir, "target/spoon-maven-plugin/result-spoon.xml");
		assertThat(resultFile).exists();
	}

	@Test
	public void testSpoonGoalWithAProcessor() throws Exception {
		File basedir = resources.getBasedir("processor");
		rule.executeMojo(basedir, "generate");

		File resultFile = new File(basedir, "target/spoon-maven-plugin/result-spoon.xml");
		File resultFileProcessor = new File(basedir, "target/spoon-maven-plugin/spoon-nb-statement.txt");
		assertThat(resultFile).exists();
		assertThat(resultFileProcessor).exists();
	}

	@Test
	public void testSpoonGoalWithCustomPConfiguration() throws Exception {
		File basedir = resources.getBasedir("custom-configuration");
		rule.executeMojo(basedir, "generate");

		File resultFile = new File(basedir, "target/spoon-maven-plugin/result-spoon.xml");
		File generateFiles = new File(basedir, "target/generate-source-with-spoon");
		assertThat(resultFile).exists();
		assertThat(generateFiles).exists();
	}
}
