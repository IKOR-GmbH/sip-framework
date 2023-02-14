package de.ikor.sip.foundation.core.declarative.connectorgroup;

import lombok.AllArgsConstructor;

/**
 * Default connector which is automatically created by framework when connector is not specified by
 * user.
 *
 * <p><em>For internal use only.</em>
 */
@AllArgsConstructor
public class DefaultConnectorGroup implements ConnectorGroupDefinition {

  private static final String EMPTY_PATH = "";

  private String connectorId;

  @Override
  public String getId() {
    return connectorId;
  }

  @Override
  public String getPathToDocumentationResource() {
    return EMPTY_PATH;
  }
}
