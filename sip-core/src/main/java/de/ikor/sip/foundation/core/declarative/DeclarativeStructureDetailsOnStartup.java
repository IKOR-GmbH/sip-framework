package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class DeclarativeStructureDetailsOnStartup {

  private final DeclarationsRegistry declarationsRegistry;

  @EventListener(ApplicationReadyEvent.class)
  public void logDeclarativeStructureDetails() {
    log.info("SIP Framework Status:");
    logElements("Connectors", declarationsRegistry.getConnectors(), ConnectorDefinition::getId);
    logElements(
        "Integration Scenarios",
        declarationsRegistry.getScenarios(),
        IntegrationScenarioDefinition::getId);
    logElements(
        "Composite Processes",
        declarationsRegistry.getProcesses(),
        CompositeProcessDefinition::getId);
    logElements(
        "Connector Groups",
        declarationsRegistry.getConnectorGroups(),
        ConnectorGroupDefinition::getId);
    log.info("SIP Framework - Adapter started and ready");
  }

  private static <T> void logElements(
      String elementName, List<T> elements, Function<T, String> idExtractor) {
    log.info(
        "{} ({}): {}",
        elementName,
        elements.size(),
        elements.stream().map(idExtractor).collect(Collectors.joining(", ")));
  }
}
