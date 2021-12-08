# System Integration Platform Framework

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=IKOR-GmbH_sip-framework&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=IKOR-GmbH_sip-framework)
[![Impressum](https://img.shields.io/badge/Imprint-ikor.one-blue)](https://ikor.one/impressum/)

The System Integration Platform (SIP) Framework is an IKOR product
developed from 10 project years of experience with standardized integration of core and peripheral systems.

The framework enables building light-weight integration adapters to achieve a technical and non-technical decoupling of
systems, using microservices and is therefore highly scalable.

It builds on [Apache Camel Framework](https://camel.apache.org/manual/) and extends it with a lot of usable features to create a standardized integration
approach for all adapters.

[TOC]

## What is SIP

**S**ystem **I**ntegration **P**latform is a combination of SIP framework, SIP management application and various implementation
guidelines and best practices. Combined they create an advantage when developing microservice integration adapters.
SIP adapters are specially designed as middleware integrators of specific subdomains of an enterprise. SIP project structure
and implementation guidelines provide higher degree of code consistency across different adapter instances, making them
much easier to maintain, manage and monitor. The goal is to have minimal set of restrictions of developer's freedom with
maximum comfort and efficiency while developing.

To start developing with SIP, we should first get familiar with its basic concepts.
Let's take a very simple scenario as an example. Say we have two systems working on the same domain (Partner, Policy, Billing
etc.), but they were never designed to work together, and suddenly there is a need to connect them.

![Image of Unconnected systems image](./img/SIP_readme_systems.svg?raw=true "Unconnected systems")

Both systems expose APIs, which are mutually not compatible, both by data model and/or communication technology
they use. SIP is designed as a standalone middleware app with a sole purpose to resolve exactly this kind of problems in
a flexible and standardized way. Actual integration scenarios may include more than two APIs, or even more than two
systems, but all the principles apply equally to such scenarios.

![Image of SIP connected systems](./img/SIP_readme_adapter.svg?raw=true "SIP connected systems")

The image below shows the modules structure of a single SIP adapter.
To provide a level of flexibility, SIP splits the problem into two, giving the possibility to develop and/or deploy connectors
for individual systems separately. The integration
logic is divided into three modules: **System A - Connector**, **System B - Connector** and **Domain**. The fourth module is the **Application**
module. Its purpose is to converge the three integration modules into one deployable and executable application.
The blue arrows represent dependencies of the application module.

![Image of SIP Adapter](./img/SIP_readme_adapter_detail.svg?raw=true "SIP Adapter")

**Domain**

The **Domain** module takes a central position in the image, because it provides the common data model for both systems.
Also, it separates the system connectors from each other to allow loose coupling between them. It should not contain any
integration logic but only simple Java objects representing the respective domain in which the system connectors of an adapter
operate. All connectors should adapt the data models of their systems to or from this common model, depending on data flow,
due to their incompatibilities. The domain can be seen as a kind of contract between the different system connectors which
ensures that they can communicate with each other. It contains common data model which uniforms the data models from all
integration sides.

**Connectors**

**Connectors** are designed to communicate with the associated external systems, thus all classes found in a connector
should only relate to their integration side. To enable this, their local domain objects are aligned with the API of an
external systems they communicate with. In order to send a message from one system connector
to another, the local domain objects must be mapped to the shared domain object. Furthermore, this means that a message
from system A is mapped to the shared domain object and then from the shared domain object to the model of system B and
vice versa, due to their bidirectional nature.
Notice the blue arrows pointing from the system connectors to the domain
module, meaning that connectors are absolutely independent of each other. A developer can work on or deploy only one side,
without the others being affected. This also means that changes on one of adapter's connector does not necessarily require
changes of the other. That's especially important if the affected connector is reused across multiple adapters.
In the picture domain A and domain B packages are shown optionally, together with corresponding mapper
component, since integrated systems use the same communication data model sometimes.

Each connector will have the following structure:

- `config` a place for any configuration classes
- `sink` here we should define Camel routes
- `transformers` it should contain classes for adapting the connector model to common domain model.
- `domain` (optional) it may contain the data model of the system.

**Application**

The Application module gathers all connector modules as one system and run them together.
Starting your adapter is done by running SIPApplication class in the application module.
Furthermore, its pom.xml must contain dependencies to all connector modules in adapter,
in order to start them. This module should not contain any integration logic, but it's a good place for implementing Spring
integration tests, such as default SIPApplicationTest, provided by archetype.

## Usage

### Framework components

- **[sip-archetype](./archetype.md)** - Archetype creates a basic SIP Adapter project with a defined structure and necessary dependencies. Project is created by executing single maven command.
- **[sip-core](./core.md)** - Core project for base SIP functionalities.
- **[sip-middle-component](./middle-component.md)** - Custom Camel component used as abstracted connector between different integration sides.
- **[sip-integration-starter](./integration-starter.md)** - Starter project adding necessary predefined dependencies for integration adapters.
- **[sip-starter-parent](./starter-parent.md)** - This project takes care of versions for Spring Boot and Camel dependencies.
- **[sip-security](./security.md)** - Security in SIP framework.

The following image displays how listed modules are utilized on SIP adapter where the up arrows represent inheritance,
down arrows dependencies.

![Image of SIP connected systems](./img/SIP_readme_dependencies.svg?raw=true "SIP connected systems")

### Framework features

Framework provides different features some of which are enabled by default. All the features are customizable and can be
overwritten or turned off by configuration. More about how to use them you can find under the corresponding module's
documentation.

- **[Actuator health check and metrics](./core.md#actuator-health-check-and-metrics)** - Out-of-the-box health checks for HTTP(S), JMS and FTP, SFTP and FTPS endpoints.
- **[Proxy for Apache Camel Processors](./core.md#proxy-for-apache-camel-processors)** - Proxies for Apache Camel processors with process and mock functionalities.
- **[Working with routes in runtime](./core.md#working-with-routes-in-runtime)** - Dynamical changing routes lifecycle.
- **[Logging Translation](./core.md#logging-translation)** - Translation of logging messages.
- **[Changing log level programmatically](./core.md#changing-log-level-programmatically)** - Dynamical changing of log level.
- **[Exchange tracing](./core.md#exchange-tracing)** - Tracing and storing exchanges on Camel Processor level.
- **[OpenAPI Descriptor](./core.md#openapi-descriptor)** - Built-in OpenAPI.
- **[SIP Middle component publish-subscribe](./middle-component.md#description)** - Multiple consumers on middle component.
- **[SIP Security](./security.md)** - Includes SSL setup, base and x509 authentication

## Getting started

Before development, check the following [Installation guide](installation.md).

Once you have your adapter you can do the following steps:

- Run `mvn clean install`
- Crate common Data Model inside domain module
- Add necessary dependencies to each module
- Add RouteBuilders inside "sink" package in connectors
- Add classes which transform system data models to or from common domain model in "transformers" package in connectors (if needed)
- Add any configuration classes for a specific system inside "config" package in connectors
- Add general integration configuration in application.yml found inside application module resources
- Run SIPApplication found inside application module
- After the application is up and running you can check SIP's management API under [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### How and why to use SIP Middle Component

[SIP Middle Component](./middle-component.md) (sipmc) is a key concept of SIP Framework.
As such it is used as a communication device between connectors.
To make a clear distinction between connectors, the point of separation can be seen with sipmc.
It marks the end or beginning of integration sides.
Currently, sipmc works as either 1 to 1 connector or publish subscribe,
based on the number of consumers, without any additional configuration.

### Adding additional Camel starters to the project

The Maven dependency management techniques implemented in the SIP archetype give you the ability to easily add new
camel-starter dependencies to integration adapters.

Should you need additional [Apache Camel Components](https://camel.apache.org/components/latest/) in the project, it is
enough to add their Spring Boot starter dependency, without version number - as it is managed in the parent Maven module
of the SIP integration adapter.

For instance, to add ArangoDb Camel component, it is enough to add the following dependency to the project:

```xml
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-arangodb-starter</artifactId>
</dependency>
```

The same stands for adding Spring Boot starters - as they are managed in the parent Maven module they too can be added
without explicitly stating version numbers. As a matter of fact, the overall dependency management performed by the
Spring Boot is in place in integration adapters too.

However, if you need to use some third-party libraries - there are chances that you will be responsible for managing
their versions in the application.
It is important to add the required dependency to the corresponding connector module's pom. Only that way connector modules
can keep their independence.

### Adding new System Connectors

By using the SIP archetype to create a new SIP adapter, by default there are two system connectors, designed to make it
more convenient to integrate systems. In case there are more than two systems, which need to be integrated, you need to add
additional modules to the project structure. There are a number of ways to add new system connectors to a SIP adapter.
These possibilities are explained in detail in the following part.

Initially each system connector module has two dependencies.
These are the domain module and the `sip-integration-starter`.
Make sure to add these to your `pom.xml` dependencies list.
Please observe this exemplary [pom.xml](#example-pom) to see how it should look like in a system connector module.

The module structure usually looks like this:

```text
fancy-sip-adapter
├───new-system-connector-module
│   ├───src/main/java/<package-path>
│   │   ├───config
│   │   ├───sink
│   │   └───transformer
│   └───pom.xml
...
```

**Copying Existing Module**

One way to add a system connector as a new module is to copy an existing module,
that has been created by using the SIP archetype to your project structure.
If the module was copied the `name` and `artifactId` in its `pom.xml` need to be adjusted.
Then the module name should be added to the `modules` element of the `pom.xml` of the main project.

```xml
<modules>
  <module>system-connector-A</module>
  <module>system-connector-B</module>
  <module>new-system-connector-module</module>
</modules>
```

Additionally, the copied module must be added as a dependency to the `pom.xml` of the application module.

```xml
<dependencies>
  <dependency>
    <groupId>de.ikor.sip.adapter</groupId>
    <artifactId>new-system-connector-module</artifactId>
    <version>${project.version}</version>
  </dependency>
  ...
</dependencies>
```

If necessary, refresh or rebuild the project so that the newly created module gets registered.

**Using IDEs Built-In Functionality**

- IntelliJ
    - Right click on the parent project folder and select `New > Module...`.
    - Select `Maven` in the left menu and click on the `Next` button
    - Provide a new module name and click on `Finish`

- Eclipse
    - `File > New > Other...`
    - Enter `maven  module` select `Maven Module` from the search result list and click on `Next`
    - Enter a module name select the parent project and press `Next`
    - Choose a maven archetype version from the list and click `Next` (You can use the default selection)
    - Check the archetype parameters and press `Finish`

The result is a new subdirectory of the SIP adapter project.
Compare the `pom.xml` of the newly created module with one of the modules that were created by SIP archetype.
There are only a few differences that need to be adjusted.

Make sure the newly created module is present in the `modules` element list of the main `pom.xml`
and that it has been added as a dependency to the application `pom.xml`.

**Example POM**

<a name="example-pom"/>

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>de.ikor.sip.adapter</groupId>
    <artifactId>systemadapter</artifactId>
    <version>1.0-SNAPSHOT</version>
  </parent>

  <artifactId>outbound</artifactId>
  <name>outbound</name>

  <dependencies>
    <dependency>
      <groupId>de.ikor.sip.foundation</groupId>
      <artifactId>sip-integration-starter</artifactId>
    </dependency>
    <dependency>
      <groupId>de.ikor.sip.adapter</groupId>
      <artifactId>systemadapter-domain</artifactId>
    </dependency>
  </dependencies>
</project>
```

### Development Tips

**Endpoint Configuration**

When it comes to working with URIs in routes, it is recommended to use property placeholders, which makes the routes configurable.
Additionally, it would make much sense to follow suggested configuration convention for defining endpoint configuration.

```yaml
endpoint:
  <in/out>:
   <adapter-name>:
    <external-system>:
     <endpoint>: # optional - if more endpoints on single external-system are involved in integration
      id: <adapterName>.<externalSystem>
      uri: ftp://...
```

`<in/out>` corresponds to consumers and producers respectively.
This means in case a message is received through a route using "from", then it is a consumer and "in" is used.
On the other hand, it is a producer when a message is sent via "to". In this case, "out" is used as key in the configuration file.  

`<adapter-name>` should correspond to the domain adapter it is dealing with (e.g. billing, partner, policy etc.)  

`<external-system>` should match the name of the system or client the adapter is communicating with.  

`<endpoint>` in case there are multiple endpoints for an adapter that uses the same domain and external system, additional identification
is required. For this purpose we use an additional endpoint key to provide distinction.  

For example:

```yaml
endpoint:
  in:
    partner:
      my-assurance-co:
        id: partner.my-assurance-co
        uri: ftp://...
  out:
    partner:
      their-assurance-co:
        id: partner.their-assurance-co
        uri: https://...
```

Using this configuration can be easily achieved in Camel by following their placeholder syntax.
Here's what the example from above would look like in the Camel route:

```java
from("{{endpoint.in.partner.my-assurance-co.uri}}")
    .id("{{endpoint.in.partner.my-assurance-co.id}}")
    .to(...);

from(...)
    .process(...)    
    .to("{{endpoint.out.partner.their-assurance-co.uri}}")
    .id("{{endpoint.out.partner.their-assurance-co.id}}")
```

If this convention is followed in the configuration, it leads to a unified structure that makes it possible
to identify at a single glance which systems are communicating with each other and which communication technologies are 
being used.
It also makes routes more descriptive and adapters much easier to maintain.

**Setting processor and route IDs**

As we can see each external endpoint, definition is followed by explicit setting of id. Although it's not mandatory,
doing so is highly recommended especially in case of outgoing endpoints. This will provide a reference of the external
endpoints, which can be used for different functionalities, like mocking, custom health check or other functionalities
that are yet to come.
Notice that in case of incoming endpoints (those in "from" statement), following id refers to the routeId.

### Configuration properties

By default, the following properties come as a part of SIP Framework, to override them simply add them to your configuration
file with desired values.

When using a yaml configuration file, which is already available in application module, adapt the properties to its format.

Name | Description | Value | Default |
--- | --- | --- | --- |
sip.core.translation.fileLocations | Sets locations of translation bundles | List | classpath:translations/translated-messages, classpath:translations/sip-core-messages |
sip.core.translation.default-encoding | Sets default encoding | String | UTF-8 |
sip.core.translation.fallback-to-system-locale | Use system language if none defined | boolean | false |
sip.core.translation.use-code-as-default-message | If key is not assigned use it in message | boolean | true |
sip.core.translation.lang | Set language of log messages | String | en |
sip.core.tracing.enabled | Enable SIP tracing and trace history | boolean | true |
sip.core.tracing.limit | Sets storage limit in trace history | number | 100 |
sip.core.tracing.exchange-formatter.{property-name} | Sets value for specific property in ExchangeFormatter | / | / |
management.endpoint.health.show-details | Enable health details in actuator | String | always |
management.endpoints.web.exposure.include | Set which endpoints are included | String | health,info,metrics,loggers,prometheus |
springdoc.show-actuator | Show actuator API in Swagger docs| boolean | true |
springdoc.api-docs.path | Custom path to API docs | String | /api-docs |
springdoc.swagger-ui.path | Custom path to Swagger | String | /swagger-ui.html |
springdoc.swagger-ui.disable-swagger-default-url | Disables default petstore API in swagger | boolean | true |
springdoc.api-docs.enabled | Enable/Disable API docs | boolean | true |
springdoc.swagger-ui.enabled | Enable/Disable swagger | boolean | true |
logging.level.root | Sets the default log level | String | INFO |