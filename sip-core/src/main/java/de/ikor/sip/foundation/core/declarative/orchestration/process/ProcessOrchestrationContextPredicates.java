package de.ikor.sip.foundation.core.declarative.orchestration.process;

import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationContext;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Predicate;

@UtilityClass
public class ProcessOrchestrationContextPredicates {

  public static CompositeProcessStepConditional headerEquals(
      final String headerName, final Object expectedValue, Class<?> valueType) {
    Objects.requireNonNull(expectedValue);
    return context ->
        context.getHeader(headerName, valueType).map(expectedValue::equals).orElse(false);
  }

  /**
   * Tests if a specific header exists
   *
   * @param headerName Name of the header
   * @return <code>true</code> if a header with that name exists
   */
  public static CompositeProcessStepConditional hasHeader(final String headerName) {
    return context -> context.getHeader(headerName, Object.class).isPresent();
  }

  /**
   * Tests the original request using the given predicate
   *
   * @param requestPredicate Predicate to test against the original request
   * @return Test result
   */
  public static CompositeProcessStepConditional originalRequestMatches(
      final Predicate<?> requestPredicate) {
    return context -> requestPredicate.test(context.getOriginalRequest());
  }

  /**
   * Tests the current response against the given predicate
   *
   * @see ScenarioOrchestrationContext#getResponse()
   * @param responsePredicate Predicate to test response against
   * @return Test result
   */
  public static CompositeProcessStepConditional responseMatches(
      final Predicate<Object> responsePredicate) {
    return context ->
        (boolean) context.getLatestResponse().map(responsePredicate::test).orElse(false);
  }
}
