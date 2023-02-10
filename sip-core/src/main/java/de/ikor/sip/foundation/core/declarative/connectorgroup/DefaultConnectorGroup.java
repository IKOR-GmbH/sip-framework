package de.ikor.sip.foundation.core.declarative.connectorgroup;

import lombok.AllArgsConstructor;

/**
 * Default connector which is automatically created by framework when connector is not specified by
 * user.
 */
@AllArgsConstructor
public class DefaultConnectorGroup implements ConnectorGroupDefinition {

  private static final String EMPTY_PATH = "";

  private String connectorId;

  @Override
  public String getDocumentation() {
    return readDocumentation(EMPTY_PATH);
  }

  @Override
  public String getID() {
    return connectorId;
  }
}
