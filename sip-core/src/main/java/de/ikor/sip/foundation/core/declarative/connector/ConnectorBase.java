package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.DeclarationRegistryApi;
import de.ikor.sip.foundation.core.declarative.annonation.UseRequestMapping;
import de.ikor.sip.foundation.core.declarative.annonation.UseResponseMapping;
import de.ikor.sip.foundation.core.declarative.model.FindAutomaticModelMapper;
import de.ikor.sip.foundation.core.declarative.model.ModelMapper;
import de.ikor.sip.foundation.core.declarative.model.RequestMappingRouteTransformer;
import de.ikor.sip.foundation.core.declarative.model.ResponseMappingRouteTransformer;
import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrator;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
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
abstract class ConnectorBase
    implements ConnectorDefinition,
        Orchestrator<ConnectorOrchestrationInfo>,
        ApplicationContextAware {

  @Getter(AccessLevel.PROTECTED)
  private ApplicationContext applicationContext;

  @Getter(AccessLevel.PROTECTED)
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Delegate
  private final Orchestrator<ConnectorOrchestrationInfo> modelTransformationOrchestrator =
      defineTransformationOrchestrator();

  @Override
  public Orchestrator<ConnectorOrchestrationInfo> getOrchestrator() {
    return this;
  }

  public final Supplier<IntegrationScenarioDefinition> getScenario() {
    return () ->
        applicationContext.getBean(DeclarationRegistryApi.class).getScenarioById(getScenarioId());
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
    DeclarativeHelper.getAnnotationIfPresent(UseRequestMapping.class, this)
        .ifPresent(
            requestAnnotation -> {
              final Optional<ModelMapper<Object, Object>> mapper =
                  FindAutomaticModelMapper.class.equals(requestAnnotation.mapper())
                      ? Optional.empty()
                      : Optional.of(DeclarativeHelper.createInstance(requestAnnotation.mapper()));
              orchestrator.setRequestRouteTransformer(
                  RequestMappingRouteTransformer.forConnectorWithScenario(this, getScenario())
                      .setMapper(mapper));
            });
    DeclarativeHelper.getAnnotationIfPresent(UseResponseMapping.class, this)
        .ifPresent(
            respAnnotation -> {
              final Optional<ModelMapper<Object, Object>> mapper =
                  FindAutomaticModelMapper.class.equals(respAnnotation.mapper())
                      ? Optional.empty()
                      : Optional.of(DeclarativeHelper.createInstance(respAnnotation.mapper()));
              orchestrator.setRequestRouteTransformer(
                  ResponseMappingRouteTransformer.forConnectorWithScenario(this, getScenario())
                      .setMapper(mapper));
            });
    return orchestrator;
  }

  @Override
  public final void setApplicationContext(final ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }
}
