package de.ikor.sip.foundation.core.declarative.orchestration;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.model.MulticastDefinition;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Accessors(chain = true)
public class ScenarioOrchestrator implements Orchestrator<ScenarioOrchestrationInfo> {
  private final Supplier<IntegrationScenarioDefinition> relatedIntegrationScenario;
  private Function<Map<String, Object>, Object> scenarioResponseAggregation =
      this::defaultScenarioResponseAggregation;

  public static ScenarioOrchestrator forScenario(
      final IntegrationScenarioDefinition relatedIntegrationScenario) {
    return new ScenarioOrchestrator(() -> relatedIntegrationScenario);
  }

  private Object defaultScenarioResponseAggregation(Map<String, Object> values) {
    return values.entrySet().stream().findFirst().get().getValue();
  }

  @Override
  public boolean canOrchestrate(final ScenarioOrchestrationInfo data) {
    return true;
  }

  @Override
  public void doOrchestrate(final ScenarioOrchestrationInfo data) {
    data.getInboundConnectorRouteEnds()
        .forEach(
            inboundConnectorRoute -> {
              MulticastDefinition scenarioRoute = inboundConnectorRoute.multicast();
              data.getOutboundConnectorsStarts()
                  .forEach(scenarioRoute::to);
              scenarioRoute.aggregationStrategy(AggregationStrategies.groupedExchange());
              scenarioRoute
                  .end()
                  .setBody(
                      exchange -> {
                        List<Exchange> exchanges = exchange.getMessage().getBody(List.class);
                        return exchanges.stream()
                            .collect(
                                Collectors.toMap(
                                    e -> e.getProperty("sip-connector-id", String.class),
                                    e ->
                                        e.getMessage()
                                            .getBody(
                                                // relatedIntegrationScenario
                                                //  .get()
                                                //  .getResponseModelClass().orElse(Object.class)
                                                // this will actually
                                                // force the response and it will not fail CDM
                                                // Validation
                                                )));
                      })
                  .setBody(
                      e ->
                          scenarioResponseAggregation.apply(
                              (Map<String, Object>) e.getMessage().getBody()));
            });
  }
}
