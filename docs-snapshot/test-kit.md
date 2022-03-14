# IKOR SIP Test Kit

[TOC]

# Description

SIP Test Kit is a testing system designed to work with SIP integration adapters. 
It's main purpose is to test how an adapter works internally, by mocking all external communication. 
It executes a test workflow that is defined in a specific file format, 
providing a possibility to create tests without code change. 
This file format is referred to as <i><b>TestCaseDefinition</i></b>. 
The flow itself and thus the TestCaseDefinition file is split into three phases (sections)

<li> when-execute - The test is executed by triggering one off adapter's endpoints</li> 
<li> with-mocks - All external calls are replaced with predefined mocks</li>
<li> then-expect - The test outcome is compared with a predefined expected outcome</li>

# Test Kit provides...

## Response validation

Major feature of Test Kit is validation of the response from test execution.
By adding a validation definition inside the then-expect section
and setting that validation for the endpoint we defined in when-execute section,
the response of the execution will be validated and a report will be displayed.

## External endpoint mocking

This feature is active whenever a test is executed via test kit.
It will provide default behavior (forwarding the request without processing it) and mock all external endpoints.
Adding specific behavior for each endpoint can be done in test case definition in 'with-mocks' section.

## Endpoint reports

Test Kit will provide reports for all mocked endpoints, both with default (set by Test Kit) and user defined behavior.
For each test report in the 'Endpoints' section there will be an overview of request that each received.

## Endpoint validation

It is possible to also validate body and headers of each mocked endpoint.
By defining desired validation inside test case definition file,
Test Kit will validate endpoints and display reports for each.

## Reports

As mentioned each test case will be executed as its own unit test, so for each a test report will be generated
and printed in console.
First part of report is for the response. It will display the validated body and headers, as well as expected ones.
The following endpoints part is for mocked endpoint reports, with similar data as in the first part.

# How to use

Test kit is enabled by default when the adapter is generated from archetype.

A test class needs to be created inside test package of the adapter, which extends SIPBatchTest. 
Running this class would execute the test, but also it will be executed during build time.

```java
public class TFWTest extends SIPBatchTest { }
```

The next step is to provide the TestCaseDefinition file in yaml format in the `test/resources` package
(detailed description in next section):
``` yaml
test-case-definitions:
- title: "Title of individual test"
  when-execute:
    endpoint: "id of endpoint under test"
    with:
      body: "Content that will be send as request body to the adapter endpoint (plain text, JSON String)"
      headers:
        header-key: "Value of the header"
        another-header-key: "Another value"
  with-mocks:
  - endpoint: "id of endpoint that should be mocked"
    returning:
      body: "Response message that real endpoint is expected to return"
  then-expect:
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

You also need to provide the location of TestCaseDefinition file to the Test Kit by setting the
following property inside adapter configuration:
`sip.testkit.test-cases-path: myTests.yml`
The default value is test-case-definition.yml, so you can place your test case description under that file, 
and skip additional setting.

Each test case will execute as a separate unit test with its own report displayed.

![alt text](./img/ConsoleReport.png)

To disable SIP Test Kit, the following configuration is needed:
```yaml
sip:
  testkit:
    enabled: false
```

# Defining a Test Case

The TestCaseDefinition file starts with `test-case-definitions` property, which consists of a list of test cases

## when-execute

In this section a request that should be sent to the adapter is defined.

"endpoint" refers to ID (routeId in camel routes) of the endpoint to which we wish to send a test request.
In "with" part we define content of the request we wish to send, meaning body and headers are added here.
The body can also be defined as plain text or JSON string, which represents a POJO.

```yaml
    when-execute:
      endpoint: "rest-endpoint"
      with:
        body: "body of request"
```

## with-mocks

This section contains a list of endpoints for which we wish to have specific mocked behavior.
"endpoint" is the endpoint ID, (processor ID in camel route) of the mocked endpoint.
"returning" should have the body, that we expect as the response from real external call.

```yaml
    with-mocks:
      - endpoint: "external-service"
        returning:
          body: "response message from service"
```

## then-expect

Validation of test response is defined by setting the "endpoint" parameter to the ID of endpoint under test
and defining the expected body or headers.

Also, validation of requests, which mocks received, 
is defined by setting the "endpoint" parameter to the ID of mocked endpoint
and defining the expected body or headers.

Body and header validation is possible by either defining regex (Java) expression or matching exact String content.

```yaml
    then-expect:
      - endpoint: "rest-endpoint" # matches endpoint under test
        having:
          body: "response .* from service"
          headers:
            CamelHttpResponseCode: "200"
      - endpoint: "external-service" # matches endpoint with mocked behavior
        having:
          body: "body of request"
          headers:
            test-mode: "true"
```

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
                // Mocked endpoint
                .to("http:localhost:8081/hello?bridgeEndpoint=true") 
                .id("external-service");
    }
}
```

**Sample test case definition**
``` yaml
  test-case-definitions:
  - title: "Test case 1"
    when-execute:
      endpoint: "rest-endpoint" # id of endpoint under test (routeId)
      with:
        body: "body of request"
    with-mocks:
    - endpoint: "external-service" # id of endpoint that is mocked (processorId)
      returning:
        body: "response message from service"
    then-expect:
    - endpoint: "rest-endpoint" # matches endpoint under test
      having:
        body: "response .* from service"
        headers:
          CamelHttpResponseCode: "200"
    - endpoint: "external-service" # matches endpoint with mocked behavior
      having:
        body: "body of request"
        headers:
          test-mode: "true"
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
      Received:
       Body: body of request
       Headers:
        - test-mode: true
      Expected:
       Body: body of request
       Headers:
        - test-mode: true

-----------------------------
```

