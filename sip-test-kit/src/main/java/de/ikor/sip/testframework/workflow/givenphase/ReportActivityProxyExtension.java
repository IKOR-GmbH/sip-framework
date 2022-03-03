package de.ikor.sip.testframework.workflow.givenphase;

import static javax.xml.bind.DatatypeConverter.parseBoolean;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.core.proxies.extension.ProxyExtension;
import de.ikor.sip.testframework.util.SIPExchangeHelper;
import de.ikor.sip.testframework.workflow.TestCase;
import de.ikor.sip.testframework.workflow.TestExecutionStatus;
import de.ikor.sip.testframework.workflow.whenphase.executor.Executor;
import java.util.List;
import java.util.Optional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.context.annotation.Configuration;

/** Proxy extensions for tracking activity of mocked ProcessorProxy */
@Slf4j
@Configuration
public class ReportActivityProxyExtension implements ProxyExtension {
  @Setter private List<TestCase> testCases;

  @Override
  public void run(ProcessorProxy proxy, Exchange original, Exchange current) {
    findTestReport(original)
        .getMockReport(proxy.getId())
        .setActual(original)
        .setActualMessage(SIPExchangeHelper.mapToMessageProperties(original));
  }

  private String getTestName(Exchange original) {
    return original.getMessage().getHeader(Executor.TEST_NAME_HEADER, String.class);
  }

  @Override
  public boolean isApplicable(ProcessorProxy proxy, Exchange original, Exchange current) {
    return isTest(original) && proxy.isEndpointProcessor() && hasTestCase(original);
  }

  private boolean isTest(Exchange exchange) {
    return parseBoolean(exchange.getIn().getHeader(ProcessorProxy.TEST_MODE_HEADER, String.class));
  }

  private TestExecutionStatus findTestReport(Exchange exchange) {
    String testName = getTestName(exchange);
    Optional<TestCase> tc =
        testCases.stream().filter(testCase -> testCase.getTestName().equals(testName)).findFirst();
    if (tc.isEmpty()) {
      throw new IllegalArgumentException(
          "Test case with name " + testName + " could not be found!");
    }
    return tc.get().getTestExecutionStatus();
  }

  private boolean hasTestCase(Exchange exchange) {
    return testCases != null
        && testCases.stream()
            .anyMatch(testCase -> testCase.getTestName().equals(getTestName(exchange)));
  }
}
