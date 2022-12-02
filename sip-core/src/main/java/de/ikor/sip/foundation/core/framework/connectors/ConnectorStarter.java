package de.ikor.sip.foundation.core.framework.connectors;

import org.apache.camel.builder.RouteConfigurationBuilder;

public class ConnectorStarter {
  private ConnectorStarter() {}

  public static void initConnector(
      Connector connector, RouteConfigurationBuilder configurationBuilder) {
    connector.initBuilders(configurationBuilder);
  }
}
