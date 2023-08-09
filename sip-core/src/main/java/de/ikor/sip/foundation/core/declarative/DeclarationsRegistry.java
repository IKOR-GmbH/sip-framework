package de.ikor.sip.foundation.core.declarative;

import static java.util.function.Predicate.not;

import de.ikor.sip.foundation.core.declarative.annonation.*;
import de.ikor.sip.foundation.core.declarative.connector.*;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupBase;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.connectorgroup.DefaultConnectorGroup;
import de.ikor.sip.foundation.core.declarative.model.ModelMapper;
import de.ikor.sip.foundation.core.declarative.model.RequestMappingRouteTransformer;
import de.ikor.sip.foundation.core.declarative.model.ResponseMappingRouteTransformer;
import de.ikor.sip.foundation.core.declarative.orchestration.connector.ConnectorOrchestrator;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioBase;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Getter
@Service
@Slf4j
public final class DeclarationsRegistry implements DeclarationsRegistryApi {

  private static final String CONNECTOR_GROUP = "connector group";
  private static final String SCENARIO = "integration scenario";
  private static final String CONNECTOR = "connector";

  private final ApplicationContext applicationContext;
  private final List<ConnectorGroupDefinition> connectorGroups;

  private final List<CompositeProcessDefinition> processes;
  private final List<IntegrationScenarioDefinition> scenarios;
  private final List<ConnectorDefinition> connectors;
  private final Map<MapperPair, ModelMapper<Object, Object>> globalModelMappersRegistry;

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

    this.processes =
        compositeProcessDefinitions.stream().filter(not(isDisabled())).collect(Collectors.toList());

