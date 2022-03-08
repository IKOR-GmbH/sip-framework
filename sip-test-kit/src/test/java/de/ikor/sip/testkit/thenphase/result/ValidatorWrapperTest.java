package de.ikor.sip.testkit.thenphase.result;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import de.ikor.sip.testkit.workflow.TestExecutionStatus;
import de.ikor.sip.testkit.workflow.thenphase.result.ValidationResult;
import de.ikor.sip.testkit.workflow.thenphase.validator.TestCaseValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Message;

class ValidatorWrapperTest {
  private static final String TEST_NAME = "testname";

  //  @Test
  void When_validate_Expect_Success() {
    // arrange
    Message message = mock(Message.class);
    Exchange thenPhaseDefinition = mock(Exchange.class, RETURNS_DEEP_STUBS);
    Exchange result = mock(Exchange.class);
    TestCaseValidator subject = mock(TestCaseValidator.class);
    TestExecutionStatus testExecutionStatus = new TestExecutionStatus();
    ValidationResult validationResult = new ValidationResult(true, "message");
    List<ValidationResult> validationResultList = new ArrayList<>();
    when(thenPhaseDefinition.getMessage()).thenReturn(message);
    when(message.getHeaders()).thenReturn(new HashMap<>());
    when(result.getMessage()).thenReturn(message);
    validationResultList.add(validationResult);
    testExecutionStatus.getAdapterReport().setActualResponse(result);

    // act
    subject.validate(testExecutionStatus);

    // assert
    assertThat(testExecutionStatus.isSuccessfulExecution()).isTrue();
  }
}
