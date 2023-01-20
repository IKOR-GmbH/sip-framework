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

