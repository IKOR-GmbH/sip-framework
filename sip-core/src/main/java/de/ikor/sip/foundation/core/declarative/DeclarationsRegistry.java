package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.connectorgroup.DefaultConnectorGroup;
import de.ikor.sip.foundation.core.declarative.model.ModelMapper;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Getter
@Slf4j
@Service
public class DeclarationsRegistry implements DeclarationRegistryApi {

  @Value
  @Builder
  private static class MapperPair {
    Class<?> scenarioClass;
    Class<?> connectorClass;
  }

  private static final String CONNECTOR_GROUP = "connector group";
  private static final String SCENARIO = "integration scenario";
  private static final String CONNECTOR = "connector";

  private final List<ConnectorGroupDefinition> connectorGroups;
  private final List<IntegrationScenarioDefinition> scenarios;
  private final List<ConnectorDefinition> connectors;

  @Getter
  @SuppressWarnings("rawtypes")
  private final Map<MapperPair, ModelMapper> modelMappers;

  public DeclarationsRegistry(
      List<ConnectorGroupDefinition> connectorGroups,
      List<IntegrationScenarioDefinition> scenarios,
      List<ConnectorDefinition> connectors,
      List<ModelMapper> modelMappers) {
    this.connectorGroups = connectorGroups;
    this.scenarios = scenarios;
    this.connectors = connectors;
    this.modelMappers = checkAndInitializeModelMappers(modelMappers);

    checkForDuplicateConnectorGroups();
    checkForDuplicateScenarios();
    checkForUnusedScenarios();
    checkForDuplicateConnectors();
  }

  @SuppressWarnings("rawtypes")
  private Map<MapperPair, ModelMapper> checkAndInitializeModelMappers(
      final List<ModelMapper> mappers) {
    final Map<MapperPair, ModelMapper> modelMappers = new HashMap<>(mappers.size());
    mappers.forEach(
        mapper -> {
          final MapperPair mapperPair =
              MapperPair.builder()
                  .scenarioClass(mapper.getScenarioModelClass())
                  .connectorClass(mapper.getConnectorModelClass())
                  .build();
          if (modelMappers.containsKey(mapperPair)) {
            final var duplicate = modelMappers.get(mapperPair);
            throw new SIPFrameworkInitializationException(
                String.format(
                    "ModelMapper implementations %s and %s share the same scenario and connector model classes",
                    mapper.getClass().getName(), duplicate.getClass().getName()));
          }
          modelMappers.put(mapperPair, mapper);
        });
    return modelMappers;
  }

  private void createMissingConnectorGroups() {
    connectors.forEach(
        connector -> {
          Optional<ConnectorGroupDefinition> connectorGroup =
              getConnectorGroupById(connector.getConnectorGroupId());
          if (connectorGroup.isEmpty()) {
            connectorGroups.add(new DefaultConnectorGroup(connector.getConnectorGroupId()));
          }
        });
  }

  private void checkForDuplicateConnectorGroups() {
    Set<String> set = new HashSet<>();
    List<String> connectorGroupIds =
        connectorGroups.stream().map(ConnectorGroupDefinition::getId).collect(Collectors.toList());
    connectorGroupIds.forEach(id -> checkIfDuplicate(set, id, CONNECTOR_GROUP));
  }

  private void checkForDuplicateScenarios() {
    Set<String> set = new HashSet<>();
    List<String> scenarioIds =
        scenarios.stream().map(IntegrationScenarioDefinition::getId).collect(Collectors.toList());
    scenarioIds.forEach(id -> checkIfDuplicate(set, id, SCENARIO));
  }

  private void checkForUnusedScenarios() {
    scenarios.stream()
        .filter(
            scenario ->
                getInboundConnectorsByScenarioId(scenario.getId()).isEmpty()
                    || getOutboundConnectorsByScenarioId(scenario.getId()).isEmpty())
        .map(
            scenario -> {
              throw new SIPFrameworkInitializationException(
                  String.format(
                      "There is unused integration scenario with id %s", scenario.getId()));
            })
        .forEach(
            x -> {
              /* don't need the result */
            });
  }

