# IKOR SIP Archetype

[TOC]

## Description

This archetype creates a basic SIP Adapter project with a defined structure and necessary dependencies

## How to create a SIP Adapter using SIP Archetype

In order to make setting up a new integration adapter project an easy task, we have created a dedicated SIP (Adapter) Maven archetype, that gives the ability to quickly bootstrap a new integration adapter, along with all needed Maven dependencies and goodies, as well as the recommended, preferred organization of the project.

To make a long story short, you can create a SIP Adapter by using the following Maven command:

```shell
  mvn archetype:generate -DarchetypeGroupId=de.ikor.sip.foundation -DarchetypeArtifactId=sip-archetype -DarchetypeVersion=<latest.sip-archetype.version> -DgroupId=de.ikor.sip.adapter -DartifactId=demo -DprojectName=DemoAdapter -Dversion=1.0.0-SNAPSHOT
```

<button class="btn btn-neutral" onclick="copyToClipboard()">Copy maven command</button>

<script>
function copyToClipboard() {
    var textToCopy = "mvn archetype:generate -DarchetypeGroupId=de.ikor.sip.foundation -DarchetypeArtifactId=sip-archetype -DarchetypeVersion=<latest.sip-archetype.version> -DgroupId=de.ikor.sip.adapter -DartifactId=demo -DprojectName=DemoAdapter -Dversion=1.0.0-SNAPSHOT";
    if (navigator.clipboard && window.isSecureContext) {
        return navigator.clipboard.writeText(textToCopy);
    } else {
        let textArea = document.createElement("textarea");
        textArea.value = textToCopy;
        textArea.style.position = "fixed";
        textArea.style.left = "-999999px";
        textArea.style.top = "-999999px";
        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();
        return new Promise((res, rej) => {
            document.execCommand('copy') ? res() : rej();
            textArea.remove();
        });
    }
}
</script>

When executing the command, pay attention to use the latest archetype version for the -DarchetypeVersion parameter.

**Overview of the command**
```shell
mvn archetype:generate
-DarchetypeGroupId=de.ikor.sip.cloud
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
- **systemConnector1**/**systemConnector2** are representing names of your connector modules inside the project. Naming recommendation is to use lower case letters and kebab-case.
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
- partner-connector-{arbitrary connector1 name}
- partner-connector-{arbitrary connector2 name}

More about modules and internal SIP structure you can find [here](./README.md).

More information about Maven archetypes is available here:
[Maven Archetype](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html)
