package de.ikor.sip.foundation.core.framework.templates;

import de.ikor.sip.foundation.core.framework.connectors.ConnectorStarter;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.RouteDefinition;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.*;

@AllArgsConstructor
public class FromDirectOutConnectorRouteTemplate {
  private String useCase;
  private RouteConfigurationBuilder routeConfigurationBuilder;

  public RouteBuilder bindWithRouteBuilder(OutConnector outConnector) {
    ConnectorStarter.initConnector(outConnector, routeConfigurationBuilder);
    outConnector.configureOnException();

    String routeId = generateRouteId(useCase, outConnector.getName());
    RouteDefinition connectorRouteDefinition =
        outConnector.getRouteBuilder().from("direct:" + outConnector.getName()).routeId(routeId);

    outConnector.configure(connectorRouteDefinition);
    return outConnector.getRouteBuilder();
  }
}
