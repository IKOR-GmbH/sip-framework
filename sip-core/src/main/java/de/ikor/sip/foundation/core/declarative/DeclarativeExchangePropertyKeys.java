package de.ikor.sip.foundation.core.declarative;

public enum DeclarativeExchangePropertyKeys {
  INTEGRATION_SCENARIO("SipIntegrationScenario"),
  SOURCE_CONNECTOR("SipCallerSourceConnector");

  private final String propertyKey;

  DeclarativeExchangePropertyKeys(final String propertyKey) {
    this.propertyKey = propertyKey;
  }

  public String getPropertyKey() {
    return propertyKey;
  }
}
