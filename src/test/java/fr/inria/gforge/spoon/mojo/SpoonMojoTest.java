package fr.inria.gforge.spoon.mojo;

import fr.inria.gforge.spoon.configuration.SpoonMavenPluginException;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import spoon.SpoonException;

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
	public void testSpoonGoalGenerateResultFileForProjectWithoutComments() throws Exception {
		File basedir = resources.getBasedir("hello-world-with-comments-disabled");
		rule.executeMojo(basedir, "generate");

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		final File[] files = dirOutputResults.listFiles();
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");
	}

	@Test
	public void testSpoonGoalGenerateResultFileForSimpleCommentedProject() throws Exception {
		File basedir = resources.getBasedir("hello-world-commented");
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

	@Test
	public void testSpoonGoalWithProperties() throws Exception {
		File basedir = resources.getBasedir("hello-world-with-properties");
		rule.executeMojo(basedir, "generate");

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();


		final File[] files = dirOutputResults.listFiles();
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");

		final File contentSource = new File(basedir, "target/generated-sources/spoon/fr/inria/gforge/spoon");
		assertThat(contentSource).exists();

		final File[] sourceFiles = contentSource.listFiles();
		assertThat(sourceFiles.length).isEqualTo(1);
		assertThat(sourceFiles[0].getName()).isEqualTo("NewName.java");
	}

	@Test
	public void testSpoonThrowException() throws Exception {
		File basedir = resources.getBasedir("hello-world-exception");
		try {
			rule.executeMojo(basedir, "generate");
		} catch (MojoExecutionException e) {
			assertThat(e.getCause().getCause()).isInstanceOf(SpoonException.class);
		}

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).doesNotExist();
	}
	@Ignore("source folder could point to a non existent folder early, asking maven for source folders removes this.")
	@Test
	public void testSpoonConfigThrowException() throws Exception {
		File basedir = resources.getBasedir("hello-world-config-exception");
		try {
			rule.executeMojo(basedir, "generate");
		} catch (MojoExecutionException e) {
			assertThat(e.getCause()).isInstanceOf(SpoonMavenPluginException.class);
		}

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).doesNotExist();
	}
	@Ignore("source folder could point to a non existent folder early, asking maven for source folders removes this.")
	@Test
	public void testSpoonConfigCatchException() throws Exception {
		File basedir = resources.getBasedir("hello-world-config-exception-ignored");
		rule.executeMojo(basedir, "generate");

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).doesNotExist();
	}

	@Test
	public void testSpoonNoSources() throws Exception {
		File basedir = resources.getBasedir("hello-world-no-sources");
		rule.executeMojo(basedir, "generate");

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).doesNotExist();
	}

	@Test
	public void testSpoonCheckGoal() throws Exception {
		File basedir = resources.getBasedir("hello-world");
		rule.executeMojo(basedir, "check");

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		final File contentSource = new File(basedir, "target/generated-sources/spoon/fr/inria/gforge/spoon");
		assertThat(contentSource).doesNotExist();
	}

	@Ignore
	@Test
	public void testSpoonGoalGenerateResultFileForMultimoduleProject() throws Exception {
		File basedir = resources.getBasedir("multi-module");
		rule.executeMojo(basedir, "generate");

		File dirOutputResults = new File(basedir, "module1/target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		File[] files = dirOutputResults.listFiles();
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");

		dirOutputResults = new File(basedir, "module2/target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		files = dirOutputResults.listFiles();
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");
	}

	@Ignore
	@Test
	public void testSpoonCheckGoalForMultimoduleProject() throws Exception {
		File basedir = resources.getBasedir("multi-module");
		rule.executeMojo(basedir, "check");

		File dirOutputResults = new File(basedir, "module1/target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		File[] files = dirOutputResults.listFiles();
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");

		File contentSource = new File(basedir, "module1/target/generated-sources/spoon/fr/inria/gforge/spoon");
		assertThat(contentSource).doesNotExist();

		dirOutputResults = new File(basedir, "module2/target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		files = dirOutputResults.listFiles();
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");

		contentSource = new File(basedir, "module2/target/generated-sources/spoon/fr/inria/gforge/spoon");
		assertThat(contentSource).doesNotExist();
	}

	/**
	 * The current implementation of this test does not work, but calling a "mvn compile" in the targeted project does work
	 * So please test it in doing so.
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testSpoonGoalGenerateResultFileForProjectWithGeneratedSources() throws Exception {
		File basedir = resources.getBasedir("antlr-example");

		rule.executeMojo(basedir, "generate");

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		final File[] files = dirOutputResults.listFiles();
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");
	}

	@Test
	public void testSpoonGoalWithTestClass() throws Exception {
		File basedir = resources.getBasedir("hello-world-with-test");
		rule.executeMojo(basedir, "generate");

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		final File[] files = dirOutputResults.listFiles();
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");

		final File contentSource = new File(basedir, "target/generated-sources/spoon/fr/inria/gforge/spoon");
		assertThat(contentSource).exists();

		final File[] sourceFiles = contentSource.listFiles();
		assertThat(sourceFiles.length).isEqualTo(1);
		assertThat(sourceFiles[0].getName()).isEqualTo("AppTest.java");
	}

	@Test
	public void testSpoonCheckGoalWithTest() throws Exception {
		File basedir = resources.getBasedir("hello-world-with-test");
		rule.executeMojo(basedir, "check");

		final File dirOutputResults = new File(basedir, "target/spoon-maven-plugin");
		assertThat(dirOutputResults).exists();

		final File contentSource = new File(basedir, "target/generated-sources/spoon/fr/inria/gforge/spoon");
		assertThat(contentSource).doesNotExist();

		final WildcardFileFilter filter = new WildcardFileFilter("result-spoon-*.xml");
		final File[] files = dirOutputResults.listFiles((FileFilter) filter);
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0].getName()).startsWith("result-spoon");
	}
}
