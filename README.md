[![Build Status](https://travis-ci.org/SpoonLabs/spoon-maven-plugin.svg?branch=master)](https://travis-ci.org/SpoonLabs/spoon-maven-plugin)

# Spoon-maven-plugin

Spoon-maven-plugin is a maven plugin for performing code anaysis or transformation during build. It can be used for instance:
* to implement architectural checks, design pattern check and make the build fail if required
* execute pre-compilation source code transformations, for instance to automatically add logging, error-handling code, dependency injection, etc.

To report an issue, please use the main Spoon issue tracker: <https://github.com/INRIA/spoon/issues>.

## Download

Stable version available on Maven Central:

```xml
<dependency>
  <groupId>fr.inria.gforge.spoon</groupId>
  <artifactId>spoon-maven-plugin</artifactId>
  <version>2.4.1</version>
</dependency>
<pluginRepositories>
    <!-- required for JDT dependency -->
    <pluginRepository>
      <id>gforge.inria.fr-releases</id>
      <name>Maven Repository for Spoon releases</name>
      <url>http://spoon.gforge.inria.fr/repositories/releases/</url>
    </pluginRepository>
</pluginRepositories>

```

## Basic usage

To use spoon-maven-plugin, you need to declare it on the `build` tag in the `pom.xml` file of your project and specify an execution during the `generate-source` phase of the maven lifecycle.

The usage below is the minimum to execute the plugin and run spoon on your project.

```xml
<plugin>
  <groupId>fr.inria.gforge.spoon</groupId>
  <artifactId>spoon-maven-plugin</artifactId>
  <version>2.4.1</version>
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

Consequently, when `mvn clean install` is run on your project, the source code is first rewritten by spoon before compilation.

## How to add processors?

Spoon can use processors to analyse and transform source code.

To add processors, one must:

1. add a dependency in the `plugin` block.
2. add a processor with its full qualified name in the `configuration` block.

In the example below, we add processor `fr.inria.gforge.spoon.processors.CountStatementProcessor` and the dependency necessary to locate the processor.

```xml
<configuration>
  <processors>
    <processor>
      fr.inria.gforge.spoon.processors.CountStatementProcessor
    </processor>
  </processors>
</configuration>
<dependencies>
  <dependency>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-processors</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

## How to change source and output folder?

You can specify at spoon its input and output directories with, respectively, `srcFolder` and `outFolder` tags.

## How to compile original sources?

By default, spoon generate your source code and compile these sources but you can specify at the plugin that you want to compile your original sources with the tag `compileOriginalSources` sets to true.

## How to specify a custom version for Spoon?

Spoon maven plugin defines a default value for spoon's version but you can override it to another one.

For example, if you would like the version 2.4 of spoon and not the version 3.0, you must add the dependency below.

```xml
<plugin>
  <groupId>fr.inria.gforge.spoon</groupId>
  <artifactId>spoon-maven-plugin</artifactId>
  <executions>
    <execution>
      <phase>generate-sources</phase>
      <goals>
        <goal>generate</goal>
      </goals>
    </execution>
  </executions>
  <dependencies>
    <dependency>
      <groupId>fr.inria.gforge.spoon</groupId>
      <artifactId>spoon-core</artifactId>
      <version>2.4</version>
    </dependency>
  </dependencies>
</plugin>
```

## Reports

The plugin creates some reports about its context and the execution of spoon on your project. These reports are available according to your definition of the plugin.

- If you have a simple project, you can retrieve the report at this location: `target/spoon-maven-plugin/result-spoon.xml`.
- If you have a multi-module project, you can retrieve on each sub module at the some location: `target/spoon-maven-plugin/result-spoon.xml`.
- If you have a multi-module project but you didn't declared your plugin at the root `pom.xml` file, you can retrieve the report on each sub module from your declaration.

## Skipping

It's possible to skip the plugin execution using `skip` property as in :

```xml
<plugin>
  <groupId>fr.inria.gforge.spoon</groupId>
  <artifactId>spoon-maven-plugin</artifactId>
  <configuration>
    <skip>true</skip>
  </configuration>
</plugin>
```

or from command line using `-Dspoon.skip=true`.
