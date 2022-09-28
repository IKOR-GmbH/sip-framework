# IKOR SIP Test Kit

[TOC]

# Description

SIP Test Kit is a testing system designed to work with SIP integration adapters. 
It's main purpose is to test how an adapter works internally, by mocking all external communication. 
It executes a test workflow that is defined in a specific file format, 
providing a possibility to create tests without code change. 
This file format is referred to as <i><b>TestCaseDefinition</i></b>. 
The flow itself and thus the TestCaseDefinition file is split into three phases (sections)

- when-execute - The test is executed by triggering one of adapter's endpoints
- with-mocks - External calls which are to be replaced with defined mock behaviour
- then-expect - The test outcome that should be compared with a defined expected outcome

# Features

## External endpoint mocking

This feature is active whenever a test is executed via Test Kit.
It will provide default behavior (forwarding the request without processing it) and mock all external endpoints.
Adding specific behavior for each endpoint can be done in test case definition in 'with-mocks' section.

## Validation

Validation is one of key properties to any testing system. SIP Test Kit supports validation for batch testing.
Validation is configured trough than-expect section of test case definition, by setting expected properties of endpoint
we want to validate. It could be the entering endpoint of the adapter, for example we want to validate HTTP response of the
adapter, or it could be any external system (mocked) endpoint, where we can validate the input that mocked endpoint has received;
this way we could validate, for instance, if a properly transformed file reached the outgoing FTP endpoint.  
Validation is performed on two levels, _*body*_ - where the data is validated and _*headers*_ - where metadata is validated. Body 
validation is performed as plain text comparison, binary payload is not yet supported. Headers comparison is comparing 
textual key value maps. Both body and header validation support regex pattern as expected value.
Given that all SIP mocks are internal, meaning that the actual endpoint is replaced with the mock, any URI options defined 
on the mock will not apply and behavior produced by them is not possible to verify. 


## Reports

Each test case will be executed as its own unit test, so for each a test report will be generated
and printed in console.
First part of report is for the response. It will display the validated body and headers, as well as expected ones.
The following endpoints part is for mocked endpoint reports, with similar data as in the first part.

## Endpoint reports

Reports for all mocked endpoints will be provided, both with default (set by Test Kit) and user defined behavior.
For each test report in the 'Endpoints' section there will be an overview of request that each received.

# How to use

Test kit is enabled by default when the adapter is generated from archetype.

A test class needs to be created inside test package of the adapter, which extends SIPBatchTest. 
Running this class would execute the test, but also it will be executed in the testing step during build time.

```java
public class TFWTest extends SIPBatchTest { }
```

Configuring Spring profile is needed. Make sure that the following configuration property inside your test resources is defined:
```yaml
spring:
  profiles:
    active: test
```

The next step is to provide the TestCaseDefinition file in yaml format in the `test/resources` package
(detailed description in next section):
``` yaml
test-case-definitions:
- TITLE: "Title of individual test"
  WHEN-execute:
    endpoint: "id of endpoint under test"
    with:
      body: "Content that will be send as request body to the adapter endpoint (plain text, JSON String)"
      headers:
        header-key: "Value of the header"
        another-header-key: "Another value"
  WITH-mocks:
  - endpoint: "id of endpoint that should be mocked"
    returning:
      body: "Response message that real endpoint is expected to return"
  THEN-expect:
  - endpoint: "id of endpoint under test" # matches endpoint under test defined in when phase
    having:
      body: "Regex expression (java) which will be compered to the reponse of the test"
      headers:
        header-key: "Regex expression (java) which will be compered to the value of this header key"
  - endpoint: "id of endpoint that is mocked" # matches endpoint with defined or default mocked behavior
    having:
      body: "Regex expression (java) which will be compered to the request which arrived on the endpoint"
      headers:
        header-key: "Regex expression (java) which will be compered to the header key value from request which arrived on the endpoint"
```

Location of the TestCaseDefinition file can be provided to the Test Kit by setting the
following property inside adapter configuration:
`sip.testkit.test-cases-path: myTests.yml`
The default value is _test-case-definition.yml_, so you can place your test case description using that filename, 
and avoid additional setting.

