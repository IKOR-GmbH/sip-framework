package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import java.util.Optional;

/**
 * Common definition interface for specifying connectors used within a SIP adapter.
 *
 * <p>A connector is responsible for defining the endpoints for communication with external systems,
 * as well as transforming the request- and response-models between the external system's domain and
 * the integration scenario's common domain model.
 *
 * <p><em>Adapter developers should not implement this interface directly, but use one of the more
 * specific {@link InboundConnectorDefinition} or {@link OutboundConnectorDefinition} interfaces
 * instead.</em>
 *
 * @see InboundConnectorDefinition
 * @see OutboundConnectorDefinition
 */
public interface ConnectorDefinition extends Orchestratable<ConnectorOrchestrationInfo> {

  /**
   * Returns the ID of the connector. Must be unique within the scope of the adapter.
   *
   * @return Unique connector identifier
   */
  String getId();

  /**
   * Returns the type of the connector.
   *
   * @return Connector type
   */
  ConnectorType getConnectorType();

  /**
   * Returns the ID of the connector group this connector belongs to.
   *
   * @see de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition
   * @return ID of the connector group
   */
  String getConnectorGroupId();

  /**
   * Returns the ID of the integration scenario this connector belongs to.
   *
   * @see de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition
   * @return ID of the integration scenario
   */
  String getScenarioId();

  /**
   * Returns the base class of the request model used by the connector. This is the request model
   * that is used by the adapter to communicate with the external system.
   *
   * @return Request model base class
   */
  Class<?> getRequestModelClass();

  /**
   * Returns whether this connector has a response flow in the connected scenario. The default
   * imlementation returns <code>true</code> if {@link #getResponseModelClass()} returns a non-empty
   * {@link Optional}.
   *
   * @return <code>true</code> if the connector has a response flow, <code>false</code> otherwise
   */
  default boolean hasResponseFlow() {
    return getResponseModelClass().isPresent();
  }

  /**
   * Returns the base class of the response model used by the connector. This is the response model
   * that is used by the adapter to communicate with the external system.
   *
   * @return Response model base class, or an empty {@link Optional} if the connector does not have
   *     a response flow
   */
  Optional<Class<?>> getResponseModelClass();

  /**
   * Returns the path to the documentation resource for this connector. The documentation resource
   * is a file that contains documentation for the connector, such as a description of the external
   * system's API.
   *
   * @return Path to the documentation resource
   */
  String getPathToDocumentationResource();
}
