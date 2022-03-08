package de.ikor.sip.testkit.config;

import static java.util.stream.Collectors.toList;

import de.ikor.sip.testkit.configurationproperties.TestCaseDefinition;
import de.ikor.sip.testkit.configurationproperties.models.EndpointProperties;
import de.ikor.sip.testkit.workflow.TestExecutionStatus;
import de.ikor.sip.testkit.workflow.givenphase.Mock;
import de.ikor.sip.testkit.workflow.reporting.model.MockReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
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
                endpointProperty.getEndpoint(),
                new MockReport().setExpected(parseExchangeProperties(endpointProperty))));
    return reportMap;
  }

  private Exchange getExpectedAdapterResponse(TestCaseDefinition testCaseDefinition) {
    String startingEndpoint = testCaseDefinition.getWhenExecute().getEndpoint();
    EndpointProperties endpointProperties =
        IterableUtils.find(
            testCaseDefinition.getThenExpect(),
            endpoint -> endpoint.getEndpoint().equals(startingEndpoint));
    return parseExchangeProperties(endpointProperties);
  }

  private List<EndpointProperties> expectedEndpointResponses(
      TestCaseDefinition testCaseDefinition) {
    String expectedAdapterResponseId = testCaseDefinition.getWhenExecute().getEndpoint();
    return testCaseDefinition.getThenExpect().isEmpty()
        ? new ArrayList<>()
        : testCaseDefinition.getThenExpect().stream()
            .filter(
                endpointProperties ->
                    !endpointProperties.getEndpoint().equals(expectedAdapterResponseId))
            .collect(toList());
  }

  private Exchange parseExchangeProperties(EndpointProperties properties) {
    if (properties == null) {
      return null;
    }
    ExchangeBuilder exchangeBuilder =
        ExchangeBuilder.anExchange(camelContext).withBody(properties.getMessage().getBody());
    properties.getMessage().getHeaders().forEach(exchangeBuilder::withHeader);
    exchangeBuilder.withProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, properties.getEndpoint());
    return exchangeBuilder.build();
  }
}
