package de.ikor.sip.foundation.core.framework.templates;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.*;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.TemplatedRouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.stereotype.Component;

@AllArgsConstructor
public class FromDirectOutConnectorRouteTemplate {
  private String useCase;
  public static final String USE_CASE_PARAM_KEY = "use-case";

  public void fromMCMulticastRoute(OutConnectorDefinition[] outConnectors) {
    // TODO setting routeID does not work. Double check why and choose way to go
    RouteDefinition definition = FromDirectOutConnectorRouteTemplate.Template.getDefinition();
    Stream.of(outConnectors)
        .forEach(
            outConnector -> {
              definition.setId(useCase + outConnector.getName());
              //              outConnector.setRouteDefinition(definition);//toggling between
              // definition and builder setter on OutConnectorDefinition
              outConnector.configureOnException(); // TODO is this the perfect place?
              outConnector.configure(definition);
              TemplatedRouteBuilder parameter =
                  TemplatedRouteBuilder.builder(camelContext(), "direct-out-connector")
                      .parameter("out-connector-name", outConnector.getName())
                      .parameter(USE_CASE_PARAM_KEY, useCase);
              parameter.add();
            });
  }

  public void fromCustomRouteBuilder(OutConnectorDefinition[] outConnectors) {
    for (OutConnectorDefinition connector : outConnectors) {
      addOutConnectorRoute(connector);
    }
  }

  private void addOutConnectorRoute(OutConnectorDefinition outConnector) {
    RouteBuilder rb = anonymousDummyRouteBuilder();
    outConnector.setRouteBuilder(rb);
    outConnector.configureOnException(); // TODO split configException from route building,
    // connector.configure and adding route to context

    String routeId = generateRouteId(useCase, outConnector.getName());
    RouteDefinition connectorRouteDefinition =
        rb.from("direct:" + outConnector.getName()).routeId(routeId);

    outConnector.configure(connectorRouteDefinition);
    try {
      camelContext().addRoutes(rb);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }

  @Component
  public static class Template extends RouteBuilder {
    private static RouteDefinition definition;

    @Override
    public void configure() throws Exception {
      synchronized (this) {
        definition =
            routeTemplate("direct-out-connector")
                .templateParameter(USE_CASE_PARAM_KEY)
                .templateParameter("out-connector-name")
                .from("direct:{{out-connector-name}}");
      }
    }

    public static RouteDefinition getDefinition() {
      return definition;
    }
  }
}
