package fr.inria.gforge.spoon.mojo;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;

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

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		final File[] files = dirOutputResults.listFiles();
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");
	}

	@Test
	public void testSpoonGoalWithAProcessor() throws Exception {
		File basedir = resources.getBasedir("processor");
		rule.executeMojo(basedir, "generate");

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		final WildcardFileFilter filter = new WildcardFileFilter("result-spoon-*.xml");
		final File[] files = dirOutputResults.listFiles((FileFilter) filter);
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");

		final File resultFileProcessor = new File(basedir, "target/spoon-maven-plugin/spoon-nb-statement.txt");
		assertThat(resultFileProcessor).exists();
	}

	@Test
	public void testSpoonGoalWithCustomPConfiguration() throws Exception {
		File basedir = resources.getBasedir("custom-configuration");
		rule.executeMojo(basedir, "generate");

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		final WildcardFileFilter filter = new WildcardFileFilter("result-spoon-*.xml");
		final File[] files = dirOutputResults.listFiles((FileFilter) filter);
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");

		File generateFiles = new File(basedir, "target/generate-source-with-spoon");
		assertThat(generateFiles).exists();
	}

	@Test
	public void testSpoonGoalWithSeveralInputs() throws Exception {
		File basedir = resources.getBasedir("hello-world-inputs");
		rule.executeMojo(basedir, "generate");

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		final File[] files = dirOutputResults.listFiles();
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");
	}
}
