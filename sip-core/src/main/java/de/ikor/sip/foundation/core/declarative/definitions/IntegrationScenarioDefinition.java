package de.ikor.sip.foundation.core.declarative.definitions;

public interface IntegrationScenarioDefinition<T> {

  String getID();

  String getDescription();

  Class<? extends T> getDomainModelBaseClass();
}
