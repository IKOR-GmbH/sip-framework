package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.function.Supplier;

public final class ResponseMappingRouteTransformer<S, T> extends BaseMappingRouteTransformer<S, T> {

  private final Class<?> connectorRequestModel =
      getConnector().get().getResponseModelClass().orElseThrow();
  private final Class<?> scenarioRequestModel =
      getScenario().get().getResponseModelClass().orElseThrow();

  protected ResponseMappingRouteTransformer(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    super(connector, scenario);
    if (!connector.get().hasResponseFlow()) {
      throw new IllegalStateException(
          String.format(
              "Can't assign response mapping transformer to connector '%s', as it does not have a response flow",
              getConnector().get().getId()));
    }
  }

  public static <S, T> ResponseMappingRouteTransformer<S, T> forConnectorWithScenario(
      final ConnectorDefinition connector, final Supplier<IntegrationScenarioDefinition> scenario) {
    return forConnectorWithScenario(() -> connector, scenario);
  }

  public static <S, T> ResponseMappingRouteTransformer<S, T> forConnectorWithScenario(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    return new ResponseMappingRouteTransformer<>(connector, scenario);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Class<S> getSourceModelClass() {
    return switch (getConnector().get().getConnectorType()) {
      case IN -> (Class<S>) scenarioRequestModel;
      case OUT -> (Class<S>) connectorRequestModel;
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Class<T> getTargetModelClass() {
    return switch (getConnector().get().getConnectorType()) {
      case IN -> (Class<T>) connectorRequestModel;
      case OUT -> (Class<T>) scenarioRequestModel;
    };
  }
}
