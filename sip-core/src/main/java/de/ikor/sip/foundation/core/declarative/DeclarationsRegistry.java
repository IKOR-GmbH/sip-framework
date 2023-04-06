package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.annonation.GlobalMapper;
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
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service(DeclarationsRegistryApi.BEAN_NAME)
public final class DeclarationsRegistry implements DeclarationsRegistryApi {

  private static final String CONNECTOR_GROUP = "connector group";
  private static final String SCENARIO = "integration scenario";
  private static final String CONNECTOR = "connector";

  private final List<ConnectorGroupDefinition> connectorGroups;
  private final List<IntegrationScenarioDefinition> scenarios;
  private final List<ConnectorDefinition> connectors;
  private final Map<MapperPair, ModelMapper<Object, Object>> globalModelMappersRegistry;

  public DeclarationsRegistry(
      List<ConnectorGroupDefinition> connectorGroups,
      List<IntegrationScenarioDefinition> scenarios,
      List<ConnectorDefinition> connectors,
      List<ModelMapper<?, ?>> modelMappers) {
    this.connectorGroups = connectorGroups;
    this.scenarios = scenarios;
    this.connectors = connectors;
    this.globalModelMappersRegistry = checkAndInitializeGlobalModelMappers(modelMappers);

    createMissingConnectorGroups();
    checkForDuplicateConnectorGroups();
    checkForDuplicateScenarios();
    checkForUnusedScenarios();
    checkForDuplicateConnectors();
  }

  @SuppressWarnings("unchecked")
  private Map<MapperPair, ModelMapper<Object, Object>> checkAndInitializeGlobalModelMappers(
      final List<ModelMapper<?, ?>> mappers) {
    final Map<MapperPair, ModelMapper<Object, Object>> modelMappers = new HashMap<>(mappers.size());
    mappers.stream()
        .filter(modelMapper -> modelMapper.getClass().isAnnotationPresent(GlobalMapper.class))
        .forEach(
            mapper -> {
              final MapperPair mapperPair =
                  MapperPair.builder()
                      .sourceClass(mapper.getSourceModelClass())
                      .targetClass(mapper.getTargetModelClass())
                      .build();
              if (modelMappers.containsKey(mapperPair)) {
                final var duplicate = modelMappers.get(mapperPair);
                throw new SIPFrameworkInitializationException(
                    String.format(
                        "ModelMapper implementations %s and %s share the same source and target model classes",
                        mapper.getClass().getName(), duplicate.getClass().getName()));
              }
              modelMappers.put(mapperPair, (ModelMapper<Object, Object>) mapper);
            });
    return modelMappers;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <C, S> Optional<ModelMapper<C, S>> getGlobalModelMapperForModels(
      final Class<C> sourceModelClass, final Class<S> targetModelClass) {
    final var mapperPair =
        MapperPair.builder().sourceClass(sourceModelClass).targetClass(targetModelClass).build();
    if (globalModelMappersRegistry.containsKey(mapperPair)) {
      return Optional.of((ModelMapper<C, S>) globalModelMappersRegistry.get(mapperPair));
    }
    return Optional.empty();
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
        connectorGroups.stream().map(ConnectorGroupDefinition::getId).toList();
    connectorGroupIds.forEach(id -> checkIfDuplicate(set, id, CONNECTOR_GROUP));
  }

  private void checkForDuplicateScenarios() {
    Set<String> set = new HashSet<>();
    List<String> scenarioIds =
        scenarios.stream().map(IntegrationScenarioDefinition::getId).toList();
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
    List<String> connectorIds = connectors.stream().map(ConnectorDefinition::getId).toList();
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

  @SuppressWarnings("rawtypes")
  @Override
  public List<InboundConnectorDefinition> getInboundConnectors() {
    return connectors.stream()
        .filter(InboundConnectorDefinition.class::isInstance)
        .map(InboundConnectorDefinition.class::cast)
        .toList();
  }

  @Override
  public List<OutboundConnectorDefinition> getOutboundConnectors() {
    return connectors.stream()
        .filter(OutboundConnectorDefinition.class::isInstance)
        .map(OutboundConnectorDefinition.class::cast)
        .toList();
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<InboundConnectorDefinition> getInboundConnectorsByScenarioId(String scenarioId) {
    return connectors.stream()
        .filter(connector -> connector.getScenarioId().equals(scenarioId))
        .filter(InboundConnectorDefinition.class::isInstance)
        .map(InboundConnectorDefinition.class::cast)
        .toList();
  }

  @Override
  public List<OutboundConnectorDefinition> getOutboundConnectorsByScenarioId(String scenarioId) {
    return connectors.stream()
        .filter(connector -> connector.getScenarioId().equals(scenarioId))
        .filter(OutboundConnectorDefinition.class::isInstance)
        .map(OutboundConnectorDefinition.class::cast)
        .toList();
  }

  @Builder
  private record MapperPair(Class<?> sourceClass, Class<?> targetClass) {}
}
