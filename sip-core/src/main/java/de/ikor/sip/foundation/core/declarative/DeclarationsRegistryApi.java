package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.model.ModelMapper;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import java.util.Optional;

/** API interface for {@link DeclarationsRegistry} used within the framework structure. */
public sealed interface DeclarationsRegistryApi permits DeclarationsRegistry {

  /**
   * Get {@link ConnectorGroupDefinition} by its id.
   *
   * @param connectorGroupId is its id
   * @return Optional connector group
   */
  Optional<ConnectorGroupDefinition> getConnectorGroupById(final String connectorGroupId);

  /**
   * Get {@link IntegrationScenarioDefinition} by its id.
   *
   * @param scenarioId is its id
   * @return Integration scenario
   */
  IntegrationScenarioDefinition getScenarioById(final String scenarioId);

  /**
   * Get {@link ConnectorDefinition} by its id.
   *
   * @param connectorId is its id
   * @return Optional connector
   */
  Optional<ConnectorDefinition> getConnectorById(final String connectorId);

  /**
   * Get list of all {@link InboundConnectorDefinition}.
   *
   * @return all inbound connectors
   */
  @SuppressWarnings("rawtypes")
  List<InboundConnectorDefinition> getInboundConnectors();

  /**
   * Get list of all {@link OutboundConnectorDefinition}.
   *
   * @return all outbound connectors
   */
  List<OutboundConnectorDefinition> getOutboundConnectors();

  /**
   * Get list of {@link InboundConnectorDefinition} by id of {@link IntegrationScenarioDefinition}.
   *
   * @param scenarioId is its id
   * @return inbound connectors
   */
  @SuppressWarnings("rawtypes")
  List<InboundConnectorDefinition> getInboundConnectorsByScenarioId(String scenarioId);

  /**
   * Get list of {@link OutboundConnectorDefinition} by id of {@link IntegrationScenarioDefinition}.
   *
   * @param scenarioId is its id
   * @return outbound connectors
   */
  List<OutboundConnectorDefinition> getOutboundConnectorsByScenarioId(String scenarioId);

  /**
   * If exists, returns a {@link ModelMapper} for that can map from the given <code>sourceModelClass
   * </code> to <code>targetModelClass</code>.
   *
   * @param sourceModelClass Source model class
   * @param targetModelClass Target model class
   * @return If exists, returns a {@link ModelMapper} for the given model classes
   * @param <S> Connector model type
   * @param <T> Scenario model type
   */
  <S, T> Optional<ModelMapper<S, T>> getModelMapperForModels(
      Class<S> sourceModelClass, Class<T> targetModelClass);
}