package de.ikor.sip.foundation.core.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.*;
import org.apache.commons.collections4.CollectionUtils;

import static de.ikor.sip.foundation.core.framework.CentralRouter.anonymousDummyRouteBuilder;

@RequiredArgsConstructor
public class UseCaseTopologyDefinition {
  private static final String TESTING_SUFFIX = "-testing";
  private static final String URI_PREFIX = "direct:";
  private final CamelContext camelContext;
  private final String useCase;

  private MulticastDefinition multicastDefinition = null;
  private MulticastDefinition testingDefinition = null;
  @Getter private RouteBuilder routeBuilder;
//  private final RouteBuilder testingRouteBuilder = CentralRouter.anonymousDummyRouteBuilder();

  public UseCaseTopologyDefinition to(OutConnector... outConnectors) throws Exception {
    routeBuilder = getRouteBuilderInstance();
    multicastDefinition = initMulticastRoute(routeBuilder, multicastDefinition, "");
    multicastDefinition =
        appendParallelProcessingIfMultipleConnectors(multicastDefinition, outConnectors);
    Stream.of(outConnectors)
        .forEach(
            outConnector -> {
              outConnector.configureOnConnectorLevel();
              multicastDefinition.to(URI_PREFIX + outConnector.getName());
              this.from(outConnector, URI_PREFIX + outConnector.getName(), "");
            });
    generateTestRoutes(outConnectors);
    return this;
  }

  private void generateTestRoutes(OutConnector... outConnectors) {
    createNewRouteBuilder();
    testingDefinition = initMulticastRoute(routeBuilder, testingDefinition, TESTING_SUFFIX);      // bio je testingRouteBuilder
    testingDefinition =
        appendParallelProcessingIfMultipleConnectors(testingDefinition, outConnectors);
    CentralEndpointsRegister.setState("testing");
    Stream.of(outConnectors)
        .forEach(
            outConnector -> {
              outConnector.configureOnConnectorLevel();
              testingDefinition.to(URI_PREFIX + outConnector.getName() + TESTING_SUFFIX);
              this.from(outConnector, URI_PREFIX + outConnector.getName(), TESTING_SUFFIX);
            });
    CentralEndpointsRegister.setState("actual");
  }

  private MulticastDefinition appendParallelProcessingIfMultipleConnectors(
      MulticastDefinition multicastDefinition, OutConnector[] outConnectors) {
    if (outConnectors.length > 1) {
      return multicastDefinition.parallelProcessing();
    }
    return multicastDefinition;
  }

  public void build() throws Exception {      // verovatno se moze obrisati uz onaj definition.build
//    camelContext.addRoutes(this.routeBuilder);
//    camelContext.addRoutes(this.testingRouteBuilder);
  }

  private void createNewRouteBuilder() {
    routeBuilder = anonymousDummyRouteBuilder();
  }

  private MulticastDefinition initMulticastRoute(
      RouteBuilder routeBuilder, MulticastDefinition multicastDefinition, String suffix) {
    String uri = "sipmc:" + useCase + suffix;
    return multicastDefinition == null ? routeBuilder.from(uri).multicast() : multicastDefinition;
  }

  @SneakyThrows
  private UseCaseTopologyDefinition from(
      OutConnector outConnector, String uri, String routeSuffix) {
    RouteBuilder rb = routeBuilder;                 // routeSuffix.equals(TESTING_SUFFIX) ? testingRouteBuilder : routeBuilder;
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

  private RouteBuilder getRouteBuilderInstance() {
    if (routeBuilder == null) {
      return anonymousDummyRouteBuilder();
    }
    return routeBuilder;
  }

}
