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