Each test case will execute as a separate unit test with its own report displayed.

![alt-text](./img/ConsoleReport.png)

To disable SIP Test Kit, use the following configuration:
```yaml
sip:
  testkit:
    enabled: false
```


Development note:
_To be able to fully utilize the Test Kit, all the endpoints used in the test case need to have a defined ID which will be referenced in the _endpoint_ parameter of the test case._

# Defining a Test Case

The TestCaseDefinition file starts with `test-case-definitions` property, which consists of a list of test cases

## WHEN-execute

In this section a payload that should be sent to the adapter is defined.

"endpoint" refers to ID (routeId in Camel routes) of the endpoint to which we wish to send a test request.
In "with" part we define content of the request we wish to send, meaning body and headers are added here.
The body can also be defined as plain text or JSON string, which represents a POJO.

```yaml
    WHEN-execute:
      endpoint: "rest-endpoint"
      with:
        body: "body of request"
```

## WITH-mocks

This section contains a list of endpoints for which we wish to have specific mocked response.
"endpoint" is the endpoint ID, (processor ID in Camel route) of the mocked endpoint.
"returning" should have the body, that we expect as the response from real external call.

```yaml
    WITH-mocks:
      - endpoint: "external-service"
        returning:
          body: "response message from service"
```

## THEN-expect

Validation of adapter response is defined by setting the "endpoint" parameter to the ID of endpoint under test
and defining the expected body or headers.

Validation of requests which outgoing endpoints received from the adapter is defined by setting the "endpoint" parameter to 
the ID of mocked endpoint and defining the expected body or headers.

Body and header validation is possible by either defining regex (Java) expression or matching exact String content.

```yaml
    THEN-expect:
      - endpoint: "rest-endpoint" # matches endpoint under test
        having:
          body: "response .* from service"
          headers:
            CamelHttpResponseCode: "200"
      - endpoint: "external-service" # matches endpoint with mocked behavior
        having:
          body: "body of request"
          headers:
            Authorization: "Basic .*"
```

# Supported Camel components

Following Camel components support testing with Test Kit:

- REST
- SOAP (by using CXF)
- File
- FTP, FTPS, SFTP
- JMS

Please check the special conditions for these components in following chapters. since there are some special conditions
which must be met.

### REST

Running Test Kit tests with REST component is straightforward. Keep in mind that REST headers could be provided in 
<i>when-execute</i> phase. There are no special considerations and our general [example](#complete-example) is shown 
with REST component. 

### SOAP

Testing SOAP requests is possible if the adapter is using Camel CXF component. When writing body payloads within TestCaseDefinition file,
it is required for all three phases (<i>when-execute</i>, <i>with-mocks</i>, <i>then-expect</i>) to provide soap xml
content as a String in one line and to do String escape. Be sure that you meet these requirements, otherwise tests 
could fail.

### File

File content should be provided as body in <i>when-execute</i> phase. 

Outside of testing, the File component will read the actual file from a specified location. 
In that case Exchange within the route will have File component specific exchange headers (for example `CamelFileName`, `CamelFileLength`, 
`CamelFileLastModified`... ). These headers are listed and explained in Camel File component docs.
If these headers are needed in tests for route processing, they can be provided in <i>when-execute</i> configuration 
part, under <i>headers</i> field. Header keys must be specified exactly the same as they are specified in Camel docs.

When specifying some File headers, Test Kit will automatically set a few other headers which are populated under same conditions and 
calculations as it is done in Camel File component:

1) `CamelFileLength` - calculated and set automatically according to body length.
2) `CamelFileName` - you can provide this header, and we will set `CamelFileNameConsumed` and `CamelFileNameOnly` 
additionally.
3) `CamelFileLastModified` - by providing this header, `CamelMessageTimestamp` will be set additionally.

Any of these automatically set headers can be overridden by setting the header explicitly.
All other File component headers, which are not mentioned here, can be provided by setting them explicitly.

### FTP, FTPS, SFTP

