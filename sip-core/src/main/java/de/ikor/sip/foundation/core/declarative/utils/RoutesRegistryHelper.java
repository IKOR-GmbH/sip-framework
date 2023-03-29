package de.ikor.sip.foundation.core.declarative.utils;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RoutesRegistryHelper {

  public static String getConnectorId(ConnectorDefinition connector) {
    return connector != null ? connector.getId() : null;
  }
}
