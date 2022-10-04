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

