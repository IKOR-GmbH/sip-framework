package de.ikor.sip.foundation.core.declarative.model;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorType;
import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.Optional;
import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BaseMappingRouteTransformerTest {

  static InboundConnectorDefinition inboundConnectorDefinition =
      mock(InboundConnectorDefinition.class);
  static OutboundConnectorDefinition outboundConnectorDefinition =
      mock(OutboundConnectorDefinition.class);
  static IntegrationScenarioDefinition integrationScenarioDefinition =
      mock(IntegrationScenarioDefinition.class);

  static RouteDefinition routeDefinition = new RouteDefinition();
  ResponseMappingRouteTransformer inboundResponseTransformerUnderTest =
      new ResponseMappingRouteTransformer<>(
          () -> inboundConnectorDefinition, () -> integrationScenarioDefinition);

  ResponseMappingRouteTransformer outboundResponseTransformerUnderTest =
      new ResponseMappingRouteTransformer<>(
          () -> outboundConnectorDefinition, () -> integrationScenarioDefinition);

  ModelMapper<Integer, Integer> integerModelMapper =
      new ModelMapper<>() {
        @Override
        public Class<Integer> getSourceModelClass() {
          return Integer.class;
        }

        @Override
        public Class<Integer> getTargetModelClass() {
          return Integer.class;
        }

        @Override
        public Integer mapToTargetModel(Integer sourceModel) {
          return sourceModel;
        }
      };

  @BeforeAll
  static void setUp() {

    Mockito.<Class<?>>when(integrationScenarioDefinition.getRequestModelClass())
        .thenReturn(Integer.class);
    Mockito.when(integrationScenarioDefinition.getResponseModelClass())
        .thenReturn(Optional.of(String.class));

    when(inboundConnectorDefinition.getId()).thenReturn("inbound-connector");
    when(inboundConnectorDefinition.getConnectorType()).thenReturn(ConnectorType.IN);
    Mockito.<Class<?>>when(inboundConnectorDefinition.getRequestModelClass())
        .thenReturn(String.class);
    Mockito.when(inboundConnectorDefinition.getResponseModelClass())
        .thenReturn(Optional.of(Integer.class));

    when(outboundConnectorDefinition.getId()).thenReturn("outbound-connector");
    when(outboundConnectorDefinition.getConnectorType()).thenReturn(ConnectorType.OUT);
    Mockito.<Class<?>>when(outboundConnectorDefinition.getRequestModelClass())
        .thenReturn(Integer.class);
    Mockito.when(outboundConnectorDefinition.getResponseModelClass())
        .thenReturn(Optional.of(Integer.class));

    // mock non-existing global mapper
    CamelContext camelContext = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    routeDefinition.setCamelContext(camelContext);
    DeclarationsRegistry declarationsRegistry = mock(DeclarationsRegistry.class);
    when(camelContext.getRegistry().findSingleByType(DeclarationsRegistry.class))
        .thenReturn(declarationsRegistry);
    when(declarationsRegistry.getGlobalModelMapperForModels(any(), any()))
        .thenReturn(Optional.empty());
  }

  @Test
  void WHEN_incompatibleSourceMappersUsed_THEN_SIPExceptionIsThrown() {
    // arrange
    inboundResponseTransformerUnderTest.setMapper(Optional.of(integerModelMapper));

    // act&assert
    assertThatThrownBy(
            () -> {
              inboundResponseTransformerUnderTest.accept(routeDefinition);
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            "Mapper '%s' %s type '%s' is not compatible with assigned type '%s' of connector '%s'",
            integerModelMapper.getClass().getName(),
            "source",
            integerModelMapper.getSourceModelClass().getName(),
            inboundResponseTransformerUnderTest.getSourceModelClass().getName(),
            inboundConnectorDefinition.getId());
  }

  @Test
  void WHEN_incompatibleTargetMappersUsed_THEN_SIPExceptionIsThrown() {
    // arrange
    outboundResponseTransformerUnderTest.setMapper(Optional.of(integerModelMapper));

    // act&assert
    assertThatThrownBy(
            () -> {
              outboundResponseTransformerUnderTest.accept(routeDefinition);
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            "Mapper '%s' %s type '%s' is not compatible with assigned type '%s' of connector '%s'",
            integerModelMapper.getClass().getName(),
            "target",
            integerModelMapper.getTargetModelClass().getName(),
            outboundResponseTransformerUnderTest.getTargetModelClass().getName(),
            outboundConnectorDefinition.getId());
  }

  @Test
  void WHEN_noCompatibleMapperFound_THEN_SIPExceptionIsThrown() {
    // arrange
    inboundResponseTransformerUnderTest.setMapper(Optional.empty());

    // act&assert
    assertThatThrownBy(
            () -> {
              inboundResponseTransformerUnderTest.accept(routeDefinition);
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            "No compatible Mapper found for Connector '%s' to map between %s and %s",
            inboundConnectorDefinition.getId(),
            inboundResponseTransformerUnderTest.getSourceModelClass().getName(),
            inboundResponseTransformerUnderTest.getTargetModelClass().getName());
  }
}
