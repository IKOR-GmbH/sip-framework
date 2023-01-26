package de.ikor.sip.foundation.testkit.workflow.givenphase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.testkit.workflow.TestCase;
import de.ikor.sip.foundation.testkit.workflow.TestExecutionStatus;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportActivityProxyExtensionTest {
  private final String TEST_NAME = "test name";

  TestCase testCase;
  ReportActivityProxyExtension subject;
  Exchange original;
  Exchange current;
  Message message;
  ProcessorProxy proxy;
  List<TestCase> testCases;

  @BeforeEach
  void setUp() {
    testCase = mock(TestCase.class);
    subject = new ReportActivityProxyExtension();
    original = mock(Exchange.class, RETURNS_DEEP_STUBS);
    current = mock(Exchange.class, RETURNS_DEEP_STUBS);
    message = mock(Message.class);
    proxy = mock(ProcessorProxy.class);
    when(testCase.getTestName()).thenReturn(TEST_NAME);
    testCases = new ArrayList<>(Collections.singletonList(testCase));
    subject.setTestCases(testCases);
  }

  @Test
  void When_run_Expect_GeneratedReport() {
    // arrange
    TestExecutionStatus testExecutionStatus = new TestExecutionStatus();
    when(original.getMessage()).thenReturn(message);
    when(message.getHeader(RouteInvoker.TEST_NAME_HEADER, String.class)).thenReturn(TEST_NAME);
    String ORIGINAL_BODY = "originalbody";
    when(message.getBody()).thenReturn(ORIGINAL_BODY);
    String PROXY_ID = "proxy id";
    when(proxy.getId()).thenReturn(PROXY_ID);
    when(testCase.getTestExecutionStatus()).thenReturn(testExecutionStatus);

    // act
    subject.run(proxy, original, current);

    // assert
    assertThat(testExecutionStatus.getMockReports().get(PROXY_ID)).isNotNull();
  }

  @Test
  void When_run_With_NoTestCases_Then_exception() {
    // arrange
    subject.setTestCases(new ArrayList<>());
    when(original.getMessage().getHeader(RouteInvoker.TEST_NAME_HEADER, String.class))
        .thenReturn(TEST_NAME);

    // act + assert
    assertThatThrownBy(() -> subject.run(proxy, original, current))
        .isInstanceOf(SIPFrameworkException.class)
        .hasMessage("Test case with name " + TEST_NAME + " could not be found!");
  }

  @Test
  void When_isApplicable_With_AllConditionsMatch_Then_true() {
    when(proxy.isEndpointProcessor()).thenReturn(true);
    when(original.getMessage().getHeader(RouteInvoker.TEST_NAME_HEADER, String.class))
        .thenReturn(TEST_NAME);
    when(original.getIn().getHeader(ProcessorProxy.TEST_MODE_HEADER, String.class))
        .thenReturn("true");
    assertTrue(subject.isApplicable(proxy, original, current));
  }

  @Test
  void When_isApplicable_With_NonEndpointProcessor_Then_false() {
    when(proxy.isEndpointProcessor()).thenReturn(false);
    when(original.getMessage().getHeader(RouteInvoker.TEST_NAME_HEADER, String.class))
        .thenReturn(TEST_NAME);
    when(original.getIn().getHeader(ProcessorProxy.TEST_MODE_HEADER, String.class))
        .thenReturn("true");
    assertFalse(subject.isApplicable(proxy, original, current));
  }

  @Test
  void When_isApplicable_With_NoTestCases_Then_false() {
    subject.setTestCases(null);
    when(original.getMessage().getHeader(RouteInvoker.TEST_NAME_HEADER, String.class))
        .thenReturn(TEST_NAME);
    when(proxy.isEndpointProcessor()).thenReturn(true);
    when(original.getIn().getHeader(ProcessorProxy.TEST_MODE_HEADER, String.class))
        .thenReturn("true");
    assertFalse(subject.isApplicable(proxy, original, current));
  }

  @Test
  void When_isApplicable_With_TestModeFalse_Then_false() {
    when(proxy.isEndpointProcessor()).thenReturn(true);
    when(original.getMessage().getHeader(RouteInvoker.TEST_NAME_HEADER, String.class))
        .thenReturn(TEST_NAME);
    when(original.getIn().getHeader(ProcessorProxy.TEST_MODE_HEADER, String.class))
        .thenReturn("false");
    assertFalse(subject.isApplicable(proxy, original, current));
  }

  @Test
  void When_isApplicable_With_NoTestName_Then_false() {
    when(proxy.isEndpointProcessor()).thenReturn(true);
    when(original.getIn().getHeader(ProcessorProxy.TEST_MODE_HEADER, String.class))
        .thenReturn("true");
    assertFalse(subject.isApplicable(proxy, original, current));
  }
}
