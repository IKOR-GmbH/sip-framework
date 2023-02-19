package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.function.Supplier;
import org.apache.camel.model.RouteDefinition;

public class RequestMappingRouteTransformer extends BaseMappingRouteTransformer {

  private final Class<?> connectorRequestModel = getConnector().get().getRequestModelClass();
  private final Class<?> scenarioRequestModel = getScenario().get().getRequestModelClass();

  protected RequestMappingRouteTransformer(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    super(connector, scenario);
  }

  public static RequestMappingRouteTransformer forConnectorWithScenario(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    return new RequestMappingRouteTransformer(connector, scenario);
  }

  public static RequestMappingRouteTransformer forConnectorWithScenario(
      final ConnectorDefinition connector, final Supplier<IntegrationScenarioDefinition> scenario) {
    return new RequestMappingRouteTransformer(() -> connector, scenario);
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
    switch (getConnector().get().getConnectorType()) {
      case IN:
        getDataFormat().ifPresent(routeDefinition::unmarshal);
        routeDefinition.convertBodyTo(getTargetModelClass());
        break;
      case OUT:
        routeDefinition.convertBodyTo(getTargetModelClass());
        getDataFormat().ifPresent(routeDefinition::marshal);
        break;
      default:
        forceThrowUnknownConnectorTypeException();
    }
  }
}
