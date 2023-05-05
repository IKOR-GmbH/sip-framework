package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationContext;
import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface ScenarioContextPredicate<M> extends Predicate<ScenarioOrchestrationContext<M>> {

    /**
     * Test if the content of a header exists and is equal to the expected value
     * @param headerName Name of the header
     * @param expectedValue Expected value to test for
     * @return <code>true</code> if this header exists and is of expected value
     * @param <T> Element type
     */
    default <T> ScenarioContextPredicate<M> headerEquals(final String headerName, final T expectedValue) {
        Objects.requireNonNull(expectedValue);
        return context -> context.getHeader(headerName, expectedValue.getClass()).map(expectedValue::equals).orElse(false);
    }

    /**
     * Tests if a specific header exists
     * @param headerName Name of the header
     * @return <code>true</code> if a header with that name exists
     */
    default ScenarioContextPredicate<M> hasHeader(final String headerName) {
        return context -> context.getHeader(headerName, Object.class).isPresent();
    }

    /**
     * Tests the original request using the given predicate
     * @param requestPredicate Predicate to test against the original request
     * @param requestType Request type class
     * @return Test result
     * @param <T> Request type
     */
    default <T> ScenarioContextPredicate<M> testOriginalRequest(final Predicate<T> requestPredicate, final Class<T> requestType) {
        return context -> requestPredicate.test(context.getOriginalRequest(requestType));
    }

    /**
     * Tests the current response against the given predicate
     * @see ScenarioOrchestrationContext#getResponse()
     * @param responsePredicate Predicate to test response against
     * @return Test result
     */
    default ScenarioContextPredicate<M> testResponse(final Predicate<M> responsePredicate) {
        return context -> context.getResponse().map(responsePredicate::test).orElse(false);
    }

}
