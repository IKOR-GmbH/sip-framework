# SIP Framework version upgrade

[TOC]

## Upgrade from 1.0.0 to 2.0.0

The following text represents the complete guide how to update SIP framework version in your adapter from version 
`1.0.0` to `2.0.0`.

In case adapter is generated with archetype of `2.0.0` framework version, following steps are not necessary. In order 
that your adapter work completely successful, the following changes has to be done manually. Please follow the guide
step by step.

First step is to update the parent field `version` in project root `pom.xml` file:

```xml
<parent>
    <groupId>de.ikor.sip.foundation</groupId>
    <artifactId>sip-framework</artifactId>
    <version>2.0.0</version>    <!-- update -->
    <relativePath/>
</parent>
```

In the same file, profile fields `id` and `maxJdkVersion` should be updated to java version 11. Single lines necessary 
for updating are marked with `update` comment:

```xml
<profiles>
    <profile>
        <id>enforce-java-11</id>                <!-- update -->
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-enforcer-plugin</artifactId>
                    <version>3.0.0-M3</version>
                    <dependencies>
                        <dependency>
                            <groupId>org.codehaus.mojo</groupId>
                            <artifactId>extra-enforcer-rules</artifactId>
                            <version>1.3</version>
                        </dependency>
                    </dependencies>
                    <executions>
                        <execution>
                            <id>enforce-bytecode-version</id>
                            <goals>
                                <goal>enforce</goal>
                            </goals>
                            <configuration>
                                <rules>
                                    <enforceBytecodeVersion>
                                        <maxJdkVersion>11</maxJdkVersion>   <!-- update -->
                                    </enforceBytecodeVersion>
                                </rules>
                                <fail>true</fail>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

Next step is to update `pom.xml` file in adapter's application module. Change the parent field `version`:

```xml
<parent>
    <groupId>de.ikor.sip.foundation</groupId>
    <artifactId>sip-starter-parent</artifactId>
    <version>2.0.0</version>    <!-- update -->
    <relativePath/>
</parent>
```

In the same file modification to `spring-boot-maven-plugin` is necessary. Add goal `build-info` and it's additional
configuration. Simply copy the lines marked with comment `update` and add them to your code in a proper place:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <fork>false</fork>
        <skip>false</skip>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
                <goal>build-info</goal>     <!-- update -->
            </goals>
            <configuration>                         <!-- update -->
                <additionalProperties>                  <!-- update -->
                    <sipFrameworkVersion>2.0.0</sipFrameworkVersion>    <!-- update -->
                </additionalProperties>                 <!-- update -->
            </configuration>                        <!-- update -->
        </execution>
    </executions>
</plugin>
```

Newer version requires keeping your markdown files in adapter's application module under `main/resources/documents` directory.
It is strictly required to create `documents` folder and to keep markdown files inside, so the
[markdown files API](./core.md#sip-details-in-actuator-info-endpoint) can work properly.

If you intend to use SIP Test Kit, you should add `application.yml` in your application module `src/test/resources`
directory with test profile configured:

```yaml
spring:
  profiles:
    active: test
```

Additionally `logback-test.xml` should be added in the same test directory. You can find the content of this file 
[here](../sip-archetype/src/main/resources/archetype-resources/__rootArtifactId__-application/src/test/resources/logback-test.xml).