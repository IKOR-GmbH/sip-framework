-----------------------------
| SIP Test Execution Report |
-----------------------------

  <#if report.successfulExecution>
  Test "${report.testName}" executed successfully.
  </#if>
  <#if !report.successfulExecution>
  Test "${report.testName}" executed unsuccessfully.
  </#if>
  <#if report.adapterReport.validationResults?has_content>
    Validation details:
      <#list report.adapterReport.validationResults as validationResult>
      ${validationResult.message}
      </#list>
  </#if>
  <#if report.adapterReport.responseMessage?has_content>
    Actual response:
    <#if report.adapterReport.responseMessage.body??>
      Body: ${report.adapterReport.responseMessage.body}
    </#if>
    <#if report.adapterReport.validatedHeaders?has_content>
      Validated headers:
      <#list report.adapterReport.validatedHeaders?keys as key>
      - ${key}: ${report.adapterReport.validatedHeaders[key]}
      </#list>
    </#if>
    <#if report.adapterReport.expectedResponse?has_content>
    Expected response:
    <#if report.adapterReport.expectedResponse.message.body??>
      Body: ${report.adapterReport.expectedResponse.message.body}
    </#if>
    <#if report.adapterReport.expectedResponse.message.headers?has_content>
      Headers:
      <#list report.adapterReport.expectedResponse.message.headers?keys as key>
      - ${key}: ${report.adapterReport.expectedResponse.message.headers[key]}
      </#list>
    </#if>
    </#if>
  </#if>
  <#if report.workflowExceptionMessage?? && report.workflowExceptionMessage?trim?has_content>
    ${report.workflowExceptionMessage}
  </#if>
  <#if report.adapterExceptionMessage?? && report.adapterExceptionMessage?trim?has_content>
    Adapter threw exception: ${report.adapterExceptionMessage}
  </#if>
  <#if report.mockReports??>
    Endpoints:
      <#list report.mockReports?keys as key>
      Endpoint "${key}" was mocked
      Validation ${report.mockReports[key].validated}
      <#if report.mockReports[key].actualMessage?has_content>
      Received:
       Body: <#if report.mockReports[key].actualMessage.body??>${report.mockReports[key].actualMessage.body}</#if>
      </#if>
       <#if report.mockReports[key].validatedHeaders?has_content>
       Headers:
       <#list report.mockReports[key].validatedHeaders?keys as mkey>
        - ${mkey}: ${report.mockReports[key].validatedHeaders[mkey]}
       </#list>
       </#if>
      <#if report.mockReports[key].expectedMessage?has_content>
      Expected:
       Body: <#if report.mockReports[key].expectedMessage.body??>${report.mockReports[key].expectedMessage.body}</#if>
      <#if report.mockReports[key].expectedMessage.headers?has_content>
       Headers:
        <#list report.mockReports[key].expectedMessage.headers?keys as mkey>
        - ${mkey}: ${report.mockReports[key].expectedMessage.headers[mkey]}
        </#list>
      </#if>
      </#if>
      </#list>
  </#if>

-----------------------------