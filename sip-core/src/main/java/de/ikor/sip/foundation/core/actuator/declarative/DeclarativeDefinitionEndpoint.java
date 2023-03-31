package de.ikor.sip.foundation.core.actuator.declarative;

import static de.ikor.sip.foundation.core.actuator.declarative.DeclarativeEndpointInfoTransformer.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorGroupInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.DeclarativeStructureInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.IntegrationScenarioInfo;
import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/** Actuator endpoints for exposing Connectors, Connector Groups and Integration Scenarios. */
@Component
@RestControllerEndpoint(id = "adapterdefinition")
@DependsOn("adapterBuilder")
public class DeclarativeDefinitionEndpoint {

  private final DeclarationsRegistry declarationsRegistry;
  private final RoutesRegistry routesRegistry;

  private final JsonSchemaGenerator schemaGen;

  private final List<ConnectorGroupInfo> connectorGroups = new ArrayList<>();
  private final List<IntegrationScenarioInfo> scenarios = new ArrayList<>();
  private final List<ConnectorInfo> connectors = new ArrayList<>();

  /**
   * Constructor for {@link DeclarativeDefinitionEndpoint}
   *
   * @param declarationsRegistry {@link DeclarationsRegistry}
   * @param routesRegistry {@link RoutesRegistry}
   * @param mapper {@link ObjectMapper}
   */
  public DeclarativeDefinitionEndpoint(
      DeclarationsRegistry declarationsRegistry,
      RoutesRegistry routesRegistry,
      ObjectMapper mapper) {
    this.declarationsRegistry = declarationsRegistry;
    this.routesRegistry = routesRegistry;
    this.schemaGen = new JsonSchemaGenerator(mapper);
  }

  @EventListener(ApplicationReadyEvent.class)
  private void collectInfos() {
    initializeConnectorInfos();
    initializeIntegrationScenarioInfos();
    initializeConnectorGroupInfos();
  }

  /**
   * Base endpoint which exposes adapter structure including connectors, connector groups and
   * scenarios.
   *
   * @return DeclarativeStructureInfo
   */
  @GetMapping("/")
  public DeclarativeStructureInfo getStructure() {
    return DeclarativeStructureInfo.builder()
        .connectorgroups(getConnectorGroupInfo())
        .scenarios(getScenarioInfo())
        .build();
  }

  @GetMapping("/connectorgroups")
  public List<ConnectorGroupInfo> getConnectorGroupInfo() {
    return connectorGroups;
  }

  @GetMapping("/scenarios")
  public List<IntegrationScenarioInfo> getScenarioInfo() {
    return scenarios;
  }

  @GetMapping("/connectors")
  public List<ConnectorInfo> getConnectorInfo() {
    return connectors;
  }

  private void initializeConnectorInfos() {
    declarationsRegistry
        .getConnectors()
        .forEach(
            connector ->
                connectors.add(createAndAddConnectorInfo(connector, routesRegistry, schemaGen)));
  }

  private void initializeIntegrationScenarioInfos() {
    declarationsRegistry
        .getScenarios()
        .forEach(scenario -> scenarios.add(createIntegrationScenarioInfo(scenario, schemaGen)));
  }

  private void initializeConnectorGroupInfos() {
    declarationsRegistry
        .getConnectorGroups()
        .forEach(
            connectorGroup ->
                connectorGroups.add(
                    createConnectorGroupInfo(
                        connectors.stream()
                            .filter(
                                connectorInfo ->
                                    connectorInfo
                                        .getConnectorGroupId()
                                        .equals(connectorGroup.getId()))
                            .toList(),
                        connectorGroup)));
  }
}
