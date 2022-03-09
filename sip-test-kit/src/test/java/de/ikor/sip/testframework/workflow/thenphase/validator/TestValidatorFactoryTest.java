package de.ikor.sip.testframework.workflow.thenphase.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import de.ikor.sip.testframework.exception.TestCaseInitializationException;
import de.ikor.sip.testframework.workflow.thenphase.result.ValidationType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestValidatorFactoryTest {

  TestValidatorFactory subject;
  List<TestCaseValidator> testCaseValidators;
  TestCaseValidator fullValidator;

  @BeforeEach
  void setup() {
    fullValidator = mock(TestCaseValidator.class, CALLS_REAL_METHODS);
    testCaseValidators = new ArrayList<>();
    subject = new TestValidatorFactory(testCaseValidators);
  }

  @Test
  void When_getValidator_With_FullValidation_Then_FullValidator() {
    testCaseValidators.add(fullValidator);
    when(fullValidator.getValidationType()).thenReturn(ValidationType.FULL);

    TestCaseValidator result = subject.getValidator(ValidationType.FULL);

    assertThat(result).isEqualTo(fullValidator);
  }

  @Test
  void When_getValidator_With_NoValidation_Then_NoValidator() {
    testCaseValidators.add(fullValidator);
    when(fullValidator.getValidationType()).thenReturn(ValidationType.FULL);
    TestCaseValidator noValidator = mock(TestCaseValidator.class, CALLS_REAL_METHODS);
    when(noValidator.getValidationType()).thenReturn(ValidationType.NO_VALIDATION);
    testCaseValidators.add(noValidator);

    TestCaseValidator result = subject.getValidator(ValidationType.NO_VALIDATION);

    assertThat(result).isEqualTo(noValidator);
  }

  @Test
  void When_getValidator_With_MissingValidator_Then_TestCaseInitException() {
    assertThatThrownBy(() -> subject.getValidator(ValidationType.NO_VALIDATION))
        .isInstanceOf(TestCaseInitializationException.class);
  }
}
