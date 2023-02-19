package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.annonation.GlobalMapper;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.spi.TypeConverterRegistry;

@Data
@Accessors(chain = true)
abstract class BaseMappingRouteTransformer implements Consumer<RouteDefinition> {

  @Getter(AccessLevel.PROTECTED)
  private final Supplier<ConnectorDefinition> connector;

  @Getter(AccessLevel.PROTECTED)
  private final Supplier<IntegrationScenarioDefinition> scenario;

  private Optional<ModelMapper> mapper = Optional.empty();

  private Optional<DataFormat> dataFormat = Optional.empty();

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
    prepareAndVerifyConverter(routeDefinition.getCamelContext().getTypeConverterRegistry());
    fillInConversionDefinitions(routeDefinition);
  }

  protected <T> T forceThrowUnknownConnectorTypeException() {
    throw new IllegalStateException(
        String.format(
            "Unknown connector type '%s' declared in connector %s",
            connector.get().getConnectorType(), connector.get().getId()));
  }

  private void prepareAndVerifyConverter(final TypeConverterRegistry converterRegistry) {
    mapper.ifPresent(
        mapper -> ModelMapperHelper.registerMapperAsTypeConverters(mapper, converterRegistry));
    if (null == converterRegistry.lookup(getTargetModelClass(), getSourceModelClass())) {
      throw new SIPFrameworkException(
          String.format(
              "No ModelMapper found for connector '%s' that can map from %s to %s. Register a global mapper for these types with using the @%s annotation, or provide a specific mapper for this connector",
              connector.get().getId(),
              getSourceModelClass().getName(),
              getTargetModelClass().getName(),
              GlobalMapper.class.getSimpleName()));
    }
  }
}
