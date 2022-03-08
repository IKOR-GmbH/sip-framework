package de.ikor.sip.testframework.util;

import de.ikor.sip.testframework.SIPBatchTest;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class SIPBatchTestArgumentSource implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
      throws Exception {
    SIPBatchTest executedBatchTest = (SIPBatchTest) extensionContext.getRequiredTestInstance();
    return executedBatchTest.getTestCases().stream()
        .map(testCase -> Arguments.of(testCase.getTestName(), testCase));
  }
}
