package de.ikor.sip.foundation.testkit.workflow.thenphase.validator.impl;

import de.ikor.sip.foundation.testkit.workflow.thenphase.result.ValidationResult;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CamelBodyValidatorTest {

    private static final ValidationResult VALIDATION_RESULT_SUCCESSFUL =
            new ValidationResult(true, "Body validation successful");
    private static final ValidationResult VALIDATION_RESULT_UNSUCCESSFUL =
            new ValidationResult(false, "Body validation unsuccessful");

    private final CamelBodyValidator bodyValidatorSubject = new CamelBodyValidator();
    private Exchange actual;
    private Exchange expected;

    @BeforeEach
    public void setUp() {
        // reset mocks
        actual = mock(Exchange.class, RETURNS_DEEP_STUBS);
        expected = mock(Exchange.class, RETURNS_DEEP_STUBS);
    }

    @ParameterizedTest
    @ValueSource(strings = {EMPTY, "some value", "null"})
    void When_expectedBodyHasValue_Then_validatorIsApplicableForAnyValueOfActual(String actualValue) {
        when(expected.getMessage().getBody()).thenReturn("any content");
        actualValue = parserNullStringToNull(actualValue);
        lenient().when(actual.getMessage().getBody()).thenReturn(actualValue);

        boolean isApplicable = bodyValidatorSubject.isApplicable(actual, expected);

        assertThat(isApplicable).describedAs("Body validator should be applicable if expected has concrete value, regardless of actual value").isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {EMPTY, "some value", "null"})
    void When_expectedBodyHasEmptyStringAsValue_Then_validatorIsApplicableForAnyValueOfActual(String actualValue) {
        when(expected.getMessage().getBody()).thenReturn(EMPTY);
        actualValue = parserNullStringToNull(actualValue);
        lenient().when(actual.getMessage().getBody()).thenReturn(actualValue);

        boolean isApplicable = bodyValidatorSubject.isApplicable(actual, expected);

        assertThat(isApplicable).describedAs("Body validator should be applicable if expected has empty string value, regardless of actual value").isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {EMPTY, "some value", "null"})
    void When_expectedBodyIsNull_Then_validatorIsNotApplicableForAnyValueOfActual(String actualValue) {
        when(expected.getMessage().getBody()).thenReturn(null);
        actualValue = parserNullStringToNull(actualValue);
        lenient().when(actual.getMessage().getBody()).thenReturn(actualValue);

        boolean isApplicable = bodyValidatorSubject.isApplicable(actual, expected);

        assertThat(isApplicable).describedAs("Body validator should not be applicable if expected value is null, regardless of actual value").isFalse();
    }

    @Test
    void When_actualAndExpectedAreTheSame_Then_validationPasses() {
        when(actual.getMessage().getBody()).thenReturn("some content");
        when(expected.getMessage().getBody()).thenReturn("some content");

        ValidationResult validationResult = bodyValidatorSubject.execute(actual, expected);

        assertEquals(VALIDATION_RESULT_SUCCESSFUL, validationResult);
    }

    @Test
    void When_actualAndExpectedAreDifferent_Then_validationFails() {
        when(actual.getMessage().getBody()).thenReturn("some content");
        when(expected.getMessage().getBody()).thenReturn("some other content");

        ValidationResult validationResult = bodyValidatorSubject.execute(actual, expected);

        assertThat(validationResult).isEqualTo(VALIDATION_RESULT_UNSUCCESSFUL);
    }

    @Test
    void When_expectedHasValueAndActualIsNull_Then_validationFails() {
        when(actual.getMessage().getBody()).thenReturn(null);
        when(actual.getMessage().getBody()).thenReturn("test");

        ValidationResult validationResult = bodyValidatorSubject.execute(actual, expected);

        assertThat(validationResult).isEqualTo(VALIDATION_RESULT_UNSUCCESSFUL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", ""})
    void When_expectedIsEmptyAndActualIsNullOrEmpty_Then_validationPasses(String actualValue) {
        actualValue = parserNullStringToNull(actualValue);
        when(actual.getMessage().getBody()).thenReturn(actualValue);
        when(expected.getMessage().getBody()).thenReturn(EMPTY);

        ValidationResult validationResult = bodyValidatorSubject.execute(actual, expected);

        assertThat(validationResult).isEqualTo(VALIDATION_RESULT_SUCCESSFUL);
    }

    private static String parserNullStringToNull(String actualValue) {
        return "null".equals(actualValue) ? null : actualValue;
    }
}
