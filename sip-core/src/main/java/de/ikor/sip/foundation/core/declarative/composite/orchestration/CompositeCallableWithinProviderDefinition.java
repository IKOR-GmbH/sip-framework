package de.ikor.sip.foundation.core.declarative.composite.orchestration;

/**
 * Marker interface for elements that can be nodes below a provider-definition within the scenario
 * orchestration DSL
 */
sealed interface CompositeCallableWithinProviderDefinition
    permits CallCompositeScenarioConsumerBaseDefinition {}
