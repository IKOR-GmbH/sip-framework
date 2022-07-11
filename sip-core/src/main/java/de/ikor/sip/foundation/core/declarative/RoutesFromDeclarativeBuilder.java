package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.annotations.Connector;
import de.ikor.sip.foundation.core.declarative.annotations.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annotations.ScenarioParticipationIncoming;
import de.ikor.sip.foundation.core.declarative.annotations.ScenarioParticipationOutgoing;
import de.ikor.sip.foundation.core.declarative.definitions.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.definitions.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.definitions.ScenarioParticipationIncomingDefinition;
import de.ikor.sip.foundation.core.declarative.definitions.ScenarioParticipationOutgoingDefinition;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.StepDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
class RoutesFromDeclarativeBuilder extends RouteConfigurationBuilder {

  private static final String DEFAULT_ROUTE_CONFIG_ID = "sipDefaultConfig";

  private final ApplicationContext context;

  RoutesFromDeclarativeBuilder(@Autowired final ApplicationContext context) {
    this.context = context;
  }

  @Override
  public void configure() throws Exception {

    final var integrationScenarios =
        context.getBeansWithAnnotation(IntegrationScenario.class).values().stream()
            .map(IntegrationScenarioDefinition.class::cast)
            .collect(
                Collectors.toUnmodifiableMap(IntegrationScenarioDefinition::getID, scen -> scen));
    final var connectors =
        context.getBeansWithAnnotation(Connector.class).values().stream()
            .map(ConnectorDefinition.class::cast)
            .collect(Collectors.toUnmodifiableMap(ConnectorDefinition::getID, con -> con));

    for (final var connector : connectors.values()) {
      final var incomingParticipations =
          Arrays.stream(connector.getClass().getMethods())
              .filter(method -> method.isAnnotationPresent(ScenarioParticipationIncoming.class))
              .collect(Collectors.toUnmodifiableList());
      final var outgoingParticipations =
          Arrays.stream(connector.getClass().getMethods())
              .filter(method -> method.isAnnotationPresent(ScenarioParticipationOutgoing.class))
              .collect(Collectors.toUnmodifiableList());

      for (final var incomingMethod : incomingParticipations) {
        final String scenarioId =
            incomingMethod.getAnnotation(ScenarioParticipationIncoming.class).value();
        final ScenarioParticipationIncomingDefinition participationDefinition =
            (ScenarioParticipationIncomingDefinition) incomingMethod.invoke(connector);
        buildIncomingRoute(
            integrationScenarios.get(scenarioId), connector, participationDefinition);
      }

      for (final var outgoingMethod : outgoingParticipations) {
        final String scenarioId =
            outgoingMethod.getAnnotation(ScenarioParticipationOutgoing.class).value();
        final ScenarioParticipationOutgoingDefinition participationDefinition =
            (ScenarioParticipationOutgoingDefinition) outgoingMethod.invoke(connector);
        buildOutgoingRoute(
            integrationScenarios.get(scenarioId), connector, participationDefinition);
      }
    }
  }

  private void buildIncomingRoute(
      final IntegrationScenarioDefinition scenario,
      final ConnectorDefinition connector,
      final ScenarioParticipationIncomingDefinition definition) {

    final var routeId =
        String.format("sip-route-incoming-%s-%s", connector.getID(), scenario.getID());
    final var customDefStepId =
        String.format(
            "sip-incoming-step-connector-definition-%s-%s", connector.getID(), scenario.getID());
    final var fwPreparationStepId =
        String.format(
            "sip-incoming-step-framework-preparation-%s-%s", connector.getID(), scenario.getID());

    // Prepare route with standard SIP structure
    ProcessorDefinition<?> route =
        from(definition.getIncomingEndpointUri())
            .routeConfigurationId(DEFAULT_ROUTE_CONFIG_ID)
            .routeId(routeId)
            .step(fwPreparationStepId)
            .setProperty(
                DeclarativeExchangePropertyKeys.INTEGRATION_SCENARIO.getPropertyKey(),
                simple(scenario.getID()))
            .setProperty(
                DeclarativeExchangePropertyKeys.SOURCE_CONNECTOR.getPropertyKey(),
                simple(connector.getID()))
            .end();

    // Do participation-specific route config with hook
    route =
        route.log(
            String.format(
                "START: Connector-specified incoming route-definition for connector '%s', integration-scenario '%s'",
                connector.getID(), scenario.getID()));
    StepDefinition customStep = route.step(customDefStepId);
    definition.buildIncomingConnectorHook(customStep);
    route = customStep.end();
    route =
        route.log(
            String.format(
                "END: Connector-specified incoming route-definition for connector '%s', integration-scenario '%s'",
                connector.getID(), scenario.getID()));

    // Verify correct model
    route.log("Insert validation for common domain model here...");

    // Pass to MC
    route.to(String.format("seda:%s", scenario.getID()));
  }

  private void buildOutgoingRoute(
      final IntegrationScenarioDefinition scenario,
      final ConnectorDefinition connector,
      final ScenarioParticipationOutgoingDefinition definition) {

    final var routeId =
        String.format("sip-route-outgoing-%s-%s", connector.getID(), scenario.getID());
    final var customDefStepId =
        String.format(
            "sip-outgoing-step-connector-definition-%s-%s", connector.getID(), scenario.getID());

    // Prepare route with standard SIP structure
    ProcessorDefinition<?> route =
        from(String.format("seda:%s?multipleConsumers=true", scenario.getID()))
            .routeConfigurationId(DEFAULT_ROUTE_CONFIG_ID)
            .routeId(routeId);

    // Stop block to quit flow if data entered from the same processor
    route =
        route
            .choice()
            .when(
                exchange ->
                    exchange
                        .getProperty(
                            DeclarativeExchangePropertyKeys.SOURCE_CONNECTOR.getPropertyKey())
                        .equals(connector.getID()))
            .log(
                String.format(
                    "STOP Flow for outgoing-participation of connector '%s': message originated from same connector",
                    connector.getID()))
            .otherwise();

    // Initiate custom hook
    route =
        route.log(
            String.format(
                "START: Connector-specified outgoing route-definition for connector '%s', integration-scenario '%s'",
                connector.getID(), scenario.getID()));
    StepDefinition step = route.step(customDefStepId);
    definition.buildOutgoingConnectorHook(step);
    route =
        step.end()
            .log(
                String.format(
                    "END: Connector-specified outgoing route-definition for connector '%s', integration-scenario '%s'",
                    connector.getID(), scenario.getID()));

    // Send to external system
    route.to(definition.getOutgoingEndpointUri()).end();
  }

  @Override
  public void configuration() throws Exception {
    routeConfiguration(DEFAULT_ROUTE_CONFIG_ID)
        .onException(Exception.class)
        .log(
            LoggingLevel.ERROR,
            "An unrecoverable error occured on integration scenario '${exchangeProperty.SipIntegrationScenario}', incoming connector '${exchangeProperty.SipCallerSourceConnector}'");
  }
}
