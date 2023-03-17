package de.ikor.sip.foundation.core.declarative.orchestration;

import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.scenariodsl.ScenarioOrderDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
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
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.MulticastDefinition;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Accessors(chain = true)
public class ScenarioOrchestrator implements Orchestrator<ScenarioOrchestrationInfo> {
  private final Supplier<IntegrationScenarioDefinition> relatedIntegrationScenario;

  private ScenarioOrderDefinition scenarioOrderDefinition =
      ScenarioOrderDefinition.builder()
          .fromAll(true)
          .orderedConectors(new ArrayList<>())
          .method(this::defaultScenarioResponseAggregation)
          .build();

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
            (key, value) -> {
              MulticastDefinition scenarioRoute = value.multicast();

              Map<OutboundConnectorDefinition, EndpointProducerBuilder> outbounds =
                  data.getOutboundConnectorsStarts();
              Map<OutboundConnectorDefinition, EndpointProducerBuilder> outboundsSorted =
                  outbounds.entrySet().stream()
                      .sorted(
                          Comparator.comparing(
                              item ->
                                  scenarioOrderDefinition
                                      .getOrderedConectors()
                                      .indexOf(item.getKey().getId())))
                      .collect(
                          Collectors.toMap(
                              Map.Entry::getKey,
                              Map.Entry::getValue,
                              (e1, e2) -> e1,
                              LinkedHashMap::new));
              outboundsSorted.values().forEach(scenarioRoute::to);
              // data.getOutboundConnectorsStarts().values().forEach(scenarioRoute::to);

              scenarioRoute.aggregationStrategy(AggregationStrategies.groupedExchange());
              scenarioRoute
                  .end()
                  .process(
                      e -> {
                        System.out.println(e);
                      })
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