  private void checkForDuplicateConnectors() {
    Set<String> set = new HashSet<>();
    List<String> connectorIds =
        connectors.stream().map(ConnectorDefinition::getId).collect(Collectors.toList());
    connectorIds.forEach(id -> checkIfDuplicate(set, id, CONNECTOR));
  }

  private void checkIfDuplicate(Set<String> set, String id, String declarativeElement) {
    if (!set.add(id)) {
      throw new SIPFrameworkInitializationException(
          String.format("There is a duplicate %s id: %s", declarativeElement, id));
    }
  }

  @Override
  public Optional<ConnectorGroupDefinition> getConnectorGroupById(final String connectorGroupId) {
    return connectorGroups.stream()
        .filter(connector -> connector.getId().equals(connectorGroupId))
        .findFirst();
  }

  @Override
  public IntegrationScenarioDefinition getScenarioById(final String scenarioId) {
    return scenarios.stream()
        .filter(scenario -> scenario.getId().equals(scenarioId))
        .findFirst()
        .orElseThrow(
            () ->
                new SIPFrameworkInitializationException(
                    String.format("There is no integration scenario with id: %s", scenarioId)));
  }

  @Override
  public Optional<ConnectorDefinition> getConnectorById(final String connectorId) {
    return connectors.stream()
        .filter(connector -> connector.getId().equals(connectorId))
        .findFirst();
  }

  @Override
  public List<InboundConnectorDefinition> getInboundConnectors() {
    return connectors.stream()
        .filter(InboundConnectorDefinition.class::isInstance)
        .map(InboundConnectorDefinition.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public List<OutboundConnectorDefinition> getOutboundConnectors() {
    return connectors.stream()
        .filter(OutboundConnectorDefinition.class::isInstance)
        .map(OutboundConnectorDefinition.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<InboundConnectorDefinition> getInboundConnectorById(String connectorId) {
    return connectors.stream()
        .filter(connector -> connector.getId().equals(connectorId))
        .map(InboundConnectorDefinition.class::cast)
        .findFirst();
  }

  @Override
  public Optional<OutboundConnectorDefinition> getOutboundConnectorById(String connectorId) {
    return connectors.stream()
        .filter(connector -> connector.getId().equals(connectorId))
        .map(OutboundConnectorDefinition.class::cast)
        .findFirst();
  }

  @Override
  public List<InboundConnectorDefinition> getInboundConnectorsByConnectorGroupId(
      String connectorGroupId) {
    return connectors.stream()
        .filter(connector -> connector.getConnectorGroupId().equals(connectorGroupId))
        .filter(InboundConnectorDefinition.class::isInstance)
        .map(InboundConnectorDefinition.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public List<OutboundConnectorDefinition> getOutboundConnectorsByConnectorGroupId(
      String connectorGroupId) {
    return connectors.stream()
        .filter(connector -> connector.getConnectorGroupId().equals(connectorGroupId))
        .filter(OutboundConnectorDefinition.class::isInstance)
        .map(OutboundConnectorDefinition.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public List<InboundConnectorDefinition> getInboundConnectorsByScenarioId(String scenarioId) {
    return connectors.stream()
        .filter(connector -> connector.getScenarioId().equals(scenarioId))
        .filter(InboundConnectorDefinition.class::isInstance)
        .map(InboundConnectorDefinition.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public List<OutboundConnectorDefinition> getOutboundConnectorsByScenarioId(String scenarioId) {
    return connectors.stream()
        .filter(connector -> connector.getScenarioId().equals(scenarioId))
        .filter(OutboundConnectorDefinition.class::isInstance)
        .map(OutboundConnectorDefinition.class::cast)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <C, S> Optional<ModelMapper<C, S>> getModelMapperForModels(
      final Class<C> connectorModel, final Class<S> scenarioModel) {
    final var mapperPair =
        MapperPair.builder().connectorClass(connectorModel).scenarioClass(scenarioModel).build();
    if (modelMappers.containsKey(mapperPair)) {
      return Optional.of((ModelMapper<C, S>) modelMappers.get(mapperPair));
    }
    return Optional.empty();
  }
}
