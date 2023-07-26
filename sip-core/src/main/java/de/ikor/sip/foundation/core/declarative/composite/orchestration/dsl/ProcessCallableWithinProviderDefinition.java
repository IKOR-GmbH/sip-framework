package de.ikor.sip.foundation.core.declarative.composite.orchestration.dsl;

/**
 * Marker interface for elements that can be nodes below a provider-definition within the scenario
 * orchestration DSL
 */
public sealed interface ProcessCallableWithinProviderDefinition permits CallProcessConsumer {}
