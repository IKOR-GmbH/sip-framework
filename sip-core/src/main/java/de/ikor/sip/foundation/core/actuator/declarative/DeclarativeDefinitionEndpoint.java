package de.ikor.sip.foundation.core.actuator.declarative;

import static de.ikor.sip.foundation.core.actuator.declarative.DeclarativeEndpointInfoTransformer.*;

import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorGroupInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.IntegrationScenarioInfo;
import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/** Actuator endpoints for exposing Connectors, Connector Groups and Integration Scenarios. */
@Component
@RestControllerEndpoint(id = "adapterdefinition")
public class DeclarativeDefinitionEndpoint {

  private static final String CONNECTOR_GROUPS_PATH = "connectorgroups";
  private static final String SCENARIOS_PATH = "scenarios";
  private static final String CONNECTORS_PATH = "connectors";

  private final DeclarationsRegistry declarationsRegistry;
  private final List<ConnectorGroupInfo> connectorGroups = new ArrayList<>();
  private final List<IntegrationScenarioInfo> scenarios = new ArrayList<>();
  private final List<ConnectorInfo> connectors = new ArrayList<>();

  public DeclarativeDefinitionEndpoint(DeclarationsRegistry declarationsRegistry) {
    this.declarationsRegistry = declarationsRegistry;
  }

  @PostConstruct
  private void collectInfos() {
    initializeConnectorGroupInfos();
    initializeIntegrationScenarioInfos();
    initializeEndpointInfos();
  }

  /**
   * Base endpoint which exposes adapter structure including connectors, connector groups and
   * scenarios.
   *
   * @param request HttpServletRequest
   * @return links Map<String, List>
   */
  @GetMapping
  public Map<String, List> getStructure(HttpServletRequest request) {

    return Map.of(
        CONNECTORS_PATH,
        getConnectorInfo(),
        CONNECTOR_GROUPS_PATH,
        getConnectorGroupInfo(),
        SCENARIOS_PATH,
        getScenarioInfo());
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

  private void initializeConnectorGroupInfos() {
    declarationsRegistry
        .getConnectorGroups()
        .forEach(
            connectorGroup ->
                connectorGroups.add(
                    createConnectorGroupInfo(
                        declarationsRegistry.getInboundConnectorsByConnectorGroupId(
                            connectorGroup.getID()),
                        declarationsRegistry.getOutboundConnectorsByConnectorGroupId(
                            connectorGroup.getID()),
                        connectorGroup)));
  }

  private void initializeIntegrationScenarioInfos() {
    declarationsRegistry
        .getScenarios()
        .forEach(scenario -> scenarios.add(createIntegrationScenarioInfo(scenario)));
  }

  private void initializeEndpointInfos() {
    declarationsRegistry
        .getConnectors()
        .forEach(connector -> connectors.add(createAndAddConnectorInfo(connector)));
  }
}
