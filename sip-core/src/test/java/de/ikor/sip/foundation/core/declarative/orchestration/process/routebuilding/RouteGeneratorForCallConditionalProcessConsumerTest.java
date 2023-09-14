package de.ikor.sip.foundation.core.declarative.orchestration.process.routebuilding;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.CallNestedCondition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.HashSet;
import org.apache.camel.model.RouteDefinition;
import org.junit.jupiter.api.Test;

public class RouteGeneratorForCallConditionalProcessConsumerTest {

  @Test
  void when_NoStatements_expect_InitException() {
    CompositeProcessOrchestrationInfo compositeProcessOrchestrationInfo =
        mock(CompositeProcessOrchestrationInfo.class, RETURNS_DEEP_STUBS);
    CallNestedCondition callNestedCondition = mock(CallNestedCondition.class);
    when(compositeProcessOrchestrationInfo.getCompositeProcess().getId())
        .thenReturn("ID of process");
    assertThatThrownBy(
            () ->
                new RouteGeneratorForCallConditionalProcessConsumer(
                        compositeProcessOrchestrationInfo, callNestedCondition, new HashSet<>())
                    .generateRoute(mock(RouteDefinition.class)))
        .isInstanceOf(SIPFrameworkInitializationException.class);
  }
}
