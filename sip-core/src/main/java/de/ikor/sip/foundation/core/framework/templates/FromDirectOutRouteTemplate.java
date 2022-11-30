package de.ikor.sip.foundation.core.framework.templates;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.generateRouteId;

@AllArgsConstructor
public class FromDirectOutRouteTemplate {
  private String useCase;

  public RouteBuilder bindWithRouteBuilder(OutConnector outConnector) {
    outConnector.configureOnException();

    String routeId = generateRouteId(useCase, outConnector.getName());
    RouteDefinition connectorRouteDefinition =
        outConnector.getRouteBuilder().from("direct:" + outConnector.getName()).routeId(routeId);

    outConnector.configure(connectorRouteDefinition);
    return outConnector.getRouteBuilder();
  }
}
