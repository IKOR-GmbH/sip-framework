package de.ikor.sip.foundation.testkit.config;

import static de.ikor.sip.foundation.testkit.util.TestKitHelper.parseExchangeProperties;
import static java.util.stream.Collectors.toList;

import de.ikor.sip.foundation.testkit.configurationproperties.TestCaseDefinition;
import de.ikor.sip.foundation.testkit.configurationproperties.models.EndpointProperties;
import de.ikor.sip.foundation.testkit.workflow.TestExecutionStatus;
import de.ikor.sip.foundation.testkit.workflow.reporting.model.MockReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestExecutionStatusFactory {
  private final CamelContext camelContext;

  TestExecutionStatus generateTestReport(TestCaseDefinition testCaseDefinition) {
    String testName = testCaseDefinition.getTitle();
    return new TestExecutionStatus()
        .setTestName(testName)
        .setExpectedAdapterResponse(getExpectedAdapterResponse(testCaseDefinition))
        .setMockReports(getMockReports(testCaseDefinition));
  }

  private Map<String, MockReport> getMockReports(TestCaseDefinition testCaseDefinition) {
    List<EndpointProperties> expectedEndpointResponses =
        expectedEndpointResponses(testCaseDefinition);
    Map<String, MockReport> reportMap = new HashMap<>();
    expectedEndpointResponses.forEach(
        endpointProperty ->
            reportMap.put(
                endpointProperty.getEndpointId(),
                new MockReport()
                    .setExpected(parseExchangeProperties(endpointProperty, camelContext))));
    return reportMap;
  }

  private Exchange getExpectedAdapterResponse(TestCaseDefinition testCaseDefinition) {
    String startingEndpoint = testCaseDefinition.getWhenExecute().getEndpointId();
    EndpointProperties endpointProperties =
        IterableUtils.find(
            testCaseDefinition.getThenExpect(),
            endpoint -> endpoint.getEndpointId().equals(startingEndpoint));
    return parseExchangeProperties(endpointProperties, camelContext);
  }

  private List<EndpointProperties> expectedEndpointResponses(
      TestCaseDefinition testCaseDefinition) {
    String expectedAdapterResponseId = testCaseDefinition.getWhenExecute().getEndpointId();
    return testCaseDefinition.getThenExpect().isEmpty()
        ? new ArrayList<>()
        : testCaseDefinition.getThenExpect().stream()
            .filter(
                endpointProperties ->
                    !endpointProperties.getEndpointId().equals(expectedAdapterResponseId))
            .collect(toList());
  }
}
