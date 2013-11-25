package com.dooapp;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import spoon.Launcher;
import spoon.processing.FileGenerator;
import spoon.reflect.Factory;
import spoon.reflect.declaration.CtElement;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created at 07/11/2013 11:39.<br>
 *
 * @author Christophe DUFOUR
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class Spoon extends AbstractMojo {
    @Parameter(property = "folder.src", defaultValue = "${basedir}/src/main/java")
    private File srcFolder;
    @Parameter(property = "folder.out", defaultValue = "${project.build.directory}/generated-sources/spoon")
    private File outFolder;
    @Parameter(property = "formatting.preserve", defaultValue = "false")
    private boolean preserveFormatting;
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;
    private SpoonModel model;
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            loadXMLSpoonFile();
            if (!outFolder.exists()) {
                outFolder.mkdirs();
            }
            List<String> parameters = new LinkedList<String>();
            parameters.add("-i");
            parameters.add(srcFolder.getAbsolutePath());
            parameters.add("-o");
            parameters.add(outFolder.getAbsolutePath());
            parameters.add("--compliance");
            //TODO load it from the project compilation level
            parameters.add("6");
            if (model.getProcessors() != null && !model.getProcessors().isEmpty()) {
                parameters.add("-p");
                parameters.add(buildProcessors());
            }
            if (model.getTemplates() != null && !model.getTemplates().isEmpty()) {
                parameters.add("-t");
                parameters.add(buildTemplates());
            }
            if (preserveFormatting) {
                parameters.add("-f");
            }
            if (getLog().isInfoEnabled()) {
                parameters.add("-v");
            }
            if (getLog().isDebugEnabled()) {
                parameters.add("--vvv");
            }
            if (project.getArtifacts() == null || project.getArtifacts().isEmpty()) {
                getLog().info("There is not artifact in this project");
            } else {
                for (Artifact artifact : (Set<Artifact>) project.getArtifacts()) {
                    getLog().debug("Add dependency to classpath : " + artifact);
                    getLog().debug("Add file to classpath : " + artifact.getFile());
                    addURLToSystemClassLoader(artifact.getFile().toURI().toURL());
                }
            }
            fixStrangeClassLoaderIssue();
            getLog().info("Running spoon with classpath : ");
            for (URL url : ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs()) {
                getLog().info(url + "");
            }
            getLog().info("Running spoon with parameters : ");
            getLog().info(parameters.toString());
            Launcher spoonLauncher = new Launcher(parameters.toArray(new String[parameters.size()])) {
                @Override
                protected Factory createFactory() {
                    Factory factory = super.createFactory();
                    if (getFileGenerator() != null) {
                        factory.getEnvironment().setDefaultFileGenerator(getFileGenerator());
                    }
                    return factory;
                }
                public FileGenerator<? extends CtElement> getFileGenerator() {
                    if (model.getFileGenerator() == null) {
                        getLog().info("loading default fileGenerator");
                        return null;
                    }
                    try {
                        getLog().info("loading fileGenerator : " + model.getFileGenerator());
                        return (FileGenerator<? extends CtElement>) Class.forName(model.getFileGenerator()).getConstructor(File.class).newInstance(getArguments().getFile("output"));
                    } catch (Exception e) {
                        getLog().error(e.getMessage(), e);
                        return null;
                    }
                }
            };
            spoonLauncher.run();
        } catch (Exception e) {
            getLog().warn(e.getMessage(), e);
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
    private void loadXMLSpoonFile() throws Exception {
        InputStream in = Spoon.class.getClassLoader().getResourceAsStream("spoon.xml");
        if (in == null) {
            throw new RuntimeException("Unable to run Spoon plugin without spoon.xml file in your classpath");
        }
        model = XMLLoader.load(in);
    }
    private String buildTemplates() throws IOException {
        String[] templateString = new String[model.getTemplates().size()];
        for (int i = 0; i < (model.getTemplates().size()); i++) {
            templateString[i] = loadTemplateFile(model.getTemplates().get(i));
        }
        return implode(templateString, File.pathSeparator);
    }
    private String buildProcessors() {
        return implode(model.getProcessors().toArray(new String[model.getProcessors().size()]), File.pathSeparator);
    }
    private java.lang.String implode(java.lang.String[] processor, java.lang.String pathSeparator) {
        java.lang.StringBuffer buffer = new java.lang.StringBuffer();
        for (int i = 0; i < (processor.length); i++) {
            buffer.append(processor[i]);
            if (i < ((processor.length) - 1)) {
                buffer.append(pathSeparator);
            }
        }
        return buffer.toString();
    }
    private String loadTemplateFile(String templateName) throws IOException {
        String name = templateName.replace('.', '/') + ".java";
        InputStream in = Spoon.class.getClassLoader().getResourceAsStream(name);
        String packageName = templateName.substring(0, templateName.lastIndexOf('.'));
        String fileName = templateName.substring(templateName.lastIndexOf('.') + 1) + ".java";
        return TemplateLoader.loadToTmpFolder(in, packageName, fileName).getAbsolutePath();
    }
    public void fixStrangeClassLoaderIssue() {
        URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        List<URL> systemUrl = Arrays.asList(systemClassLoader.getURLs());
        for (URL url : ((URLClassLoader) Spoon.class.getClassLoader()).getURLs()) {
            if (!systemUrl.contains(url)) {
                try {
                    addURLToSystemClassLoader(url);
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void addURLToSystemClassLoader(URL url) throws IntrospectionException {
        URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> classLoaderClass = URLClassLoader.class;
        try {
            Method method = classLoaderClass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(systemClassLoader, new Object[]{url});
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IntrospectionException("Error when adding url to system ClassLoader ");
        }
    }
}