    createMissingConnectorGroups();
    checkForDuplicateConnectorGroups();
    checkForUnusedMappers();
    checkForDuplicateScenarios();
    checkAnnotatedClassForMissingParent(IntegrationScenario.class, IntegrationScenarioBase.class);
    checkAnnotatedClassForMissingParent(ConnectorGroup.class, ConnectorGroupBase.class);
    checkAnnotatedClassForMissingParent(InboundConnector.class, InboundConnectorBase.class);
    checkAnnotatedClassForMissingParent(OutboundConnector.class, OutboundConnectorDefinition.class);
    checkForUnusedScenarios();
    checkForDuplicateConnectors();
  }

  private void checkForUnusedMappers() {
    connectors.forEach(
        connectorDefinition -> {
          if (connectorDefinition instanceof ConnectorBase base) {
            base.getRequestMapper()
                .ifPresent(
                    mapper -> {
                      if (isRequestMappingOverridden(connectorDefinition, mapper)) {
                        throw SIPFrameworkInitializationException.init(
                            "Request mapping in connector '%s' is defined in annotation, but overridden by request route transformator",
                            connectorDefinition.getId());
                      }
                    });
            base.getResponseMapper()
                .ifPresent(
                    mapper -> {
                      if (isResponseMappingOverridden(connectorDefinition, mapper)) {
                        throw SIPFrameworkInitializationException.init(
                            "Response mapping in connector '%s' is defined in annotation, but overridden by response route transformator",
                            connectorDefinition.getId());
                      }
                    });
          }
        });
  }

  private boolean isRequestMappingOverridden(
      ConnectorDefinition connectorDefinition,
      RequestMappingRouteTransformer<Object, Object> mapper) {
    return connectorDefinition.getOrchestrator()
            instanceof ConnectorOrchestrator connectorOrchestrator
        && (!mapper.equals(connectorOrchestrator.getRequestRouteTransformer()));
  }

  private boolean isResponseMappingOverridden(
      ConnectorDefinition connectorDefinition,
      ResponseMappingRouteTransformer<Object, Object> mapper) {
    return connectorDefinition.getOrchestrator()
            instanceof ConnectorOrchestrator connectorOrchestrator
        && (!mapper.equals(connectorOrchestrator.getResponseRouteTransformer()));
  }

  private void checkAnnotatedClassForMissingParent(
      Class<? extends Annotation> annotatedClass, Class<?> parentClass) {
    applicationContext
        .getBeansWithAnnotation(annotatedClass)
        .values()
        .forEach(
            o -> {
              if (!parentClass.isInstance(o)) {
                throw SIPFrameworkInitializationException.init(
                    "Annotated %s %s is missing %s parent class.",
                    annotatedClass.getSimpleName(),
                    o.getClass().getName(),
                    parentClass.getSimpleName());
              }
            });
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
          "There is a duplicate %s id %s in class %s. A unique connectorId should be provided in the connector's annotation.",
          declarativeElement, id, className);
    }
  }

  private void checkForUnusedScenarios() {
    scenarios.stream()
        .filter(scenario -> getProvidersForScenario(scenario.getId()).isEmpty())
        .forEach(
            scenario -> {
              throw SIPFrameworkInitializationException.init(
                  "Nothing is providing data to the integration scenario with id '%s'",
                  scenario.getId());
            });
    scenarios.stream()
        .filter(scenario -> getConsumersForScenario(scenario.getId()).isEmpty())
        .forEach(
            scenario -> {
              throw SIPFrameworkInitializationException.init(
                  "Nothing is consuming data from the integration scenario with id '%s'",
                  scenario.getId());
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

  @Override
  public List<IntegrationScenarioDefinition> getCompositeProcessConsumerDefinitions(
      String compositeProcessID) {
    return processes.stream()
        .filter(scenario -> scenario.getId().equals(compositeProcessID))
        .findFirst()
        .orElseThrow(
            () ->
                SIPFrameworkInitializationException.init(
                    "Composite process '%s' can not be found in the registry. Please check your configuration.",
                    compositeProcessID))
        .getConsumerDefinitions()
        .stream()
        .map(definition -> (IntegrationScenarioDefinition) applicationContext.getBean(definition))
        .toList();
  }

  @Override
  public IntegrationScenarioDefinition getCompositeProcessProviderDefinition(
      String compositeProcessID) {
    return applicationContext.getBean(
        processes.stream()
            .filter(scenario -> scenario.getId().equals(compositeProcessID))
            .findFirst()
            .orElseThrow(
                () ->
                    SIPFrameworkInitializationException.init(
                        "Composite process '%s' can not be found in the registry. Please check your configuration",
                        compositeProcessID))
            .getProviderDefinition());
  }

  @Override
  public List<CompositeProcessDefinition> getCompositeProcessProvidersForScenario(
      IntegrationScenarioDefinition integrationScenario) {
    return processes.stream()
        .filter(
            composite ->
                composite.getConsumerDefinitions().stream()
                    .anyMatch(consumer -> consumer.equals(integrationScenario.getClass())))
        .toList();
  }

  @Override
  public List<CompositeProcessDefinition> getCompositeProcessConsumersForScenario(
      IntegrationScenarioDefinition integrationScenario) {
    return processes.stream()
        .filter(
            composite ->
                Stream.of(composite.getProviderDefinition())
                    .anyMatch(consumer -> consumer.equals(integrationScenario.getClass())))
        .toList();
  }

  @Override
  public List<IntegrationScenarioProviderDefinition> getProvidersForScenario(String scenarioID) {
    List<IntegrationScenarioProviderDefinition> inboundConnectorsForScenario =
        List.copyOf(getInboundConnectorsByScenarioId(scenarioID));
    List<IntegrationScenarioProviderDefinition> compositeProcessProvidersForScenario =
        List.copyOf(getCompositeProcessProvidersForScenario(getScenarioById(scenarioID)));
    return Stream.concat(
            inboundConnectorsForScenario.stream(), compositeProcessProvidersForScenario.stream())
        .toList();
  }

  public List<IntegrationScenarioConsumerDefinition> getConsumersForScenario(String scenarioID) {
    List<IntegrationScenarioConsumerDefinition> outboundConnectorsForScenario =
        List.copyOf(getOutboundConnectorsByScenarioId(scenarioID));
    List<IntegrationScenarioConsumerDefinition> compositeProcessConsumersForScenario =
        List.copyOf(getCompositeProcessConsumersForScenario(getScenarioById(scenarioID)));
    return Stream.concat(
            outboundConnectorsForScenario.stream(), compositeProcessConsumersForScenario.stream())
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
