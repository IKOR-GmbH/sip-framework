package de.ikor.sip.foundation.core.declarative;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import de.ikor.sip.foundation.core.declarative.annonation.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.GenericInboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connector.GenericOutboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.model.ModelMapper;
import de.ikor.sip.foundation.core.declarative.model.RequestMappingRouteTransformer;
import de.ikor.sip.foundation.core.declarative.model.ResponseMappingRouteTransformer;
import de.ikor.sip.foundation.core.declarative.orchestration.connector.ConnectorOrchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioBase;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

class DeclarationsRegistryTest {

  private static final String INBOUND_CONNECTOR_ID = "inboundConnectorId";
  private static final String OUTBOUND_CONNECTOR_ID = "outboundConnectorId";
  private static final String SCENARIO_ID = "scenarioId";
  private static final String CONNECTOR_GROUP_ID = "connectorGroupId";
  private static final String SECOND_SCENARIO_ID = "secondScenarioId";

  private DeclarationsRegistry subject;

  private final List<ConnectorGroupDefinition> connectorGroups = new ArrayList<>();
  private final List<IntegrationScenarioDefinition> scenarios = new ArrayList<>();
  private final List<ConnectorDefinition> connectors = new ArrayList<>();
  private final List<ModelMapper<?, ?>> modelMappers = new ArrayList<>();

  public static class ScenarioMock extends IntegrationScenarioBase {}

  public static class InboundConnectorMock extends GenericInboundConnectorBase {
    @Override
    protected EndpointConsumerBuilder defineInitiatingEndpoint() {
      return null;
    }
  }

  public static class OutboundConnectorMock extends GenericOutboundConnectorBase {
    @Override
    protected EndpointProducerBuilder defineOutgoingEndpoint() {
      return null;
    }
  }

  @BeforeEach
  void setup() {}

