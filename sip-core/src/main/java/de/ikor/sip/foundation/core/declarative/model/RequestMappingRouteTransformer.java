package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.function.Supplier;

public final class RequestMappingRouteTransformer<S, T> extends BaseMappingRouteTransformer<S, T> {

  private final Class<?> connectorRequestModel = getConnector().get().getRequestModelClass();
  private final Class<?> scenarioRequestModel = getScenario().get().getRequestModelClass();

  protected RequestMappingRouteTransformer(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    super(connector, scenario);
  }

  public static <S, T> RequestMappingRouteTransformer<S, T> forConnectorWithScenario(
      final ConnectorDefinition connector, final Supplier<IntegrationScenarioDefinition> scenario) {
    return forConnectorWithScenario(() -> connector, scenario);
  }

  public static <S, T> RequestMappingRouteTransformer<S, T> forConnectorWithScenario(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    return new RequestMappingRouteTransformer<>(connector, scenario);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Class<S> getSourceModelClass() {
    return switch (getConnector().get().getConnectorType()) {
      case IN -> (Class<S>) connectorRequestModel;
      case OUT -> (Class<S>) scenarioRequestModel;
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Class<T> getTargetModelClass() {
    return switch (getConnector().get().getConnectorType()) {
      case IN -> (Class<T>) scenarioRequestModel;
      case OUT -> (Class<T>) connectorRequestModel;
    };
  }
}
