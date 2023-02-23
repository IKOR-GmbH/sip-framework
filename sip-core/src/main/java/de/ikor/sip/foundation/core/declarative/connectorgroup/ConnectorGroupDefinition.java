package de.ikor.sip.foundation.core.declarative.connectorgroup;

import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annonation.OutboundConnector;

/**
 * Interface defining a connector group.
 *
 * <p>A connector group combines a set of connectors which are part of one logical unit, such as an
 * external system, or multiple external systems forming one unit.
 *
 * <p>If an {@link InboundConnector} and an {@link OutboundConnector} belong to the same connector
 * group and are linked to the same {@link IntegrationScenario}, the outbound connector is (in the
 * default configuration) ignored when receiving an integration call from the same groups inbound
 * connector.
 *
 * <p><em>Adapter developers should not implement this interface directly, but extend {@link
 * ConnectorGroupBase} instead.</em>
 *
 * @see ConnectorGroupBase
 * @see de.ikor.sip.foundation.core.declarative.annonation.ConnectorGroup
 */
public sealed interface ConnectorGroupDefinition permits ConnectorGroupBase, DefaultConnectorGroup {

  /**
   * Returns the ID of the connector group. Must be unique within the scope of the adapter.
   *
   * @return Unique connector group identifier
   */
  String getId();

  /**
   * Returns the path to the documentation resource for the connector group.
   *
   * @return Path to the documentation resource
   */
  String getPathToDocumentationResource();
}
