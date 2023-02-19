package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.function.Supplier;
import org.apache.camel.model.RouteDefinition;

public class ResponseMappingRouteTransformer extends BaseMappingRouteTransformer {

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

  public static ResponseMappingRouteTransformer forConnectorWithScenario(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    return new ResponseMappingRouteTransformer(connector, scenario);
  }

  public static ResponseMappingRouteTransformer forConnectorWithScenario(
      final ConnectorDefinition connector, final Supplier<IntegrationScenarioDefinition> scenario) {
    return new ResponseMappingRouteTransformer(() -> connector, scenario);
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
    switch (getConnector().get().getConnectorType()) {
      case IN:
        routeDefinition.convertBodyTo(getTargetModelClass());
        getDataFormat().ifPresent(routeDefinition::marshal);
        break;
      case OUT:
        getDataFormat().ifPresent(routeDefinition::unmarshal);
        routeDefinition.convertBodyTo(getTargetModelClass());
        break;
      default:
        forceThrowUnknownConnectorTypeException();
    }
  }
}
