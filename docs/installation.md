# Installation guide

[TOC]

## Software requirements

Before getting started to develop a first adapter with SIP the following requirements should be fulfilled:

### Java Virtual Machine

- Recommendation for using Java 8 or higher <https://www.java.com/en/download/manual.jsp>

### Maven

- Maven downloaded from <https://maven.apache.org/download.cgi>
- For the Maven documentation see <https://maven.apache.org/guides/>

### IDE

- IntelliJ - Download from <https://www.jetbrains.com/idea/download/#section=windows>
- Eclipse - Download from <https://www.eclipse.org/downloads/>

### Plugins

- Lombok with IntelliJ <https://projectlombok.org/setup/intellij>
- Lombok with Eclipse <https://projectlombok.org/setup/eclipse>
- Apache Camel with IntelliJ (recommendation) <https://plugins.jetbrains.com/plugin/9371-apache-camel>

## Create the project

### How to create a SIP Adapter from Maven Archetype

In order to make setting up a new integration adapter project an easy task, we have created a dedicated SIP (Adapter) Maven archetype, that gives the ability to quickly bootstrap a new integration adapter, along with all needed Maven dependencies and goodies, as well as the recommended, preferred organization of the project.

To make a long story short, you can create a SIP Adapter by using the following Maven command:

Creating an application using SIP archetype on the command line:

Mac OS
```shell
  mvn archetype:generate \
    -DarchetypeGroupId=de.ikor.sip.foundation \
    -DarchetypeArtifactId=sip-archetype \
    -DarchetypeVersion=<latest.sip-archetype.version> \
    -DgroupId=de.ikor.sip.adapter \
    -DartifactId=demo \
    -DprojectName=DemoAdapter \
    -Dversion=1.0.0-SNAPSHOT
```

Windows OS
```shell
  mvn archetype:generate ^
    -DarchetypeGroupId=de.ikor.sip.foundation ^
    -DarchetypeArtifactId=sip-archetype ^
    -DarchetypeVersion=<latest.sip-archetype.version> ^
    -DgroupId=de.ikor.sip.adapter ^
    -DartifactId=demo ^
    -DprojectName=DemoAdapter ^
    -Dversion=1.0.0-SNAPSHOT
```

When executing the command, pay attention to use the latest archetype version for the -DarchetypeVersion parameter.

The following parameters -DgroupId, -DartifactId, -DprojectName and -Dversion should be adjusted to better match your project,
as they set the properties for the adapter. It is recommended to follow the maven naming convention.

After executing maven command, you will be requested to insert additional parameters about project structure:

- **packageSuffix** is used to create project package name by concatenating its value to the groupId. There is a strict validation
  for using only lower case letters and it is meant to name the package by extending your groupId for only one word.

- **package** (optional) is a chance to override previous package naming and provide full package name. This can be skipped
  by leaving value empty. Following package naming convention is strongly recommended, otherwise your project will be
  created, but it will consist package naming errors.

- **systemConnector1**/**systemConnector2** are representing names of your connector modules inside the project.
  Naming recommendation is to use lower case letters and kebab-case.

- **systemConnector1Package**/**systemConnector2Package** are used to define package name suffix for the connectors. Notice that
  connector package name starts with prefix defined on **package** step.

- **useLombokDefault**/**useLombok** are properties used for including or excluding Lombok dependency in adapter.
  Parameter useLombokDefault is only used to include lombok by default. If you wish to exclude lombok you should set
  useLombok to anything other than 'y' or 'Y'.

After a successful build, a project with the 4 following modules will be created:

- {artifactId}-application
- {artifactId}-domain
- {systemConnector1}
- {systemConnector2}

Our recommendation for modules naming is shown on the following Partner Adapter example:

- partner-adapter-application
- partner-adapter-domain
- partner-connector-sap
- partner-connector-dopix

More information about Maven archetypes is available here:
[Maven Archetype](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html)
