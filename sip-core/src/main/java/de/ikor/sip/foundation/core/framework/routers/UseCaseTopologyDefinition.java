package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.*;
import org.apache.commons.collections4.CollectionUtils;

public class UseCaseTopologyDefinition {
  private static final String TESTING_SUFFIX = "-testing";
  private static final String URI_PREFIX = "direct:";
  private final CamelContext camelContext;
  private final String useCase;
  private final RouteConfigurationBuilder configurationBuilder;

  private ProcessorDefinition routeDefinition = null;
  private ProcessorDefinition testRouteDefinition = null;
  @Getter private RouteBuilder routeBuilder;
  private RouteBuilder testingRouteBuilder;

  public UseCaseTopologyDefinition(
      CamelContext camelContext, String useCase, RouteConfigurationBuilder configurationBuilder) {
    this.camelContext = camelContext;
    this.useCase = useCase;
    this.configurationBuilder = configurationBuilder;
    routeBuilder = CentralRouter.anonymousDummyRouteBuilder(configurationBuilder);
    testingRouteBuilder = CentralRouter.anonymousDummyRouteBuilder(configurationBuilder);
  }

  public UseCaseTopologyDefinition to(OutConnector... outConnectors) {
    routeBuilder = CentralRouter.anonymousDummyRouteBuilder(configurationBuilder);
    routeDefinition = initBaseRoute(routeBuilder, routeDefinition, "");
    if (outConnectors.length > 1) {
      routeDefinition = appendMulticastDefinition(outConnectors, routeDefinition, "");
    } else {
      OutConnector outConnector = outConnectors[0];
      routeDefinition.to(URI_PREFIX + outConnector.getName());
      outConnector.setRouteBuilder(routeBuilder);
      outConnector.configureOnConnectorLevel();
      this.from(outConnector, URI_PREFIX + outConnector.getName(), "");
    }
    routeBuilder.getRouteCollection().getRoutes().add((RouteDefinition) routeDefinition);
    generateTestRoutes(outConnectors);
    return this;
  }

  private void generateTestRoutes(OutConnector... outConnectors) {
    testingRouteBuilder = CentralRouter.anonymousDummyRouteBuilder(configurationBuilder);
    testRouteDefinition = initBaseRoute(testingRouteBuilder, testRouteDefinition, TESTING_SUFFIX);
    CentralEndpointsRegister.setState("testing");
    if (outConnectors.length > 1) {
      testRouteDefinition =
          appendMulticastDefinition(outConnectors, testRouteDefinition, TESTING_SUFFIX);
    } else {
      OutConnector outConnector = outConnectors[0];
      testRouteDefinition =
          testRouteDefinition.to(URI_PREFIX + outConnector.getName() + TESTING_SUFFIX);
      outConnector.setRouteBuilder(testingRouteBuilder);
      outConnector.configureOnConnectorLevel();
      this.from(outConnector, URI_PREFIX + outConnector.getName(), TESTING_SUFFIX);
    }
    testingRouteBuilder.getRouteCollection().getRoutes().add((RouteDefinition) testRouteDefinition);
    CentralEndpointsRegister.setState("actual");
  }

  private ProcessorDefinition appendMulticastDefinition(
      OutConnector[] outConnectors, ProcessorDefinition processorDefinition, String suffix) {
    MulticastDefinition multicastDefinition = processorDefinition.multicast().parallelProcessing();
    Stream.of(outConnectors)
        .forEach(
            outConnector -> {
              multicastDefinition.to(URI_PREFIX + outConnector.getName() + suffix);
              outConnector.setRouteBuilder(
                  suffix.equals(TESTING_SUFFIX) ? testingRouteBuilder : routeBuilder);
              outConnector.configureOnConnectorLevel();
              this.from(outConnector, URI_PREFIX + outConnector.getName(), suffix);
            });
    return multicastDefinition.end();
  }

  public void build() throws Exception {
    camelContext.addRoutes(this.routeBuilder);
    camelContext.addRoutes(this.testingRouteBuilder);
  }

  private ProcessorDefinition initBaseRoute(
      RouteBuilder routeBuilder, ProcessorDefinition processorDefinition, String suffix) {
    String uri = "sipmc:" + useCase + suffix;
    RouteDefinition rd = new RouteDefinition();

    return processorDefinition == null
        ? rd.from(uri).routeId("sipmc-bridge-" + useCase + suffix)
        : processorDefinition;
  }

  @SneakyThrows
  private UseCaseTopologyDefinition from(
      OutConnector outConnector, String uri, String routeSuffix) {
    RouteBuilder rb = CentralRouter.anonymousDummyRouteBuilder(configurationBuilder);
    outConnector.setRouteBuilder(rb);
    outConnector.configureOnConnectorLevel();
    String routeId = CentralRouter.generateRouteId(useCase, outConnector.getName(), routeSuffix);
    RouteDefinition routeDefinition = rb.from(uri + routeSuffix).routeId(routeId);
    outConnector.configure(routeDefinition);
    // add testing suffix to processor definition id to prevent id duplication
    if (TESTING_SUFFIX.equals(routeSuffix)) {
      routeDefinition.getOutputs().forEach(this::handleTestIDAppending);
    }
    camelContext.addRoutes(rb);

    return this;
  }

  private void handleTestIDAppending(ProcessorDefinition<?> processorDefinition) {
    if (processorDefinition.getId() != null) {
      appendTestIdToProcessor(processorDefinition);
    }
    if (CollectionUtils.isNotEmpty(processorDefinition.getOutputs())) {
      processorDefinition.getOutputs().forEach(this::handleTestIDAppending);
    }
  }

  private void appendTestIdToProcessor(ProcessorDefinition<?> processorDefinition) {
    String id = processorDefinition.getId();
    if (processorDefinition instanceof ChoiceDefinition) {
      // handler for setId of ChoiceDefinition due to its custom implementation
      handleChoiceDefinitionID((ChoiceDefinition) processorDefinition, id);
    }
    processorDefinition.setId(id + TESTING_SUFFIX);
  }

  private void handleChoiceDefinitionID(ChoiceDefinition choiceDefinition, String id) {
    // Save reference to when and otherwise definition
    List<WhenDefinition> whenDefinitions = choiceDefinition.getWhenClauses();
    OtherwiseDefinition otherwiseDefinition = choiceDefinition.getOtherwise();
    // remove when and otherwise definition so id would be set on choice definition
    choiceDefinition.setWhenClauses(new ArrayList<>());
    choiceDefinition.setOtherwise(null);
    // set choice definition id with testing suffix
    choiceDefinition.setId(id + TESTING_SUFFIX);
    // place back original when and otherwise definition
    choiceDefinition.setWhenClauses(whenDefinitions);
    choiceDefinition.setOtherwise(otherwiseDefinition);
  }
}
