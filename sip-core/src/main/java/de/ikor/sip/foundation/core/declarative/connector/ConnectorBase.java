package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrator;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for connector definitions.
 *
 * <p>This class provides a default implementation for the {@link Orchestrator} interface, and
 * allows subclasses to attach an {@link Orchestrator} for the transformation between connector and
 * common domain models through the {@link #defineTransformationOrchestrator()} method.
 */
abstract class ConnectorBase
    implements ConnectorDefinition, Orchestrator<ConnectorOrchestrationInfo> {

  @Getter private final Logger logger = LoggerFactory.getLogger(getClass());

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
    return ConnectorOrchestrator.forConnector(this);
  }
}