  @Test
  void WHEN_checkForUnusedScenarios_THEN_expectSIPFrameworkInitializationException() {
    // arrange
    ScenarioMock firstScenario = mock(ScenarioMock.class);
    when(firstScenario.getId()).thenReturn(SCENARIO_ID);
    scenarios.add(firstScenario);

    // act & assert
    assertThatThrownBy(
            () -> {
              subject =
                  new DeclarationsRegistry(
                      connectorGroups,
                      scenarios,
                      connectors,
                      modelMappers,
                      mock(ApplicationContext.class));
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage("There is unused integration scenario with id %s", SCENARIO_ID);
  }

  @Test
  void WHEN_checkForDuplicateScenarios_THEN_expectSIPFrameworkInitializationException() {
    // arrange
    IntegrationScenarioDefinition firstScenario = mock(ScenarioMock.class);
    IntegrationScenarioDefinition secondScenario = mock(ScenarioMock.class);
    when(firstScenario.getId()).thenReturn(SCENARIO_ID);
    when(secondScenario.getId()).thenReturn(SCENARIO_ID);
    scenarios.add(firstScenario);
    scenarios.add(secondScenario);

    // act & assert
    assertThatThrownBy(
            () -> {
              subject =
                  new DeclarationsRegistry(
                      connectorGroups,
                      scenarios,
                      connectors,
                      modelMappers,
                      mock(ApplicationContext.class));
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessageContaining(
            "There is a duplicate %s id %s in class %s",
            "integration scenario", SCENARIO_ID, secondScenario.getClass().getName());
  }

  @Test
  void WHEN_getConnectorById_THEN_expectValidConnector() {
    // arrange
    InboundConnectorMock connector = mock(InboundConnectorMock.class);
    when(connector.getId()).thenReturn(INBOUND_CONNECTOR_ID);
    connectors.add(connector);
    subject =
        new DeclarationsRegistry(
            connectorGroups, scenarios, connectors, modelMappers, mock(ApplicationContext.class));

    // act
    Optional<ConnectorDefinition> actual = subject.getConnectorById(INBOUND_CONNECTOR_ID);

    // assert
    assertThat(actual).isPresent();
    assertThat(actual.get().getId()).isEqualTo(INBOUND_CONNECTOR_ID);
  }

  @Test
  void WHEN_getScenarioByIdFindsNoScenario_THEN_expectSIPFrameworkInitializationException() {
    // arrange
    IntegrationScenarioDefinition firstScenario = mock(ScenarioMock.class);
    when(firstScenario.getId()).thenReturn(SCENARIO_ID);
    scenarios.add(firstScenario);

    InboundConnectorMock inboundConnector = mock(InboundConnectorMock.class);
    when(inboundConnector.getId()).thenReturn(INBOUND_CONNECTOR_ID);
    when(inboundConnector.getScenarioId()).thenReturn(SCENARIO_ID);
    when(inboundConnector.getConnectorGroupId()).thenReturn(CONNECTOR_GROUP_ID);
    connectors.add(inboundConnector);

    OutboundConnectorMock outboundConnector = mock(OutboundConnectorMock.class);
    when(outboundConnector.getId()).thenReturn(OUTBOUND_CONNECTOR_ID);
    when(outboundConnector.getScenarioId()).thenReturn(SCENARIO_ID);
    when(outboundConnector.getConnectorGroupId()).thenReturn(CONNECTOR_GROUP_ID);
    connectors.add(outboundConnector);

    subject =
        new DeclarationsRegistry(
            connectorGroups, scenarios, connectors, modelMappers, mock(ApplicationContext.class));

    // act & assert
    assertThatThrownBy(() -> subject.getScenarioById(SECOND_SCENARIO_ID))
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage("There is no integration scenario with id: %s", SECOND_SCENARIO_ID);
  }

  @Test
  void no_annotation() {
    ApplicationContext applicationContext = mock(ApplicationContext.class);
    when(applicationContext.getBeansWithAnnotation(InboundConnector.class))
        .thenReturn(Map.of("key", new Object()));
    assertThatThrownBy(
            () -> {
              subject =
                  new DeclarationsRegistry(
                      connectorGroups, scenarios, connectors, modelMappers, applicationContext);
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            "Annotated InboundConnector java.lang.Object is missing InboundConnectorBase parent class.");
  }

  @Test
  void no_annotationout() {
    ApplicationContext applicationContext = mock(ApplicationContext.class);
    when(applicationContext.getBeansWithAnnotation(OutboundConnector.class))
        .thenReturn(Map.of("key", new Object()));
    assertThatThrownBy(
            () -> {
              subject =
                  new DeclarationsRegistry(
                      connectorGroups, scenarios, connectors, modelMappers, applicationContext);
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            "Annotated OutboundConnector java.lang.Object is missing OutboundConnectorDefinition parent class.");
  }

  @Test
  void
      When_CheckControllerMapping_With_OverriddenRequestMapping_Then_SIPFrameworkInitializationExceptionThrown() {
    // arrange
    InboundConnectorMock connector = mock(InboundConnectorMock.class);
    final var transformer =
        RequestMappingRouteTransformer.forConnectorWithScenario(
            connector,
            () -> {
              return null;
            });
    RequestMappingRouteTransformer<Object, Object> routeTransformer =
        RequestMappingRouteTransformer.forConnectorWithScenario(
            connector,
            () -> {
              return null;
            });
    Optional<RequestMappingRouteTransformer<Object, Object>> mapper = Optional.of(transformer);
    ConnectorOrchestrator connectorOrchestrator = mock(ConnectorOrchestrator.class);
    when(connector.getId()).thenReturn("mockConnector");
    when(connectorOrchestrator.getRequestRouteTransformer()).thenReturn(routeTransformer);
    when(connector.getRequestMapper()).thenReturn(mapper);
    when(connector.getOrchestrator()).thenReturn(connectorOrchestrator);
    connectors.add(connector);

    // assert
    assertThatThrownBy(
            () ->
                subject =
                    new DeclarationsRegistry(connectorGroups, scenarios, connectors, modelMappers))
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage("Request mapping in connector 'mockConnector' is defined, but overridden");
  }

  @Test
  void When_CheckingConnectorMapping_With_NoMappingsOrTransformation_Then_NoExceptionThrown() {
    // arrange
    InboundConnectorMock connector = mock(InboundConnectorMock.class);
    connectors.add(connector);

    // assert
    assertDoesNotThrow(
        () -> {
          subject = new DeclarationsRegistry(connectorGroups, scenarios, connectors, modelMappers);
        });
  }

  @Test
  void When_CheckingConnectorMapping_With_MappingsAndNoTransformation_Then_NoExceptionThrown() {
    // arrange
    InboundConnectorMock connector = mock(InboundConnectorMock.class);
    RequestMappingRouteTransformer<Object, Object> routeTransformer =
        RequestMappingRouteTransformer.forConnectorWithScenario(
            connector,
            () -> {
              return null;
            });
    Optional<RequestMappingRouteTransformer<Object, Object>> mapper = Optional.of(routeTransformer);
    ConnectorOrchestrator connectorOrchestrator = mock(ConnectorOrchestrator.class);
    when(connector.getId()).thenReturn("mockConnector");
    when(connectorOrchestrator.getRequestRouteTransformer()).thenReturn(routeTransformer);
    when(connector.getRequestMapper()).thenReturn(mapper);
    when(connector.getOrchestrator()).thenReturn(connectorOrchestrator);
    connectors.add(connector);

    // assert
    assertDoesNotThrow(
        () -> {
          subject = new DeclarationsRegistry(connectorGroups, scenarios, connectors, modelMappers);
        });
  }

  @Test
  void
      When_CheckControllerMapping_With_OverriddenResponseMapping_Then_SIPFrameworkInitializationExceptionThrown() {
    // arrange
    OutboundConnectorMock connector = mock(OutboundConnectorMock.class);
    final var transformer =
        ResponseMappingRouteTransformer.forConnectorWithScenario(
            connector,
            () -> {
              return null;
            });
    ResponseMappingRouteTransformer<Object, Object> routeTransformer =
        ResponseMappingRouteTransformer.forConnectorWithScenario(
            connector,
            () -> {
              return null;
            });
    Optional<ResponseMappingRouteTransformer<Object, Object>> mapper = Optional.of(transformer);
    ConnectorOrchestrator connectorOrchestrator = mock(ConnectorOrchestrator.class);
    when(connector.getId()).thenReturn("mockConnector");
    when(connectorOrchestrator.getResponseRouteTransformer()).thenReturn(routeTransformer);
    when(connector.getResponseMapper()).thenReturn(mapper);
    when(connector.getOrchestrator()).thenReturn(connectorOrchestrator);
    connectors.add(connector);

    // assert
    assertThatThrownBy(
            () ->
                subject =
                    new DeclarationsRegistry(connectorGroups, scenarios, connectors, modelMappers))
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage("Response mapping in connector 'mockConnector' is defined, but overridden");
  }
}
