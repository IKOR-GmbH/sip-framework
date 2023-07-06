package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.annonation.UseRequestModelMapper;
import de.ikor.sip.foundation.core.declarative.annonation.UseResponseModelMapper;
import de.ikor.sip.foundation.core.declarative.model.FindAutomaticModelMapper;
import de.ikor.sip.foundation.core.declarative.model.RequestMappingRouteTransformer;
import de.ikor.sip.foundation.core.declarative.model.ResponseMappingRouteTransformer;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.connector.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.connector.ConnectorOrchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base class for connector definitions.
 *
 * <p>This class provides a default implementation for the {@link Orchestrator} interface, and
 * allows subclasses to attach an {@link Orchestrator} for the transformation between connector and
 * common domain models through the {@link #defineTransformationOrchestrator()} method.
 */
public abstract non-sealed class ConnectorBase
    implements ConnectorDefinition, ApplicationContextAware {

  @Getter(AccessLevel.PROTECTED)
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Getter(AccessLevel.PROTECTED)
  private ApplicationContext applicationContext;

  @Delegate
  private final Orchestrator<ConnectorOrchestrationInfo> modelTransformationOrchestrator =
      defineTransformationOrchestrator();

  private Optional<RequestMappingRouteTransformer<Object, Object>> requestMapper;
  private Optional<ResponseMappingRouteTransformer<Object, Object>> responseMapper;

  @Override
  public Orchestrator<ConnectorOrchestrationInfo> getOrchestrator() {
    return modelTransformationOrchestrator;
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
  public Optional<RequestMappingRouteTransformer<Object, Object>> getRequestMapper() {
    if (requestMapper == null) {
      requestMapper = Optional.empty();
    }
    if (requestMapper.isPresent()) {
      return requestMapper;
    }
    final var annotation =
        DeclarativeHelper.getAnnotationIfPresent(UseRequestModelMapper.class, this);
    if (annotation.isPresent()) {
      final var transformer =
          RequestMappingRouteTransformer.forConnectorWithScenario(this, getScenario());
      if (!FindAutomaticModelMapper.class.equals(annotation.get().value())) {
        transformer.setMapper(
            Optional.of(DeclarativeHelper.createMapperInstance(annotation.get().value())));
      }
      requestMapper = Optional.of(transformer);
    }
    return requestMapper;
  }

  @SuppressWarnings("unchecked")
  public Optional<ResponseMappingRouteTransformer<Object, Object>> getResponseMapper() {
    if (responseMapper == null) {
      responseMapper = Optional.empty();
    }
    if (responseMapper.isPresent()) {
      return responseMapper;
    }
    final var annotation =
        DeclarativeHelper.getAnnotationIfPresent(UseResponseModelMapper.class, this);
    if (annotation.isPresent()) {
      final var transformer =
          ResponseMappingRouteTransformer.forConnectorWithScenario(this, getScenario());
      if (!FindAutomaticModelMapper.class.equals(annotation.get().value())) {
        transformer.setMapper(
            Optional.of(DeclarativeHelper.createMapperInstance(annotation.get().value())));
      }
      responseMapper = Optional.of(transformer);
    }
    return responseMapper;
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
