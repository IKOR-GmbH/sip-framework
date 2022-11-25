package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.Getter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.ProcessorDefinition;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.anonymousDummyRouteBuilder;

public class UseCaseTopologyDefinition {
  @Getter
  private final LinkedHashMap<OutConnectorDefinition[], String> allConnectors =
      new LinkedHashMap<>();

  public void sequencedOutput(OutConnectorDefinition... outConnectors) {
    allConnectors.put(outConnectors, "seq");
  }

  public void parallelOutput(OutConnectorDefinition... outConnectors) {
    allConnectors.put(outConnectors, "par");
  }


  //TODO move
//  private final String useCase;
//  private final RouteConfigurationBuilder configurationBuilder;

//  public UseCaseTopologyDefinition(String useCase, RouteConfigurationBuilder configurationBuilder) {
//    this.useCase = useCase;
//    this.configurationBuilder = configurationBuilder;
//    routeBuilder = anonymousDummyRouteBuilder(configurationBuilder);
//    testingRouteBuilder = anonymousDummyRouteBuilder(configurationBuilder);
//  }
}
