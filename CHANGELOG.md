# Changelog

All notable changes to this project will be documented in this file.

## \[Unreleased\]

For unreleased changelogs look into the [changelogs](./changelogs) directory

## 3.2.0 - 2023-09-22

### ⭐ Features
- Support for XML and JSON tree comparison in Test Kit validation phase [#236](https://github.com/IKOR-GmbH/sip-framework/pull/236) by [vladiIkor](https://github.com/vladiIkor)
- Updated error messages for connectors without parent class, connectors with overridden mappings, connectors with duplicate ids and show default request or response transformer message only once. [#231](https://github.com/IKOR-GmbH/sip-framework/pull/231) by [Nemikor](https://github.com/Nemikor)
- Set value of 'body' in test-kit-definition as reference to a file on the classpath [#235](https://github.com/IKOR-GmbH/sip-framework/pull/235) by [vladiIkor](https://github.com/vladiIkor)
- Upgrade to versions SpringBoot 3, Spring 6, Apache Camel 4, Apache CXF 4 [#230](https://github.com/IKOR-GmbH/sip-framework/pull/230) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Process level orchestration support [#234](https://github.com/IKOR-GmbH/sip-framework/pull/234) by [LetoBukarica](https://github.com/LetoBukarica)

### 🐞 Bugfixes
- Switched to using case insensitive IDs to find docs for Scenarios, Connectors and Groups. [#232](https://github.com/IKOR-GmbH/sip-framework/pull/232) by [Nemikor](https://github.com/Nemikor)
- Using RestTemplate in both SOAP and REST Test Kit invokers to avoid REST config from Camel. [#233](https://github.com/IKOR-GmbH/sip-framework/pull/233) by [Nemikor](https://github.com/Nemikor)
- Fixing snakeyaml dependency issue [#237](https://github.com/IKOR-GmbH/sip-framework/pull/237) by [LetoBukarica](https://github.com/LetoBukarica)
- Improved validation for inputs related to package naming in order to follow Java conventions. [#227](https://github.com/IKOR-GmbH/sip-framework/pull/227) by [Nemikor](https://github.com/Nemikor)
- Changed deprecated parent.version into project.parent.version in pom.xml of new adapters. [#228](https://github.com/IKOR-GmbH/sip-framework/pull/228) by [Nemikor](https://github.com/Nemikor)

### 📔 Documentation
- Update README.md with changes regarding v3 adapter structure and the concept of SIP Middle Component. [#229](https://github.com/IKOR-GmbH/sip-framework/pull/229) by [Nemikor](https://github.com/Nemikor)


## 3.1.0 - 2023-05-30

### ⭐ Features
- Scenario Orchestration DSL has been improved and now supports conditional statements [#222](https://github.com/IKOR-GmbH/sip-framework/pull/222) by [MartinBuchheim](https://github.com/MartinBuchheim)

### 🐞 Bugfixes
- Fixed bad indentation problems. Improved automatic resolving of value sipFrameworkVersion in pom.xml. [#218](https://github.com/IKOR-GmbH/sip-framework/pull/218) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Added a null check on SIP validator and updated the message. [#220](https://github.com/IKOR-GmbH/sip-framework/pull/220) by [LetoBukarica](https://github.com/LetoBukarica)
- Fixed bug caused by definition syntax exceptions and validation. [#225](https://github.com/IKOR-GmbH/sip-framework/pull/225) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Improved Test Kit definition validation. Added more detailed exceptions. [#220](https://github.com/IKOR-GmbH/sip-framework/pull/220) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Updated testkit regex matcher to use DOTALL pattern. [#224](https://github.com/IKOR-GmbH/sip-framework/pull/224) by [vladiIkor](https://github.com/vladiIkor)

### 📔 Documentation
- Add Test Kit with Declarative Structure documentation file. [#217](https://github.com/IKOR-GmbH/sip-framework/pull/217) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Fix Declarative Structure documentation file. [#223](https://github.com/IKOR-GmbH/sip-framework/pull/223) by [HaukeSchroederIkor](https://github.com/HaukeSchroederIkor)


## 3.0.0 - 2023-04-13

### 🚀 Major Changes
- Declarative structure [#215](https://github.com/IKOR-GmbH/sip-framework/pull/215) by [IKOR-GmbH](https://github.com/IKOR-GmbH)

### ⭐ Features
- Actuator endpoints which expose declarative structure components (endpoints, connectors, scenarios, groups) [#182](https://github.com/IKOR-GmbH/sip-framework/pull/182) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Connector and integration scenario Orchestrations [#214](https://github.com/IKOR-GmbH/sip-framework/pull/214) by [MartinBuchheimIkor](https://github.com/MartinBuchheimIkor)
- Custom base SIP exceptions [#179](https://github.com/IKOR-GmbH/sip-framework/pull/179) by [nikolag-ikor](https://github.com/nikolag-ikor)
- SOAP Connector base for declarative structure [#204](https://github.com/IKOR-GmbH/sip-framework/pull/204) by [MartinBuchheimIkor](https://github.com/MartinBuchheimIkor)
- Support for Connectors (declarative structure) in the Test Kit [#209](https://github.com/IKOR-GmbH/sip-framework/pull/209) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Declarative structure & SOAP support in the Archetype and Maven clean-up [#215](https://github.com/IKOR-GmbH/sip-framework/pull/215) by [LetoBukarica](https://github.com/LetoBukarica)

### 🐞 Bugfixes
- Updated the Developer tags in the pom.xml [#216](https://github.com/IKOR-GmbH/sip-framework/pull/216) by [LetoBukarica](https://github.com/LetoBukarica)
- Updated dependencies with vulnerabilities [#178](https://github.com/IKOR-GmbH/sip-framework/pull/178) by [Nemikor](https://github.com/Nemikor)
- Fixed an issue where spring test tries to load empty test kit tests. [#183](https://github.com/IKOR-GmbH/sip-framework/pull/183) by [Nemikor](https://github.com/Nemikor)
- Added Unicode/ICU License check [#205](https://github.com/IKOR-GmbH/sip-framework/pull/205) by [nikolag-ikor](https://github.com/nikolag-ikor)


## 2.4.0 - 2023-01-16

### ⭐ Features
- Add possibility to test camel routes, which use email component, with TestKit. [#147](https://github.com/IKOR-GmbH/sip-framework/pull/147) by [Nemikor](https://github.com/Nemikor)
- Add implementation for supporting jms component in Test Kit. [#146](https://github.com/IKOR-GmbH/sip-framework/pull/146) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Add implementation for supporting kafka component in Test Kit. [#150](https://github.com/IKOR-GmbH/sip-framework/pull/150) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Add TestKit invoker runtime exception [#151](https://github.com/IKOR-GmbH/sip-framework/pull/151) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Add mechanism which will automatically include SIP Actuator endpoints to configured list of Actuator endpoints [#149](https://github.com/IKOR-GmbH/sip-framework/pull/149) by [Nemikor](https://github.com/Nemikor)
- Refactor tracing mechanism to improve performance when logging trace messages is disabled. [#174](https://github.com/IKOR-GmbH/sip-framework/pull/174) by [Nemikor](https://github.com/Nemikor)
- Update version of commons-text to 1.10.0 [#152](https://github.com/IKOR-GmbH/sip-framework/pull/152) by [Nemikor](https://github.com/Nemikor)
- Update Camel version to 3.18.4 and SpringBoot version to 2.7.6 [#171](https://github.com/IKOR-GmbH/sip-framework/pull/171) by [nikolag-ikor](https://github.com/nikolag-ikor)

### 🐞 Bugfixes
- Add suppress warnings for unchecked casts which are safe. [#155](https://github.com/IKOR-GmbH/sip-framework/pull/155) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Add possibility to include headers in a response from a mocked processor for Test Kit tests. [#148](https://github.com/IKOR-GmbH/sip-framework/pull/148) by [Nemikor](https://github.com/Nemikor)
- Fixed coupling of Interceptor strategy ordering. [#145](https://github.com/IKOR-GmbH/sip-framework/pull/145) by [LetoBukarica](https://github.com/LetoBukarica)
- Removed carriage returns in the validation phase. [#144](https://github.com/IKOR-GmbH/sip-framework/pull/144) by [LetoBukarica](https://github.com/LetoBukarica)
- Remove in memory tracing functionality and api. Add possibility to turn off console logs through configuration while tracing is active. [#139](https://github.com/IKOR-GmbH/sip-framework/pull/139) by [Nemikor](https://github.com/Nemikor)


## 2.3.0 - 2022-09-08

### ⭐ Features
- Implementation of File component support in SIP Test Kit. [#129](https://github.com/IKOR-GmbH/sip-framework/pull/129) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Implementation for FTP, FTPS and SFTP component support in SIP Test Kit. [#135](https://github.com/IKOR-GmbH/sip-framework/pull/135) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Mark log messages with [SIP TEST] while running sip tests. [#124](https://github.com/IKOR-GmbH/sip-framework/pull/124) by [Nemikor](https://github.com/Nemikor)

### 🐞 Bugfixes
- Add missing context path to rest endpoints from Camel DSL in generated openapi. [#136](https://github.com/IKOR-GmbH/sip-framework/pull/136) by [Nemikor](https://github.com/Nemikor)
- Add missing unit test for TranslateMessageLayout [#137](https://github.com/IKOR-GmbH/sip-framework/pull/137) by [Nemikor](https://github.com/Nemikor)
- Expose type of original processor through ProcessorProxy [#140](https://github.com/IKOR-GmbH/sip-framework/pull/140) by [Nemikor](https://github.com/Nemikor)
- Fixing SIP testing bug - mock reports not available during SIP test verification. [#141](https://github.com/IKOR-GmbH/sip-framework/pull/141) by [VladiIkor](https://github.com/VladiIkor)
- Fix translation unit test which was causing other test to not work correctly. [#143](https://github.com/IKOR-GmbH/sip-framework/pull/143) by [Nemikor](https://github.com/Nemikor)
- Fix for camel ftp dependency and loading bean in sip-cloud. [#142](https://github.com/IKOR-GmbH/sip-framework/pull/142) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Prevent NPE when actual response is null, allow validation for empty body and add validation details for mocks. [#132](https://github.com/IKOR-GmbH/sip-framework/pull/132) by [Nemikor](https://github.com/Nemikor)
- Move tracing id to CustomTracer and make it into a list of exchange ids from which a request consisted of. [#131](https://github.com/IKOR-GmbH/sip-framework/pull/131) by [Nemikor](https://github.com/Nemikor)

### 📔 Documentation
- Add Test Kit documentation about components: REST, SOAP, File, FTP, FTPS and SFTP [#138](https://github.com/IKOR-GmbH/sip-framework/pull/138) by [nikolag-ikor](https://github.com/nikolag-ikor)


## 2.2.0 - 2022-08-03

### ⭐ Features
- Implementation of CXF (SOAP) support in SIP Test Kit. [#118](https://github.com/IKOR-GmbH/sip-framework/pull/118) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Added versions to @SIPFeature annotation and additional conditions on auto configuration classes for actuator extensions. [#119](https://github.com/IKOR-GmbH/sip-framework/pull/119) by [Nemikor](https://github.com/Nemikor)
- Introducing sip-maven-plugin with cross-dependencies-check goal. It will crash the build if classes are cross referenced between connectors packages. [#115](https://github.com/IKOR-GmbH/sip-framework/pull/115) by [vladiIkor](https://github.com/vladiIkor)
- All SIP features are now separated and independent. Actuator features can also be turned on and off separately. [#114](https://github.com/IKOR-GmbH/sip-framework/pull/114) by [Nemikor](https://github.com/Nemikor)
- Add @SIPFeature annotation which marks and names all features that are available in the framework. [#117](https://github.com/IKOR-GmbH/sip-framework/pull/117) by [Nemikor](https://github.com/Nemikor)
- Upgrade version of Camel, Spring Boot and others to latest stable [#116](https://github.com/IKOR-GmbH/sip-framework/pull/116) by [Nemikor](https://github.com/Nemikor)

### 🐞 Bugfixes
- Fixed security issue in TrafficTracerController, caused by possible unsafe user input. [#127](https://github.com/IKOR-GmbH/sip-framework/pull/127) by [Nemikor](https://github.com/Nemikor)
- Remove the enum from @SIPFeature used to define name of feature and replace it with string constants. [#122](https://github.com/IKOR-GmbH/sip-framework/pull/122) by [Nemikor](https://github.com/Nemikor)
- Updated nexus-staging-maven-plugin and removed hardcoded XStream dependency management.. [#130](https://github.com/IKOR-GmbH/sip-framework/pull/130) by [LetoBukarica](https://github.com/LetoBukarica)
- Upgraded SpringBoot from 2.6.7 to 2.6.9. [#120](https://github.com/IKOR-GmbH/sip-framework/pull/120) by [LetoBukarica](https://github.com/LetoBukarica)
- Update test folder structure in SIP Archetype to match the adapter structure. [#123](https://github.com/IKOR-GmbH/sip-framework/pull/123) by [Nemikor](https://github.com/Nemikor)


## 2.1.0 - 2022-06-01

### ⭐ Features
- Replace adapter structure with new simplified one by removing multiple module structure and introducing packages instead. [#106](https://github.com/IKOR-GmbH/sip-framework/pull/106) by [Nemikor](https://github.com/Nemikor)
- Add test-case-definition.yml to archetype with empty template and explanations [#102](https://github.com/IKOR-GmbH/sip-framework/pull/102) by [Nemikor](https://github.com/Nemikor)

### 🐞 Bugfixes
- Fixed the internal server error which occurs when adapter-routes endpoint is called with an non-existing route. [#105](https://github.com/IKOR-GmbH/sip-framework/pull/105) by [LetoBukarica](https://github.com/LetoBukarica)
- Move spring boot test dependency to sip-starter-partner. Update package name regex in archetype. [#113](https://github.com/IKOR-GmbH/sip-framework/pull/113) by [Nemikor](https://github.com/Nemikor)
- Added tests with CamelContext in the Core. [#100](https://github.com/IKOR-GmbH/sip-framework/pull/100) by [LetoBukarica](https://github.com/LetoBukarica)
- Replace log messages with key value pairs so they are able to be translated [#91](https://github.com/IKOR-GmbH/sip-framework/pull/91) by [Nemikor](https://github.com/Nemikor)
- Refactor SIPTranslateMessageService so that it no longer requires SIPStaticSpringContext to register itself [#104](https://github.com/IKOR-GmbH/sip-framework/pull/104) by [Nemikor](https://github.com/Nemikor)
- Refactor code which causes unchecked or deprecated warnings [#110](https://github.com/IKOR-GmbH/sip-framework/pull/110) by [Nemikor](https://github.com/Nemikor)
- Upgraded the OpenAPI versions to match corresponding SpringBoot version [#112](https://github.com/IKOR-GmbH/sip-framework/pull/112) by [Nemikor](https://github.com/Nemikor)

### 📔 Documentation
- Add description for activating Test Kit profile [#96](https://github.com/IKOR-GmbH/sip-framework/pull/96) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Add documentation for upgrading the SIP Framework version from 1.0.0 to 2.0.0. Additional documentation enhancement. [#94](https://github.com/IKOR-GmbH/sip-framework/pull/94) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Fix URL of copy button image [#107](https://github.com/IKOR-GmbH/sip-framework/pull/107) by [Nemikor](https://github.com/Nemikor)
- Maven archetype command changed to one line format. Added copy function [#103](https://github.com/IKOR-GmbH/sip-framework/pull/103) by [dzuci](https://github.com/dzuci)
- refine test kit documentation [#89](https://github.com/IKOR-GmbH/sip-framework/pull/89) by [dzuci](https://github.com/dzuci)
- Update of the table with default configuration  [#97](https://github.com/IKOR-GmbH/sip-framework/pull/97) by [dzuci](https://github.com/dzuci)
- Update of the table with default configuration  [#108](https://github.com/IKOR-GmbH/sip-framework/pull/108) by [dzuci](https://github.com/dzuci)
- Updated SIP Security documentation. [#93](https://github.com/IKOR-GmbH/sip-framework/pull/93) by [LetoBukarica](https://github.com/LetoBukarica)


## 2.0.2 - 2022-03-25

### 🐞 Bugfixes
- Updated test case for health check on suspended route. [#92](https://github.com/IKOR-GmbH/sip-framework/pull/92) by [LetoBukarica](https://github.com/LetoBukarica)
- Resolve context path conflicts in testkit [#98](https://github.com/IKOR-GmbH/sip-framework/pull/98) by [Nemikor](https://github.com/Nemikor)
- Changed GitHub Actions to ignore missing docs-snapshot folder [#99](https://github.com/IKOR-GmbH/sip-framework/pull/99) by [LetoBukarica](https://github.com/LetoBukarica)
- camel.servlet.mapping.context-path configured on core level [#95](https://github.com/IKOR-GmbH/sip-framework/pull/95) by [vladiIkor](https://github.com/vladiIkor)
- Remove maven-enforcer-plugin.version duplicate from framework pom properties [#90](https://github.com/IKOR-GmbH/sip-framework/pull/90) by [vladiIkor](https://github.com/vladiIkor)

### 📔 Documentation
- Test Kit documentation added to GitHub pages [#86](https://github.com/IKOR-GmbH/sip-framework/pull/86) by [vladiIkor](https://github.com/vladiIkor)
- Added documentation guide about unit testing with sipmc. [#87](https://github.com/IKOR-GmbH/sip-framework/pull/87) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Removing dynamic proxy from documentation as it is part of internal architecture, and still not intended for public usage [#85](https://github.com/IKOR-GmbH/sip-framework/pull/85) by [vladiIkor](https://github.com/vladiIkor)
- Tracing documentation updated [#88](https://github.com/IKOR-GmbH/sip-framework/pull/88) by [dzuci](https://github.com/dzuci)


## 2.0.0 - 2022-03-14

### 🚀 Major Changes
- Java version updated from 8 to 11.
Camel version updated from 3.8.0 to 3.14.0.
Spring Boot version updated from 2.3.9.RELEASE to 2.6.1.
Camel's configuration properties were changed. Full list can be found in [official documentation](https://github.com/apache/camel/blob/main/docs/user-manual/modules/ROOT/pages/camel-3x-upgrade-guide-3_10.adoc).
When upgrading a SIP Adapter with framework version 1.0.0 the following must be changed as it had a default value inside SIP Archetype from `camel.component.servlet.mapping.context-path` to `camel.servlet.mapping.context-path`. [#55](https://github.com/IKOR-GmbH/sip-framework/pull/55) by [nikolag-ikor](https://github.com/nikolag-ikor)

### ⭐ Features
- Implementation for actuator info endpoint to expose the basic adapter data and markdown files [#51](https://github.com/IKOR-GmbH/sip-framework/pull/51) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Added changelog.md and adapter-description.md [#41](https://github.com/IKOR-GmbH/sip-framework/pull/41) by [Dzuci](https://github.com/Dzuci)
- Implementation of packaging markdown files in jar file. [#57](https://github.com/IKOR-GmbH/sip-framework/pull/57) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Add application.yaml with spring profile to test resources in archetype [#84](https://github.com/IKOR-GmbH/sip-framework/pull/84) by [Nemikor](https://github.com/Nemikor)
- SIP Test Kit module is added supporting rest-rest adapter testing [#73](https://github.com/IKOR-GmbH/sip-framework/pull/73) by [vladiIkor](https://github.com/vladiIkor)
- Adding SIP Security to Integration starter and its configuration template to the SIP Archetype application.yaml [#70](https://github.com/IKOR-GmbH/sip-framework/pull/70) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Refactoring of core proxy unit tests. [#67](https://github.com/IKOR-GmbH/sip-framework/pull/67) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Added trace-type configuration parameter to handle tracing options [#56](https://github.com/IKOR-GmbH/sip-framework/pull/56) by [Nemikor](https://github.com/Nemikor)
- Adding sip.security.ssl.enabled switch with false default value [#60](https://github.com/IKOR-GmbH/sip-framework/pull/60) by [vladiIkor](https://github.com/vladiIkor)
- Changing http response status to 200 by default if adapter health results with DOWN [#80](https://github.com/IKOR-GmbH/sip-framework/pull/80) by [vladiIkor](https://github.com/vladiIkor)
- Removed health calculation from actuator health endpoint. Calculation of endpoints health is moved to health check scheduler instead. [#37](https://github.com/IKOR-GmbH/sip-framework/pull/37) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Added archetype parameter which provides a choice for optional including/excluding of Lombok dependency. [#17](https://github.com/IKOR-GmbH/sip-framework/pull/17) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Added default mock function (NOOP) to outgoing processors [#58](https://github.com/IKOR-GmbH/sip-framework/pull/58) by [LetoBukarica](https://github.com/LetoBukarica)
- Added ProcessorProxy as parameter in ProxyExtension methods [#64](https://github.com/IKOR-GmbH/sip-framework/pull/64) by [Nemikor](https://github.com/Nemikor)
- Refactored actuator unit tests [#36](https://github.com/IKOR-GmbH/sip-framework/pull/36) by [Nemikor](https://github.com/Nemikor)
- Refactored security unit tests [#43](https://github.com/IKOR-GmbH/sip-framework/pull/43) by [Nemikor](https://github.com/Nemikor)
- Refactored sipmc unit tests [#42](https://github.com/IKOR-GmbH/sip-framework/pull/42) by [Nemikor](https://github.com/Nemikor)
- Refactored trace unit tests [#35](https://github.com/IKOR-GmbH/sip-framework/pull/35) by [Nemikor](https://github.com/Nemikor)
- Refactored translate unit tests [#38](https://github.com/IKOR-GmbH/sip-framework/pull/38) by [Nemikor](https://github.com/Nemikor)
- Refactored util unit tests [#39](https://github.com/IKOR-GmbH/sip-framework/pull/39) by [Nemikor](https://github.com/Nemikor)
- Removed sip.security.authentication.enabled flag, now only authentication provider list is necessary. [#30](https://github.com/IKOR-GmbH/sip-framework/pull/30) by [Nemikor](https://github.com/Nemikor)
- Implementation for enhanced user experience while creating proxy mocks. [#66](https://github.com/IKOR-GmbH/sip-framework/pull/66) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Added descriptions for operations and parameters in custom actuator endpoints [#32](https://github.com/IKOR-GmbH/sip-framework/pull/32) by [Nemikor](https://github.com/Nemikor)
- Updated swagger documentation, added descriptions for possible operations on routes. [#3](https://github.com/IKOR-GmbH/sip-framework/pull/3) by [Nemikor](https://github.com/Nemikor)
- Updated Apache Camel version to 3.13.0 [#24](https://github.com/IKOR-GmbH/sip-framework/pull/24) by [Nemikor](https://github.com/Nemikor)

### 🐞 Bugfixes
- Changing access level of AdapterRouteDetails class to public [#59](https://github.com/IKOR-GmbH/sip-framework/pull/59) by [vladiIkor](https://github.com/vladiIkor)
- GroupId is added to archetype generated application/pom.xml [#79](https://github.com/IKOR-GmbH/sip-framework/pull/79) by [vladiIkor](https://github.com/vladiIkor)
- Adding sip.security.ssl.server.client-auth config key to sip-security [#19](https://github.com/IKOR-GmbH/sip-framework/pull/19) by [vladiIkor](https://github.com/vladiIkor)
- Excluding Spring security auto-config classes to prevent default authentication [#20](https://github.com/IKOR-GmbH/sip-framework/pull/20) by [vladiIkor](https://github.com/vladiIkor)
- Enabled adapter-routes to be present in actuator endpoint by default [#25](https://github.com/IKOR-GmbH/sip-framework/pull/25) by [Nemikor](https://github.com/Nemikor)
- Enabled SpringBoot AutoConfiguration for the Core package [#62](https://github.com/IKOR-GmbH/sip-framework/pull/62) by [MartinBuchheimIkor](https://github.com/MartinBuchheimIkor)
- Fixed unit tests that fails while running 'mvn install' [#44](https://github.com/IKOR-GmbH/sip-framework/pull/44) by [Nemicore](https://github.com/Nemicore)
- Adding relative path of adapter's parent in root pom [#75](https://github.com/IKOR-GmbH/sip-framework/pull/75) by [vladiIkor](https://github.com/vladiIkor)
- Updated HealthCheckEnabledCondition to match when HealthEndpoint is enabled [#54](https://github.com/IKOR-GmbH/sip-framework/pull/54) by [Nemikor](https://github.com/Nemikor)
- Decorating Camel's RouteController in order to add missing logs when route is started and resumed. [#71](https://github.com/IKOR-GmbH/sip-framework/pull/71) by [vladiIkor](https://github.com/vladiIkor)
- Refactored scheduler to be active if property is set to true and HealthEndpoint is enabled [#45](https://github.com/IKOR-GmbH/sip-framework/pull/45) by [Nemicore](https://github.com/Nemicore)
- Enabled SIP SecurityConfig always - removed the @ConditionalOnSIPSecurityAuthenticationEnabled [#65](https://github.com/IKOR-GmbH/sip-framework/pull/65) by [LetoBukarica](https://github.com/LetoBukarica)
- /actuator/health IllegalArgumentException fix when route is suspended [#83](https://github.com/IKOR-GmbH/sip-framework/pull/83) by [LetoBukarica](https://github.com/LetoBukarica)
- maven-enforcer-plugin configuration updated according to java version update [#82](https://github.com/IKOR-GmbH/sip-framework/pull/82) by [vladiIkor](https://github.com/vladiIkor)

### 📔 Documentation
- Added docs-snapshot directory for documenting unreleased features [#52](https://github.com/IKOR-GmbH/sip-framework/pull/52) by [Nemikor](https://github.com/Nemikor)
- Added mvn command for creating an archetype on Windows machines [#23](https://github.com/IKOR-GmbH/sip-framework/pull/23) by [dzuci](https://github.com/dzuci)
- Add documentation for enabling /actuator/tracing endpoint. [#63](https://github.com/IKOR-GmbH/sip-framework/pull/63) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Removed adapter-name from endpoint naming convention [#33](https://github.com/IKOR-GmbH/sip-framework/pull/33) by [Nemikor](https://github.com/Nemikor)
- Fixed missing bad html escaping within main readme file. [#22](https://github.com/IKOR-GmbH/sip-framework/pull/22) by [nikolag-ikor](https://github.com/nikolag-ikor)
- Removed archetype duplicates in installation.md [#40](https://github.com/IKOR-GmbH/sip-framework/pull/40) by [dzuci](https://github.com/dzuci)


## 1.0.0 - 2021-11-18

### 🚀 Major Changes
- This is the initial version of the SIP framework. For more information on how to use it and details about features, please refer to the README and to our documentation on https://ikor-gmbh.github.io/sip-framework/. [#15](https://github.com/IKOR-GmbH/sip-framework/pull/15) by [dannikore](https://github.com/dannikore)


