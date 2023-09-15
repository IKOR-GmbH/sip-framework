package de.ikor.sip.foundation.core.declarative.orchestration.process.routebuilding;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessOrchestrationInfo;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import org.apache.camel.model.RoutesDefinition;
import org.junit.jupiter.api.Test;

class RouteGeneratorForProcessOrchestrationDefinitionTest {

  @Test
  void When_OrchestrationProviderMissing_Expect_SIPFrameworkInitializationException() {
    // arrange
    CompositeProcessOrchestrationInfo compositeProcessOrchestrationInfo =
        mock(CompositeProcessOrchestrationInfo.class, RETURNS_DEEP_STUBS);
    RouteGeneratorForProcessOrchestrationDefinition subject =
        new RouteGeneratorForProcessOrchestrationDefinition(
            compositeProcessOrchestrationInfo, null);
    RoutesDefinition routesDefinition = mock(RoutesDefinition.class, RETURNS_DEEP_STUBS);
    when(compositeProcessOrchestrationInfo.getRoutesDefinition()).thenReturn(routesDefinition);
    when(routesDefinition
            .getCamelContext()
            .getRegistry()
            .findSingleByType(DeclarationsRegistryApi.class))
        .thenReturn(mock(DeclarationsRegistry.class));
    when(compositeProcessOrchestrationInfo.getCompositeProcess().getId()).thenReturn("test");

    // act + assert
    assertThatThrownBy(
            () -> {
              subject.generateRoutes(routesDefinition);
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            "Orchestration for composite process 'test' doesn't have a provider. Please define it.");
  }
}