FTP, FTPS and SFTP have same behavior and testing approach in Test Kit. Security differences between them are not
relevant for testing and all three components are equally supported.

FTP component behaves mostly the same as File component. For better understanding, check the File component chapter.

On top of File component headers, FTP components have few more. However, benefits that we provide in setting 
headers automatically are different, and they are not the same as for File headers:

1) `CamelFileLength` - calculated and set automatically according to body length.
2) `CamelFileAbsolute` - calculated automatically based on component `directoryName` endpoint option.
3) `CamelFileParent` - calculated automatically based on component `directoryName` endpoint option.
4) `CamelFileHost` - calculated automatically based on component `host` endpoint option.
5) `CamelRemoteFileInputStream` - calculated automatically if endpoint option `streamDownload` is set.
6) `CamelFileName` - by providing this header, we set automatically headers `CamelFileNameConsumed`,
`CamelFileNameOnly`, `CamelFileRelativePath`, `CamelFileAbsolutePath` and `CamelFilePath`.
7) `CamelFileLastModified` - by providing this header, `CamelMessageTimestamp` will be set additionally.

Same rules as in File chapter for overriding and providing other headers apply here as well.

### JMS

When testing JMS component, there are some stuff which we do not support.


Original jms `Message` and jms `Session` are not provided within the exchange. That means if there is some logic within 
the route which is based on these elements, tests for that kind of route could not be created. Instead of original jms
Message, we provide our custom implementation `SIPJmsTextMessage` which is there to support Test Kit testing purpose.

When providing camel JMS specified headers within test case definition, there are 3 following headers which could not 
be provided with simple String value, hence we skip adding these values and keep default ones (`JMSDestination`, 
`JMSReplyTo`, `JMSCorrelationIDAsBytes`).

If logic of the route lean on jms component type converters or custom type converter option, tests could not be created.
Currently, type converters are not supported and only possible values to provide are simple String or Json. But in case 
of json, type conversion should be done somewhere in the route and outside jms component.

# Complete example

**Sample Route**

```java
public class SampleRestRoute extends RouteBuilder {
    public void configure() throws Exception {
        restConfiguration().component("servlet").port("8080").host("localhost");
        
        // Endpoint under test
        from("rest:POST:/say/hello").routeId("rest-endpoint").to("sipmc:bridge");

        from("sipmc:bridge")
                .routeId("http-route")
                .setHeader("Authorization", constant("Basic am9obkBleGFtcGxlLmNvbTphYmMxMjM="))
                .transform(body().append(" now looks better"))
                // Mocked endpoint
                .to("http:localhost:8081/hello?bridgeEndpoint=true")
                .id("external-service");
    }
}
```

**Sample test case definition**
``` yaml
  - TITLE: "Test case 1"
    WHEN-execute:
      endpoint: "rest-endpoint"
      with:
        body: "body of request"
    WITH-mocks:
      - endpoint: "external-service"
        returning:
          body: "response message from service"
    THEN-expect:
      - endpoint: "rest-endpoint" # matches endpoint under test
        having:
          body: "response .* from service"
          headers:
            CamelHttpResponseCode: "200"
      - endpoint: "external-service"
        having:
          body: "body of request now looks better"
          headers:
            Authorization: "Basic .*"
```

**Sample Console Report**

```
-----------------------------
| SIP Test Execution Report |
-----------------------------

    Test "Test case 1" executed successfully.
    Validation details:
      Body validation successful
      Header validation successful
    Actual response:
      Body: response message from service
      Validated headers:
      - CamelHttpResponseCode: 200
    Expected response:
      Body: response .* from service
      Headers:
      - CamelHttpResponseCode: 200
    Endpoints:
      Endpoint "external-service" was mocked
      Validation successful
      Validation details:
        Body validation successful
        Header validation successful
      Received:
       Body: body of request now looks better
       Headers:
        - Authorization: Basic am9obkBleGFtcGxlLmNvbTphYmMxMjM=
      Expected:
       Body: body of request now looks better
       Headers:
        - Authorization: Basic .*

-----------------------------
```

