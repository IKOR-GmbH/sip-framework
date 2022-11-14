package de.ikor.sip.foundation.core.framework.templates;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.util.TestingRoutesUtil;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.TemplatedRouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.*;
import static de.ikor.sip.foundation.core.framework.templates.FromMiddleComponentRouteTemplate.SUFFIX_PARAM_KEY;
import static de.ikor.sip.foundation.core.framework.templates.FromMiddleComponentRouteTemplate.USE_CASE_PARAM_KEY;
import static de.ikor.sip.foundation.core.framework.util.TestingRoutesUtil.TESTING_SUFFIX;

@AllArgsConstructor
public class FromDirectOutConnectorRouteTemplate {
  private String useCase;
  private String suffix;

  public void fromMCMulticastRoute(OutConnector[] outConnectors) {
    // TODO setting routeID does not work. Double check why and choose way to go
    RouteDefinition definition = FromDirectOutConnectorRouteTemplate.Template.getDefinition();
    Stream.of(outConnectors)
        .forEach(
            outConnector -> {
              definition.setId(useCase + outConnector.getName() + suffix);
              //              outConnector.setRouteDefinition(definition);//toggling between
              // definition and builder setter on OutConnector
              outConnector.configureOnException(); // TODO is this the perfect place?
              outConnector.configure(definition);
              TemplatedRouteBuilder parameter =
                  TemplatedRouteBuilder.builder(camelContext(), "direct-out-connector")
                      .parameter("out-connector-name", outConnector.getName())
                      .parameter(USE_CASE_PARAM_KEY, useCase)
                      .parameter(SUFFIX_PARAM_KEY, suffix);
              parameter.add();
            });
  }

  public void fromCustomRouteBuilder(OutConnector[] outConnectors) {
    for (OutConnector connector : outConnectors) {
      addOutConnectorRoute(connector);
    }
  }

  private void addOutConnectorRoute(OutConnector outConnector) {
    RouteBuilder rb = anonymousDummyRouteBuilder();
    outConnector.setRouteBuilder(rb);
    outConnector
        .configureOnException(); // TODO split configException from route building,
                                 // connector.configure and adding route to context

    String routeId = generateRouteId(useCase, outConnector.getName(), suffix);
    RouteDefinition connectorRouteDefinition =
        rb.from("direct:" + outConnector.getName() + suffix).routeId(routeId);

    outConnector.configure(connectorRouteDefinition);
    if (TESTING_SUFFIX.equals(suffix)) {
      connectorRouteDefinition.getOutputs().forEach(TestingRoutesUtil::handleTestIDAppending);
    }
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
                .templateParameter(SUFFIX_PARAM_KEY, "")
                .templateParameter("out-connector-name")
                .from("direct:{{out-connector-name}}")
        //                .routeId("{{use-case}}-{{out-connector-name}}-{{suffix}}")
        ;
      }
    }

    public static RouteDefinition getDefinition() {
      return definition;
    }
  }
}
