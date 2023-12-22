# SIP Framework version upgrade

[TOC]

## Upgrade from 3.2.0 to 3.2.1

This is a bugfix version, so it's shouldn't introduce any braking changes. 
However, in the previous version, Integration Scenario model validation was triggered conditionally
depending on the presence of Connector's response model (instead of Integration Scenario's).
This has been changed, and the response transformation in the connector is now triggered unconditionally as well.

A runtime **validation** error will occur if an Adapter relied on that condition and **didn't conform** to Scenario's **domain model**.

## Upgrade from 3.1.0 to 3.2.0
Version 3.2.0 introduces changes in dependencies - most notably Spring 6, Apache Camel 4 and CXF 4. 
There should be no breaking changes in this release. If the adapter developers relied on deprecated features from underlying frameworks then some changes are necessary but those should be fixed on case-by-case basis.

Upgrade guide for specific use-cases:
* Adapters using **SOAP** Connectors

From adapter pom.xml file remove this plugin:
```xml
<plugin>
  <groupId>de.codecentric</groupId>
  <artifactId>cxf-spring-boot-starter-maven-plugin</artifactId>
...
</plugin>
```
and change it to:
```xml
<plugin>
  <groupId>org.apache.cxf</groupId>
  <artifactId>cxf-codegen-plugin</artifactId>
</plugin>
```
Plugin is now managed in the parent pom so no further configuration is necessary. It is a standard CXF plugin and adapter developers can customize its behaviour if needed.


* Adapters redefining **Spring Security**:
If the adapter has redefined Spring Security configuration, exclusion of SIP Security was necessary:
For example: 
```java
@SIPIntegrationAdapter(exclude = SIPSecurityAutoConfiguration.class)
```
That should no longer be necessary, SIP has upgraded to Spring Security 6 and should work with specific behaviour redefined in the adapter.


## Upgrade from 1.0.0 to 2.0.0

The following text represents the complete guide how to update SIP framework version in your adapter from version 
`1.0.0` to `2.0.0`.

In case adapter is generated with archetype of `2.0.0` framework version, following steps are not necessary. To insure
your adapter uses all functionalities, the following changes have to be done manually. Please follow the guide
step by step.

Single lines necessary for updating are marked with `update` comment.

First step is to update the parent field `version` in project root `pom.xml` file:

```xml
<parent>
    <groupId>de.ikor.sip.foundation</groupId>
    <artifactId>sip-framework</artifactId>
    <version>2.0.0</version>    <!-- update -->
    <relativePath/>
</parent>
```

In the same file, profile fields `id` and `maxJdkVersion` should be updated to java version 11.

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

In the same file, `spring-boot-maven-plugin` must be modified. Add goal `build-info` and it's additional
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

Newer version requires moving your markdown files in adapter's application module under `main/resources/documents` directory.
Furthermore, it is strictly required to create `documents` folder and to keep markdown files inside, so the
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