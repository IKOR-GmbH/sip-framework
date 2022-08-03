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

