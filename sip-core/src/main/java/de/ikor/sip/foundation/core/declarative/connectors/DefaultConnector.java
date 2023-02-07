package de.ikor.sip.foundation.core.declarative.connectors;

import lombok.AllArgsConstructor;

/**
 * Default connector which is automatically created by framework when connector is not specified by
 * user.
 */
@AllArgsConstructor
public class DefaultConnector implements ConnectorDefinition {

  private static final String EMPTY_PATH = "";

  private String connectorId;

  @Override
  public String getID() {
    return connectorId;
  }

  @Override
  public String getDocumentation() {
    return readDocumentation(EMPTY_PATH);
  }
}
