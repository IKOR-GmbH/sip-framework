package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
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
abstract sealed class BaseMappingRouteTransformer<S, T> implements Consumer<RouteDefinition>
    permits RequestMappingRouteTransformer, ResponseMappingRouteTransformer {

  @Getter(AccessLevel.PROTECTED)
  private final Supplier<ConnectorDefinition> connector;

  @Getter(AccessLevel.PROTECTED)
  private final Supplier<IntegrationScenarioDefinition> scenario;

  @Setter private Optional<ModelMapper<S, T>> mapper = Optional.empty();

  protected BaseMappingRouteTransformer(
      final Supplier<ConnectorDefinition> connector,
      final Supplier<IntegrationScenarioDefinition> scenario) {
    this.connector = connector;
    this.scenario = scenario;
  }

  @Override
  public final void accept(final RouteDefinition routeDefinition) {
    buildTransformerRoute(routeDefinition);
  }

  private void buildTransformerRoute(final RouteDefinition routeDefinition) {
    final var modelMapper =
        retrieveUsableMapper(
                routeDefinition.getCamelContext(), getSourceModelClass(), getTargetModelClass())
            .orElseThrow(this::getExceptionForMissingMapper);

    if (notCompatibleTypes(modelMapper.getSourceModelClass(), getSourceModelClass()))
      throw newExceptionForIncompatibleTypes(
          modelMapper, "source", modelMapper.getSourceModelClass(), getSourceModelClass());
    if (notCompatibleTypes(modelMapper.getTargetModelClass(), getTargetModelClass()))
      throw newExceptionForIncompatibleTypes(
          modelMapper, "target", modelMapper.getTargetModelClass(), getTargetModelClass());

    routeDefinition.transform().method(modelMapper, ModelMapper.MAPPING_METHOD_NAME);
  }

  protected Optional<ModelMapper<S, T>> retrieveUsableMapper(
      final CamelContext context, final Class<S> sourceModel, final Class<T> targetModel) {
    if (mapper.isPresent()) {
      return mapper;
    }
    return context
        .getRegistry()
        .findSingleByType(DeclarationsRegistry.class)
        .getGlobalModelMapperForModels(sourceModel, targetModel);
  }

  protected abstract Class<S> getSourceModelClass();

  protected abstract Class<T> getTargetModelClass();

  private boolean notCompatibleTypes(final Class<?> mapperType, final Class<?> assignedType) {
    return !mapperType.isAssignableFrom(assignedType);
  }

  private SIPFrameworkInitializationException newExceptionForIncompatibleTypes(
      ModelMapper<?, ?> modelMapper,
      String direction,
      Class<?> mapperType,
      final Class<?> assignedType) {
    return SIPFrameworkInitializationException.initException(
        "Mapper '%s' %s type '%s' is not compatible with assigned type '%s' of connector '%s'",
        modelMapper.getClass().getName(),
        direction,
        mapperType.getName(),
        assignedType.getName(),
        connector.get().getId());
  }

  protected SIPFrameworkInitializationException getExceptionForMissingMapper() {
    return SIPFrameworkInitializationException.initException(
        "No compatible Mapper found for Connector '%s' to map between %s and %s",
        connector.get().getId(), getSourceModelClass().getName(), getTargetModelClass().getName());
  }
}
