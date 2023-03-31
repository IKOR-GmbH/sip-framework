package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.annonation.UseRequestModelMapper;
import de.ikor.sip.foundation.core.declarative.annonation.UseResponseModelMapper;
import de.ikor.sip.foundation.core.declarative.model.FindAutomaticModelMapper;
import de.ikor.sip.foundation.core.declarative.model.RequestMappingRouteTransformer;
import de.ikor.sip.foundation.core.declarative.model.ResponseMappingRouteTransformer;
import de.ikor.sip.foundation.core.declarative.orchestration.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.ConnectorOrchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Base class for connector definitions.
 *
 * <p>This class provides a default implementation for the {@link Orchestrator} interface, and
 * allows subclasses to attach an {@link Orchestrator} for the transformation between connector and
 * common domain models through the {@link #defineTransformationOrchestrator()} method.
 */
abstract non-sealed class ConnectorBase
    implements ConnectorDefinition,
        Orchestrator<ConnectorOrchestrationInfo>,
        ApplicationContextAware {

  @Getter(AccessLevel.PROTECTED)
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Getter(AccessLevel.PROTECTED)
  private ApplicationContext applicationContext;

  @Delegate
  private final Orchestrator<ConnectorOrchestrationInfo> modelTransformationOrchestrator =
      defineTransformationOrchestrator();

  @Override
  public Orchestrator<ConnectorOrchestrationInfo> getOrchestrator() {
    return this;
  }

  /**
   * Defines the {@link Orchestrator} for the transformation between connector and common domain
   * models. It is typically meant to be overridden, as the base implementation returns a simple
   * {@link ConnectorOrchestrator} which does not contain any additional model transformation logic.
   * It is only suitable if the connectors and the common domain model share the same type.
   *
   * @return Orchestrator for the transformation between connector and common domain models.
   */
  protected Orchestrator<ConnectorOrchestrationInfo> defineTransformationOrchestrator() {
    final var orchestrator = ConnectorOrchestrator.forConnector(this);
    getRequestMapper().ifPresent(orchestrator::setRequestRouteTransformer);
    getResponseMapper().ifPresent(orchestrator::setResponseRouteTransformer);
    return orchestrator;
  }

  @SuppressWarnings("unchecked")
  private Optional<RequestMappingRouteTransformer<Object, Object>> getRequestMapper() {
    final var annotation =
        DeclarativeHelper.getAnnotationIfPresent(UseRequestModelMapper.class, this);
    if (annotation.isPresent()) {
      final var transformer =
          RequestMappingRouteTransformer.forConnectorWithScenario(this, getScenario());
      if (!FindAutomaticModelMapper.class.equals(annotation.get().value())) {
        transformer.setMapper(
            Optional.of(DeclarativeHelper.createMapperInstance(annotation.get().value())));
      }
      return Optional.of(transformer);
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  private Optional<ResponseMappingRouteTransformer<Object, Object>> getResponseMapper() {
    final var annotation =
        DeclarativeHelper.getAnnotationIfPresent(UseResponseModelMapper.class, this);
    if (annotation.isPresent()) {
      final var transformer =
          ResponseMappingRouteTransformer.forConnectorWithScenario(this, getScenario());
      if (!FindAutomaticModelMapper.class.equals(annotation.get().value())) {
        transformer.setMapper(
            Optional.of(DeclarativeHelper.createMapperInstance(annotation.get().value())));
      }
      return Optional.of(transformer);
    }
    return Optional.empty();
  }

  public final Supplier<IntegrationScenarioDefinition> getScenario() {
    return () ->
        applicationContext.getBean(DeclarationsRegistryApi.class).getScenarioById(getScenarioId());
  }

  @Override
  public final void setApplicationContext(final ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }
}
