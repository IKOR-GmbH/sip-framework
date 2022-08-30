package de.ikor.sip.foundation.testkit.workflow.thenphase.validator.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.thenphase.result.ValidationResult;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CamelBodyValidatorTest {

  private static final ValidationResult VALIDATION_RESULT_SUCCESSFUL =
      new ValidationResult(true, "Body validation successful");
  private static final ValidationResult VALIDATION_RESULT_UNSUCCESSFUL =
      new ValidationResult(false, "Body validation unsuccessful");

  CamelBodyValidator bodyValidatorSubject;
  Exchange result;
  Exchange expected;
  private static final String RESULT = "test";

  @BeforeEach
  public void setUp() {
    bodyValidatorSubject = new CamelBodyValidator();
    result = mock(Exchange.class, RETURNS_DEEP_STUBS);
    expected = mock(Exchange.class, RETURNS_DEEP_STUBS);
  }

  @Test
  void When_execute_Expect_Success() {
    Message resultMessage = mock(Message.class);
    Message expectedMessage = mock(Message.class);
    when(result.getMessage()).thenReturn(resultMessage);
    when(expected.getMessage()).thenReturn(expectedMessage);
    when(resultMessage.getBody()).thenReturn(RESULT);
    when(expectedMessage.getBody()).thenReturn(RESULT);

    ValidationResult validationResult = bodyValidatorSubject.execute(result, expected);

    assertEquals(VALIDATION_RESULT_SUCCESSFUL, validationResult);
  }

  @Test
  void When_execute_With_DifferentActualAndExpected_Then_Fail() {
    Message resultMessage = mock(Message.class);
    Message expectedMessage = mock(Message.class);
    when(result.getMessage()).thenReturn(resultMessage);
    when(expected.getMessage()).thenReturn(expectedMessage);
    when(resultMessage.getBody()).thenReturn(RESULT);
    when(expectedMessage.getBody()).thenReturn("null");

    ValidationResult validationResult = bodyValidatorSubject.execute(result, expected);

    assertThat(validationResult).isEqualTo(VALIDATION_RESULT_UNSUCCESSFUL);
  }

  @Test
  void When_execute_With_NullActual_Then_Fail() {
    Message resultMessage = mock(Message.class);
    Message expectedMessage = mock(Message.class);
    when(result.getMessage()).thenReturn(resultMessage);
    when(expected.getMessage()).thenReturn(expectedMessage);
    when(resultMessage.getBody()).thenReturn(null);
    when(expectedMessage.getBody()).thenReturn("null");

    ValidationResult validationResult = bodyValidatorSubject.execute(result, expected);

    assertThat(validationResult).isEqualTo(VALIDATION_RESULT_UNSUCCESSFUL);
  }

  @Test
  void When_execute_With_NullActualAndEmptyExpected_Then_Success() {
    Message resultMessage = mock(Message.class);
    Message expectedMessage = mock(Message.class);
    when(result.getMessage()).thenReturn(resultMessage);
    when(expected.getMessage()).thenReturn(expectedMessage);
    when(resultMessage.getBody()).thenReturn(null);
    when(expectedMessage.getBody()).thenReturn("");

    ValidationResult validationResult = bodyValidatorSubject.execute(result, expected);

    assertThat(validationResult).isEqualTo(VALIDATION_RESULT_SUCCESSFUL);
  }

  @Test
  void When_execute_With_EmptyActualAndExpected_Then_Success() {
    Message resultMessage = mock(Message.class);
    Message expectedMessage = mock(Message.class);
    when(result.getMessage()).thenReturn(resultMessage);
    when(expected.getMessage()).thenReturn(expectedMessage);
    when(resultMessage.getBody()).thenReturn("");
    when(expectedMessage.getBody()).thenReturn("");

    ValidationResult validationResult = bodyValidatorSubject.execute(result, expected);

    assertThat(validationResult).isEqualTo(VALIDATION_RESULT_SUCCESSFUL);
  }

  @Test
  void When_isApplicable_Expect_Success() {
    when(expected.getMessage().getBody()).thenReturn(RESULT);

    boolean isApplicable = bodyValidatorSubject.isApplicable(result, expected);

    assertThat(isApplicable).isTrue();
  }

  @Test
  void When_isApplicable_With_MissingBody_Then_Fail() {
    when(expected.getMessage().getBody()).thenReturn(null);

    boolean isApplicable = bodyValidatorSubject.isApplicable(result, expected);

    assertThat(isApplicable).isFalse();
  }
}
