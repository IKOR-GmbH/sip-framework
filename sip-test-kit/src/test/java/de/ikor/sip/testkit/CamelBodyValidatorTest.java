package de.ikor.sip.testkit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import de.ikor.sip.testkit.workflow.thenphase.result.ValidationResult;
import de.ikor.sip.testkit.workflow.thenphase.validator.impl.CamelBodyValidator;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CamelBodyValidatorTest {

  CamelBodyValidator bodyValidator;
  Exchange result;
  Exchange expected;
  private static final String RESULT = "test";

  @BeforeEach
  public void setUp() {
    bodyValidator = new CamelBodyValidator();
    result = mock(Exchange.class, RETURNS_DEEP_STUBS);
    expected = mock(Exchange.class, RETURNS_DEEP_STUBS);
  }

  @Test
  void execute_Success() {
    Message resultMessage = mock(Message.class);
    Message expectedMessage = mock(Message.class);
    when(result.getMessage()).thenReturn(resultMessage);
    when(expected.getMessage()).thenReturn(expectedMessage);
    when(resultMessage.getBody()).thenReturn(RESULT);
    when(expectedMessage.getBody()).thenReturn(RESULT);
    ValidationResult validationResultSuccessful =
        new ValidationResult(true, "Body validation successful");

    ValidationResult validationResult = bodyValidator.execute(result, expected);

    assertEquals(validationResultSuccessful, validationResult);
  }

  @Test
  void execute_Fail() {
    Message resultMessage = mock(Message.class);
    Message expectedMessage = mock(Message.class);
    when(result.getMessage()).thenReturn(resultMessage);
    when(expected.getMessage()).thenReturn(expectedMessage);
    when(resultMessage.getBody()).thenReturn(RESULT);
    when(expectedMessage.getBody()).thenReturn("null");
    ValidationResult validationResultUnsuccessful =
        new ValidationResult(false, "Body validation unsuccessful");

    ValidationResult validationResult = bodyValidator.execute(result, expected);

    assertThat(validationResult).isEqualTo(validationResultUnsuccessful);
  }

  @Test
  void isApplicable_Success() {
    when(expected.getMessage().getBody()).thenReturn(RESULT);

    boolean isApplicable = bodyValidator.isApplicable(result, expected);

    assertThat(isApplicable).isTrue();
  }

  @Test
  void isApplicable_MissingBody() {
    when(expected.getMessage().getBody()).thenReturn(null);

    boolean isApplicable = bodyValidator.isApplicable(result, expected);

    assertThat(isApplicable).isFalse();
  }
}
