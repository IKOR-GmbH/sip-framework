# IKOR SIP Archetype

[TOC]

## Description

This archetype creates a basic SIP Adapter project with a defined structure and necessary dependencies

## How to create a SIP Adapter using SIP Archetype

In order to make setting up a new integration adapter project an easy task, we have created a dedicated SIP (Adapter) Maven archetype, that gives the ability to quickly bootstrap a new integration adapter, along with all needed Maven dependencies and goodies, as well as the recommended, preferred organization of the project.

You can create a SIP Adapter by using the following Maven command:

```shell
  mvn archetype:generate -DarchetypeGroupId=de.ikor.sip.foundation -DarchetypeArtifactId=sip-archetype -DarchetypeVersion=<latest.sip-archetype.version> -DgroupId=de.ikor.sip.adapter -DartifactId=demo -DprojectName=DemoAdapter -Dversion=1.0.0-SNAPSHOT
```


When executing the command, pay attention to use the latest archetype version for the `-DarchetypeVersion` parameter.
Check available versions [here](https://search.maven.org/search?q=de.ikor.sip.foundation).

**Overview of the command**
```shell
mvn archetype:generate
-DarchetypeGroupId=de.ikor.sip.foundation
-DarchetypeArtifactId=sip-archetype
-DarchetypeVersion=<latest.sip-archetype.version>
-DgroupId=de.ikor.sip.adapter
-DartifactId=demo
-DprojectName=DemoAdapter
-Dversion=1.0.0-SNAPSHOT
```

The parameters `-DgroupId`, `-DartifactId`, `-DprojectName` and `-Dversion` should be adjusted to better match your project,
as they set the properties for the adapter. It is recommended to follow the maven naming convention.

After executing maven command, you will be requested to insert additional parameters about project structure:

- **packageSuffix** is used to create project package name by concatenating its value to the groupId. There is a strict validation
  for using only lower case letters and it is meant to name the package by extending your groupId for only one word.
- **package** (optional) is used to override previous package naming and provide full package name. This can be skipped by leaving value empty.
  It is strongly recommended to follow package naming convention, otherwise your project will be created but it will consist
  package naming errors.
- **systemConnector1**/**systemConnector2** are representing names of your connector packages inside the project.
- **useLombokDefault**/**useLombok** are properties used for including or excluding Lombok dependency in adapter.
  Parameter useLombokDefault is only used to include lombok by default. If you wish to exclude lombok you should set 
  useLombok to anything other than 'y' or 'Y'.

After a successful build, a project with the following structure will be created:

- SIPApplication.java
- common
  - domain
  - util
- connectors
  - {systemConnector1}
    - config
    - domain
    - processors
    - sink
    - transformers
    - validators
  - {systemConnector2}
    - config
    - domain
    - processors
    - sink
    - transformers
    - validators
    

More about packages and internal SIP structure you can find [here](./README.md).

More information about Maven archetypes is available here:
[Maven Archetype](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html)
