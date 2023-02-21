package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;

@Accessors(chain = true)
abstract class BaseMappingRouteTransformer<C, S> implements Consumer<RouteDefinition> {

  @Getter(AccessLevel.PROTECTED)
  private final Supplier<ConnectorDefinition> connector;

  @Getter(AccessLevel.PROTECTED)
  private final Supplier<IntegrationScenarioDefinition> scenario;

  @Setter private Optional<ModelMapper<C, S>> mapper = Optional.empty();

  protected BaseMappingRouteTransformer(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    this.connector = connector;
    this.scenario = scenario;
  }

  protected abstract Class<?> getSourceModelClass();

  protected abstract Class<?> getTargetModelClass();

  protected abstract void fillInConversionDefinitions(RouteDefinition routeDefinition);

  @Override
  public final void accept(final RouteDefinition routeDefinition) {
    buildTransformerRoute(routeDefinition);
  }

  private void buildTransformerRoute(final RouteDefinition routeDefinition) {
    fillInConversionDefinitions(routeDefinition);
  }

  protected Optional<ModelMapper<C, S>> retrieveUsableMapper(
      final CamelContext context, final Class<C> connectorModel, final Class<S> scenarioModel) {
    if (mapper.isPresent()) {
      return mapper;
    }
    return context
        .getRegistry()
        .findSingleByType(DeclarationsRegistry.class)
        .getModelMapperForModels(connectorModel, scenarioModel);
  }

  protected <T> T forceThrowUnknownConnectorTypeException() {
    throw new IllegalStateException(
        String.format(
            "Unknown connector type '%s' declared in connector %s",
            connector.get().getConnectorType(), connector.get().getId()));
  }

  protected IllegalStateException getExceptionForNoMissingMapper() {
    return new IllegalStateException(
        String.format(
            "No usable mapper found for connector '%s' to map between %s and %s",
            connector.get().getId(),
            getSourceModelClass().getName(),
            getTargetModelClass().getName()));
  }
}
