[![Build Status](https://travis-ci.org/SpoonLabs/spoon-maven-plugin.svg?branch=master)](https://travis-ci.org/SpoonLabs/spoon-maven-plugin)

# Spoon maven plugin

A maven plugin to run spoon on a target project.

## Usage

To execute Spoon maven plugin, you must declare it on the build tag in the `pom.xml` file of your project and specify an execution during the `generate-source` phase of the maven lifecycle.

The usage below is the minimum to execute the plugin and run spoon on your project.

```
<plugin>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-maven-plugin</artifactId>
    <version>${plugin.spoon.version}</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

After that, you can launch the command `mvn clean install` and the plugin will be automatically called.

## Inputs

You can configure some parameters in the plugin in the configuration tag of your plugin declaration:

```
<plugin>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-maven-plugin</artifactId>
    <version>${plugin.spoon.version}</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <!-- Your configuration -->
    </configuration>
</plugin>
```

### Source and output folder

You can specify at spoon its input and output directories with, respectively, `srcFolder` and `outFolder` tags.

### Formatting

You can preserving the formatting of your source code with the boolean tag `preserveFormatting`.

### Processors

Spoon can use processors to process some codes during its analysis of a source code. The plugin supports processors and can be specified as configuration in the declaration of the plugin.

> **Warning:** For each processor specified, you must specify a jar who contains the processor compiled.

In the next usage, we would like to launch the processor name `fr.inria.gforge.spoon.processors.CountStatementProcessor` (you must specify the full qualified name) and its compiled class in the jar file `/Users/you/.m2/repository/fr/inria/gforge/spoon/spoon-processors/1.0-SNAPSHOT/spoon-processors-1.0-SNAPSHOT.jar` (you must specify the path absolute).

```
<configuration>
    <processors>
        <processor>fr.inria.gforge.spoon.processors.CountStatementProcessor</processor>
    </processors>
    <jarFiles>
        <jarFile>/Users/you/.m2/repository/fr/inria/gforge/spoon/spoon-processors/1.0-SNAPSHOT/spoon-processors-1.0-SNAPSHOT.jar</jarFile>
    </jarFiles>
</configuration>
```

## Reports

The plugin creates some reports about its context and the execution of spoon on your project. These reports are available according to your definition of the plugin.

- If you have a simple project, you can retrieve the report at this location: `target/spoon-maven-plugin/result-spoon.xml`.
- If you have a multi-module project, you can retrieve on each sub module at the some location: `target/spoon-maven-plugin/result-spoon.xml`.
- If you have a multi-module project but you didn't declared your plugin at the root `pom.xml` file, you can retrieve the report on each sub module from your declaration.

## Download

At this time, this plugin isn't available on maven central or another nexus but it is in progress. You can clone this project, compile it on your computer and use it.
