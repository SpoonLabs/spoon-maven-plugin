[![Maven Central](https://img.shields.io/maven-central/v/fr.inria.gforge.spoon/spoon-maven-plugin.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22fr.inria.gforge.spoon%22%20AND%20a%3A%22spoon-maven-plugin%22)
[![Build Status](https://travis-ci.org/SpoonLabs/spoon-maven-plugin.svg?branch=master)](https://travis-ci.org/SpoonLabs/spoon-maven-plugin)

# Spoon-maven-plugin

Spoon-maven-plugin is a maven plugin for performing code analysis or transformation during build. 

It can be used for instance:
* to implement architectural checks, design pattern check and make the build fail if required
* execute pre-compilation source code transformations, for instance to automatically add logging, error-handling code, dependency injection, etc.

To report an issue, please use the main Spoon issue tracker: <https://github.com/INRIA/spoon/issues>.

## Goals

The plugin provided two goals:
* `generate` to execute source-code transformation
* `check` to execute source-code analysis

Please note that the two goals take exactly the same arguments and configuration.
The only change is that `generate` will create java source files in the `target` directory, where `check` will only fail the build in case of analysis error.

## Basic usage

To use spoon-maven-plugin, you need to declare it on the `build` tag in the `pom.xml` file of your project and specify an execution during the `generate-source` phase of the maven lifecycle.

The usage below is the minimum to execute the plugin and run spoon on your project.

```xml
<build>
<plugins>
<plugin>
  <groupId>fr.inria.gforge.spoon</groupId>
  <artifactId>spoon-maven-plugin</artifactId>
  <version>LATEST</version><!-- replace LATEST by the value of the badge at the top of the README -->
  <executions>
    <execution>
      <phase>generate-sources</phase>
      <goals>
        <goal>generate</goal>
      </goals>
    </execution>
  </executions>
</plugin>
</plugins>
</build>
```

Consequently, when `mvn clean install` is run on your project, the source code is first rewritten by spoon before compilation.
Note that Spoon takes as input the original sources of the project, but also the sources generated by another tools (like antlr, for example).

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

## How to pass properties to a processor? 

Spoon allow to pass custom properties to the processor you want to use, directly in the `pom.xml`. 

For passing properties, one must:

1. Add annotated fields to the processor being configured from the POM
2. Add XML configuration for passing the values to those fields
3. Use JSON format to specify the value of the fields

For example, let us consider the following Processor that changes the name of a specific class. The usage of `Property` annotation for the fields (eg `@Property String oldClassName`) means that this field will be set through the POM.

```java
package my.app.pkg;

import spoon.processing.AbstractProcessor;
import spoon.processing.Property;
import spoon.reflect.declaration.CtClass;

public class ProcessorWithProperty extends AbstractProcessor<CtClass> {

    @Property
    String oldClassName;

    @Property
    String newClassName;

    @Override
    public void process(CtClass element) {
        if (element.getSimpleName().equals(this.oldClassName)) {
            element.setSimpleName(this.newClassName);
        }
    }
}
```

Then the following configuration sets the processor's fields:

```xml
<plugin>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-maven-plugin</artifactId>
    <configuration>
      <processors>
        <processor>my.app.pkg.ProcessorWithProperty</processor>
      </processors>
      <processorProperties>
        <processorProperty>
          <name>my.app.pkg.ProcessorWithProperty</name>
          <properties>
            <property>
              <name>oldClassName</name>
              <value>App</value>
            </property>
            <property>
              <name>newClassName</name>
              <value>NewName</value>
            </property>
          </properties>
        </processorProperty>
      </processorProperties>
    </configuration>
</plugin>
```
Please note that you have to specify for which processor the properties should be used with the `name` attribute (here `my.app.pkg.ProcessorWithProperty`).
Values must use JSON formatting.
Primitive types are supported as well as list, maps and custom types.
Value types are automatically inferred from their field type and JSON values are deserialized using Jackson library.
As en examples, list are created as JSON lists:

```xml
<value>["one","two","three","value containing a, comma"]</value>
```

Maps and objects are created like this:

```xml
<value>{"one":1,"two":2,"three":"a value with a,comma"}</value>
```

## How to change source and output folder?

By default, spoon-maven-plugin considers the source folders configured in the pom.xml.
Input folders are configured with two parameters: `includeTest` and `includeSource`.
If you want to include test sources set `<includeTest>true</includeTest>`.
Mavens source folder setting is extendable with Build Helper Maven Plugin, see https://www.mojohaus.org/build-helper-maven-plugin/index.html.

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

## How to make the build fail?

If you want to use a custom Processor for assessing properties (checkstyle-like, architectural rule checking, pattern checking, etc) and make the build fail if those properties are not verified, throw a `SpoonException` in the method `process()` of the processor. 


## How to analyze the plugin execution?

The plugin creates some reports about its context and the execution of spoon on your project. These reports are available according to your definition of the plugin.

- If you have a simple project, you can retrieve the report at this location: `target/spoon-maven-plugin/result-spoon.xml`.
- If you have a multi-module project, you can retrieve on each sub module at the some location: `target/spoon-maven-plugin/result-spoon.xml`.
- If you have a multi-module project but you didn't declared your plugin at the root `pom.xml` file, you can retrieve the report on each sub module from your declaration.

## How to skip generated sources?

If you want Spoon to not pass generated sources as input, just provide the following configuration:

```xml
<plugin>
  <groupId>fr.inria.gforge.spoon</groupId>
  <artifactId>spoon-maven-plugin</artifactId>
  <version>LATEST</version>
  <configuration>
    <skipGeneratedSources>true</skipGeneratedSources>
  </configuration>
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

or in command line: `mvn compile -DskipGeneratedSources=true`

It's also possible to skip the plugin execution using `skip` property as in :

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

## Command-line usage

If you add the following to your `~/.m2/settings.xml`

```xml
<pluginGroups>
    <pluginGroup>fr.inria.gforge.spoon</pluginGroup>
</pluginGroups>
```
Then, the plugin is automatically discovered ([through prefix resolution](https://maven.apache.org/guides/introduction/introduction-to-plugin-prefix-mapping.html)), and you can simply run `mvn spoon:generate`.

## Changelogs

* Version 3.3
  * Update to Spoon 8.1
  * Fixes noclasspath, see https://github.com/INRIA/spoon/issues/3325
 
