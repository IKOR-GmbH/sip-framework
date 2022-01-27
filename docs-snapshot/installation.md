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

## IntelliJ IDE setup

It is **important** to uncheck "Clear Output Directory On Rebuild" field which can be found under File -> Settings -> 
Build, Execution, Deployment -> Compiler. Keep in mind that this setup has to be done with every new adapter created
or new IntelliJ repository used.

We highly recommend following this setup! There is additional **WARNING** message by IntelliJ:

WARNING!
If option 'Clear output directory on rebuild' is enabled, the entire contents of directories where generated sources are stored WILL BE CLEARED on rebuild.

![Image of SIP connected systems](./img/intellij_setting_clear_on_rebuild.png?raw=true "IntelliJ rebuild settings")

## Create a SIP Adapter using SIP Archetype

Guide for creating a new SIP Adapter from archetype can be found [here](https://ikor-gmbh.github.io/sip-framework/archetype/).



