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

