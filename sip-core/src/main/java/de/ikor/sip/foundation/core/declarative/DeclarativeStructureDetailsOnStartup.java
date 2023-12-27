package de.ikor.sip.foundation.core.declarative;

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
    log.info(
        "Successfully loaded: Connectors - {}, Integration Scenarios - {}, Processes - {}, Connector Groups - {} ",
        declarationsRegistry.getConnectors().size(),
        declarationsRegistry.getScenarios().size(),
        declarationsRegistry.getProcesses().size(),
        declarationsRegistry.getConnectorGroups().size());
    log.info("SIP Framework - Adapter started and ready");
  }
}
