package de.ikor.sip.foundation.core.declarative;

import static java.util.function.Predicate.not;

import de.ikor.sip.foundation.core.declarative.annonation.Disabled;
import de.ikor.sip.foundation.core.declarative.annonation.GlobalMapper;
import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Getter
@Service
public final class DeclarationsRegistry implements DeclarationsRegistryApi {

  private static final String CONNECTOR_GROUP = "connector group";
  private static final String SCENARIO = "integration scenario";
  private static final String CONNECTOR = "connector";

  private final ApplicationContext applicationContext;
  private final List<ConnectorGroupDefinition> connectorGroups;
  private final List<IntegrationScenarioDefinition> scenarios;
  private final List<ConnectorDefinition> connectors;
  private final Map<MapperPair, ModelMapper<Object, Object>> globalModelMappersRegistry;

  @Getter private final List<CompositeProcessDefinition> compositeProcessDefinitions;

  @Override
  public List<IntegrationScenarioDefinition> getCompositeProcessConsumerDefinitions(
      String compositeProcessID) {
    return compositeProcessDefinitions.stream()
        .filter(scenario -> scenario.getId().equals(compositeProcessID))
        .findFirst()
        .get()
        .getConsumerDefinitions()
        .stream()
        .map(definition -> (IntegrationScenarioDefinition) applicationContext.getBean(definition))
        .toList();
  }

  @Override
  public List<IntegrationScenarioDefinition> getCompositeProcessProviderDefinitions(
      String compositeProcessID) {
    return compositeProcessDefinitions.stream()
        .filter(scenario -> scenario.getId().equals(compositeProcessID))
        .findFirst()
        .get()
        .getProviderDefinitions()
        .stream()
        .map(definition -> (IntegrationScenarioDefinition) applicationContext.getBean(definition))
        .toList();
  }

  @Override
  public List<CompositeProcessDefinition> getCompositeProvidersForScenario(
      IntegrationScenarioDefinition integrationScenario) {
    return compositeProcessDefinitions.stream()
        .filter(
            composite ->
                composite.getConsumerDefinitions().stream()
                    .anyMatch(consumer -> consumer.equals(integrationScenario.getClass())))
        .toList();
  }

  @Override
  public List<CompositeProcessDefinition> getCompositeConsumersForScenario(
      IntegrationScenarioDefinition integrationScenario) {
    return compositeProcessDefinitions.stream()
        .filter(
            composite ->
                composite.getProviderDefinitions().stream()
                    .anyMatch(consumer -> consumer.equals(integrationScenario.getClass())))
        .toList();
  }

  public DeclarationsRegistry(
      List<ConnectorGroupDefinition> autowiredConnectorGroups,
      List<IntegrationScenarioDefinition> autowiredScenarios,
      List<ConnectorDefinition> autowiredConnectors,
      List<ModelMapper<?, ?>> modelMappers,
      List<CompositeProcessDefinition> compositeProcessDefinitions,
      ApplicationContext applicationContext) {

    this.applicationContext = applicationContext;
    this.connectorGroups =
        autowiredConnectorGroups.stream().filter(not(isDisabled())).collect(Collectors.toList());

    this.scenarios = autowiredScenarios.stream().filter(not(isDisabled())).toList();

    this.connectors =
        autowiredConnectors.stream()
            .filter(not(isDisabled(autowiredScenarios, autowiredConnectorGroups)))
            .toList();

    this.globalModelMappersRegistry = checkAndInitializeGlobalModelMappers(modelMappers);

    this.compositeProcessDefinitions = compositeProcessDefinitions;

    createMissingConnectorGroups();
    checkForDuplicateConnectorGroups();
    checkForDuplicateScenarios();
    // checkForUnusedScenarios();
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
                throw SIPFrameworkInitializationException.init(
                    "ModelMapper implementations %s and %s share the same source and target model classes",
                    mapper.getClass().getName(), duplicate.getClass().getName());
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
    connectorGroups.forEach(
        connectorGroup ->
            checkIfDuplicate(
                set, connectorGroup.getId(), connectorGroup.getClass().getName(), CONNECTOR_GROUP));
  }

  private void checkForDuplicateScenarios() {
    Set<String> set = new HashSet<>();
    scenarios.forEach(
        scenario ->
            checkIfDuplicate(set, scenario.getId(), scenario.getClass().getName(), SCENARIO));
  }

  private void checkForDuplicateConnectors() {
    Set<String> set = new HashSet<>();
    connectors.forEach(
        connector ->
            checkIfDuplicate(set, connector.getId(), connector.getClass().getName(), CONNECTOR));
  }

  private void checkIfDuplicate(
      Set<String> set, String id, String className, String declarativeElement) {
    if (!set.add(id)) {
      throw SIPFrameworkInitializationException.init(
          "There is a duplicate %s id %s in class %s", declarativeElement, id, className);
    }
  }

  private void checkForUnusedScenarios() {
    scenarios.stream()
        .filter(
            scenario ->
                getInboundConnectorsByScenarioId(scenario.getId()).isEmpty()
                    || getOutboundConnectorsByScenarioId(scenario.getId()).isEmpty())
        .map(
            scenario -> {
              throw SIPFrameworkInitializationException.init(
                  "There is unused integration scenario with id %s", scenario.getId());
            })
        .forEach(
            x -> {
              /* don't need the result */
            });
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
                SIPFrameworkInitializationException.init(
                    "There is no integration scenario with id: %s", scenarioId));
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

  private Predicate<Object> isDisabled() {
    return elem -> elem.getClass().isAnnotationPresent(Disabled.class);
  }

  private Predicate<ConnectorDefinition> isDisabled(
      List<IntegrationScenarioDefinition> scenarios,
      List<ConnectorGroupDefinition> connectorGroups) {
    return connector -> {
      if (isDisabled().test(connector)) {
        return true;
      }

      Optional<IntegrationScenarioDefinition> scenarioDefinition =
          scenarios.stream()
              .filter(scenario -> scenario.getId().equals(connector.getScenarioId()))
              .findFirst();
      if (scenarioDefinition.isPresent() && isDisabled().test(scenarioDefinition.get())) {
        return true;
      }

      Optional<ConnectorGroupDefinition> connectorGroupDefinition =
          connectorGroups.stream()
              .filter(group -> group.getId().equals(connector.getConnectorGroupId()))
              .findFirst();
      return connectorGroupDefinition.isPresent()
          && isDisabled().test(connectorGroupDefinition.get());
    };
  }

  @Builder
  record MapperPair(Class<?> sourceClass, Class<?> targetClass) {}
}
