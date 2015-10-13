package fr.inria.gforge.spoon.unit;

import fr.inria.gforge.spoon.Spoon;
import fr.inria.gforge.spoon.configuration.SpoonConfigurationBuilder;
import fr.inria.gforge.spoon.configuration.SpoonConfigurationFactory;
import fr.inria.gforge.spoon.logging.ReportBuilder;
import fr.inria.gforge.spoon.logging.ReportFactory;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public final class SpoonConfigurationBuilderTest {
	@Rule
	public MojoRule rule = new MojoRule();
	@Rule
	public TestResources resources = new TestResources();

	@Test
	public void testConfigurationOfTheDefaultInputFolder() throws Exception {
		final File basedir = resources.getBasedir("hello-world");
		final SpoonConfigurationBuilder configurationBuilder = getSpoonConfigurationBuilder(basedir);

		final String[] config = configurationBuilder.addInputFolder().build();
		assertThat(config.length).isEqualTo(4);
		assertThat(config[0]).isEqualTo("--level");
		assertThat(config[1]).isEqualTo("INFO");
		assertThat(config[2]).isEqualTo("-i");
		assertThat(config[3]).isEqualTo(basedir + File.separator + "src" + File.separator + "main" + File.separator + "java");
	}

	@Test
	public void testConfigurationOfTheCustomInputFolder() throws Exception {
		final File basedir = resources.getBasedir("custom-configuration");
		final SpoonConfigurationBuilder configurationBuilder = getSpoonConfigurationBuilder(basedir);

		final String[] config = configurationBuilder.addInputFolder().build();
		assertThat(config.length).isEqualTo(4);
		assertThat(config[0]).isEqualTo("--level");
		assertThat(config[1]).isEqualTo("INFO");
		assertThat(config[2]).isEqualTo("-i");
		assertThat(config[3]).isEqualTo(
				basedir + File.separator + "src" + File.separator + "internal" + File.separator + "fr" + File.separator + "inria" + File.separator + "gforge" + File.separator + "spoon");
	}

	@Test
	public void testConfigurationOfTheDefaultOutputFolder() throws Exception {
		final File basedir = resources.getBasedir("hello-world");
		final SpoonConfigurationBuilder configurationBuilder = getSpoonConfigurationBuilder(basedir);

		final String[] config = configurationBuilder.addOutputFolder().build();
		assertThat(config.length).isEqualTo(4);
		assertThat(config[0]).isEqualTo("--level");
		assertThat(config[1]).isEqualTo("INFO");
		assertThat(config[2]).isEqualTo("-o");
		assertThat(config[3]).isEqualTo(basedir + File.separator + "target" + File.separator + "generated-sources" + File.separator + "spoon");
	}

	@Test
	public void testConfigurationOfTheCustomOutputFolder() throws Exception {
		final File basedir = resources.getBasedir("custom-configuration");
		final SpoonConfigurationBuilder configurationBuilder = getSpoonConfigurationBuilder(basedir);

		final String[] config = configurationBuilder.addOutputFolder().build();
		assertThat(config.length).isEqualTo(4);
		assertThat(config[0]).isEqualTo("--level");
		assertThat(config[1]).isEqualTo("INFO");
		assertThat(config[2]).isEqualTo("-o");
		assertThat(config[3]).isEqualTo(basedir + File.separator + "target" + File.separator + "generate-source-with-spoon");
	}

	@Test
	public void testConfigurationWithProcessors() throws Exception {
		final File basedir = resources.getBasedir("processor");
		final SpoonConfigurationBuilder configurationBuilder = getSpoonConfigurationBuilder(basedir);

		final String[] config = configurationBuilder.addProcessors().build();
		assertThat(config.length).isEqualTo(4);
		assertThat(config[0]).isEqualTo("--level");
		assertThat(config[1]).isEqualTo("INFO");
		assertThat(config[2]).isEqualTo("-p");
		assertThat(config[3]).isEqualTo("fr.inria.gforge.spoon.mojo.CountStatementProcessor");
	}

	private SpoonConfigurationBuilder getSpoonConfigurationBuilder(File basedir) throws Exception {
		final Spoon spoon = (Spoon) rule.lookupConfiguredMojo(basedir, "generate");
		final ReportBuilder reportBuilder = ReportFactory.newReportBuilder(spoon);
		return SpoonConfigurationFactory.getConfig(spoon, reportBuilder);
	}
}
