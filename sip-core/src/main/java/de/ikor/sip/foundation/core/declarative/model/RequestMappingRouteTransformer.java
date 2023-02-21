package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.function.Supplier;
import org.apache.camel.model.RouteDefinition;

public class RequestMappingRouteTransformer<C, S> extends BaseMappingRouteTransformer<C, S> {

  private final Class<C> connectorRequestModel =
      (Class<C>) getConnector().get().getRequestModelClass();
  private final Class<S> scenarioRequestModel =
      (Class<S>) getScenario().get().getRequestModelClass();

  protected RequestMappingRouteTransformer(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    super(connector, scenario);
  }

  public static <C, S> RequestMappingRouteTransformer<C, S> forConnectorWithScenario(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    return new RequestMappingRouteTransformer<>(connector, scenario);
  }

  public static <C, S> RequestMappingRouteTransformer<C, S> forConnectorWithScenario(
      final ConnectorDefinition connector, final Supplier<IntegrationScenarioDefinition> scenario) {
    return forConnectorWithScenario(() -> connector, scenario);
  }

  @Override
  protected Class<?> getSourceModelClass() {
    switch (getConnector().get().getConnectorType()) {
      case IN:
        return connectorRequestModel;
      case OUT:
        return scenarioRequestModel;
    }
    return forceThrowUnknownConnectorTypeException();
  }

  @Override
  protected Class<?> getTargetModelClass() {
    switch (getConnector().get().getConnectorType()) {
      case IN:
        return scenarioRequestModel;
      case OUT:
        return connectorRequestModel;
    }
    return forceThrowUnknownConnectorTypeException();
  }

  @Override
  protected void fillInConversionDefinitions(final RouteDefinition routeDefinition) {
    final var modelMapper =
        retrieveUsableMapper(
                routeDefinition.getCamelContext(), connectorRequestModel, scenarioRequestModel)
            .orElseThrow(this::getExceptionForNoMissingMapper);
    routeDefinition.inputType(getSourceModelClass());
    switch (getConnector().get().getConnectorType()) {
      case IN:
        routeDefinition
            .transform()
            .method(modelMapper, ModelMapper.CONNECTOR_TO_SCENARIO_METHOD_NAME);
        break;
      case OUT:
        routeDefinition
            .transform()
            .method(modelMapper, ModelMapper.SCENARIO_TO_CONNECTOR_METHOD_NAME);
        break;
      default:
        forceThrowUnknownConnectorTypeException();
    }
  }
}
