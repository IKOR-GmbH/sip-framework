package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.function.Supplier;
import org.apache.camel.model.RouteDefinition;

public class ResponseMappingRouteTransformer<C, S> extends BaseMappingRouteTransformer<C, S> {

  private final Class<C> connectorRequestModel =
      (Class<C>) getConnector().get().getResponseModelClass().orElseThrow();
  private final Class<S> scenarioRequestModel =
      (Class<S>) getScenario().get().getResponseModelClass().orElseThrow();

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

  public static <C, S> ResponseMappingRouteTransformer<C, S> forConnectorWithScenario(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    return new ResponseMappingRouteTransformer<>(connector, scenario);
  }

  public static <C, S> ResponseMappingRouteTransformer<C, S> forConnectorWithScenario(
      final ConnectorDefinition connector, final Supplier<IntegrationScenarioDefinition> scenario) {
    return forConnectorWithScenario(() -> connector, scenario);
  }

  @Override
  protected Class<?> getSourceModelClass() {
    switch (getConnector().get().getConnectorType()) {
      case IN:
        return scenarioRequestModel;
      case OUT:
        return connectorRequestModel;
    }
    return forceThrowUnknownConnectorTypeException();
  }

  @Override
  protected Class<?> getTargetModelClass() {
    switch (getConnector().get().getConnectorType()) {
      case IN:
        return connectorRequestModel;
      case OUT:
        return scenarioRequestModel;
    }
    return forceThrowUnknownConnectorTypeException();
  }

  @Override
  protected void fillInConversionDefinitions(final RouteDefinition routeDefinition) {
    final var modelMapper =
        retrieveUsableMapper(
                routeDefinition.getCamelContext(), connectorRequestModel, scenarioRequestModel)
            .orElseThrow(this::getExceptionForNoMissingMapper);
    routeDefinition.outputType(getSourceModelClass());
    switch (getConnector().get().getConnectorType()) {
      case IN:
        routeDefinition
            .transform()
            .method(modelMapper, ModelMapper.SCENARIO_TO_CONNECTOR_METHOD_NAME);
        break;
      case OUT:
        routeDefinition
            .transform()
            .method(modelMapper, ModelMapper.CONNECTOR_TO_SCENARIO_METHOD_NAME);
        break;
      default:
        forceThrowUnknownConnectorTypeException();
    }
  }
}
