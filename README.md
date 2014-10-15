spoon-maven-plugin
==================

A maven plugin to run spoon

# Usage

You can spoon a project in the generate source phase in a maven install command with this following configuration of the plugin.

```
<build>
    <plugin>
        <groupId>com.dooapp</groupId>
        <artifactId>spoon-maven-plugin</artifactId>
        <version>1.2-SNAPSHOT</version>
        <executions>
            <execution>
                <phase>generate-sources</phase>
                <goals>
                    <goal>generate</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
<build>
```