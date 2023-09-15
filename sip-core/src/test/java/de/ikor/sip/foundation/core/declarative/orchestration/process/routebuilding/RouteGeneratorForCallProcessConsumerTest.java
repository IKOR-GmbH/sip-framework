package de.ikor.sip.foundation.core.declarative.orchestration.process.routebuilding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.CallProcessConsumer;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.DSLTestHelper;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.camel.model.RoutesDefinition;
import org.junit.jupiter.api.Test;

class RouteGeneratorForCallProcessConsumerTest {

  @Test
  void init() throws NoSuchMethodException, IllegalAccessException {
    // arrange
    CompositeProcessOrchestrationInfo orchestrationInfo =
        mock(CompositeProcessOrchestrationInfo.class, RETURNS_DEEP_STUBS);
    RouteGeneratorForCallProcessConsumer generator =
        new RouteGeneratorForCallProcessConsumer(orchestrationInfo, null, null);
    Method initSomethingMethod =
        RouteGeneratorForCallProcessConsumer.class.getDeclaredMethod(
            "retrieveConsumerFromClassDefinition", CallProcessConsumer.class);
    initSomethingMethod.setAccessible(true);
    RoutesDefinition routesDefinition = mock(RoutesDefinition.class, RETURNS_DEEP_STUBS);
    when(orchestrationInfo.getRoutesDefinition()).thenReturn(routesDefinition);
    when(routesDefinition
            .getCamelContext()
            .getRegistry()
            .findSingleByType(DeclarationsRegistryApi.class))
        .thenReturn(mock(DeclarationsRegistry.class));
    CallProcessConsumer callProcessConsumer = DSLTestHelper.initCallProcessConsumer();

    try {
      // act
      initSomethingMethod.invoke(generator, callProcessConsumer);
    } catch (InvocationTargetException e) {
      // assert
      assertThat(e.getTargetException()).isInstanceOf(SIPFrameworkInitializationException.class);
    }
  }
}
