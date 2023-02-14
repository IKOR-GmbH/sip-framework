package de.ikor.sip.foundation.core.declarative;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/** Fill RoutesRegistry endpoint to route mappings once application is ready */
@Configuration
@RequiredArgsConstructor
public class RoutesRegistration {
  private final RoutesRegistry routesRegistry;

  /** On ApplicationReadyEvent trigger mapping routes and endpoints in RoutesRegistry */
  @EventListener(ApplicationReadyEvent.class)
  private void fillRegistry() {
    routesRegistry.prefillEndpointRouteMappings();
  }
}
