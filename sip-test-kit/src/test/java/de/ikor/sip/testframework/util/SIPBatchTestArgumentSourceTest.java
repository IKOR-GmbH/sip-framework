package de.ikor.sip.testframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.testframework.SIPBatchTest;
import de.ikor.sip.testframework.workflow.TestCase;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

class SIPBatchTestArgumentSourceTest {

  @Test
  void When_provideArguments_Expect_NamedTestCaseArgument() throws Exception {
    // arrange
    ExtensionContext extensionContext = mock(ExtensionContext.class);
    SIPBatchTest sipBatchTest = mock(SIPBatchTest.class);
    TestCase testCase = mock(TestCase.class);
    SIPBatchTestArgumentSource subject = new SIPBatchTestArgumentSource();

    when(extensionContext.getRequiredTestInstance()).thenReturn(sipBatchTest);
    String testName = "name";
    when(testCase.getTestName()).thenReturn(testName);
    when(sipBatchTest.getTestCases()).thenReturn(Stream.of(testCase).collect(Collectors.toList()));

    // act
    List<Arguments> testCases =
        subject.provideArguments(extensionContext).collect(Collectors.toList());
    Named namedArg = (Named) testCases.get(0).get()[0];

    // assert
    assertThat(namedArg.getName()).isEqualTo(testName);
    assertThat(namedArg.getPayload()).isEqualTo(testCase);
  }
}
