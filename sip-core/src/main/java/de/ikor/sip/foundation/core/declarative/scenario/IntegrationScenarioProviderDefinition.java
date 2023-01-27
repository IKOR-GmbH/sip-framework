package de.ikor.sip.foundation.core.declarative.scenario;

public interface IntegrationScenarioProviderDefinition extends IntegrationScenarioParticipant {

  IntegrationScenarioDefinition getProvidedScenario();
}
