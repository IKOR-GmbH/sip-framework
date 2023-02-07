package de.ikor.sip.foundation.core.declarative.connectors;

import java.io.IOException;
import org.springframework.core.io.ClassPathResource;

public interface ConnectorDefinition {

  String getID();

  String getDocumentation();

  default String readDocumentation(String path) {
    final var resourcePath =
        path.isEmpty() ? String.format("documents/structure/connectors/%s", getID()) : path;
    final var resource = new ClassPathResource(resourcePath);

    if (!resource.isReadable()) {
      return String.format("No documentation has been provided for connector '%s'", getID());
    }

    try (var input = resource.getInputStream()) {
      return new String(input.readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read documentation resource", e);
    }
  }

  //  Map<String, IntegrationScenarioConsumerDefinition> getConsumedIntegrationScenarios();
  //
  //  Map<String, IntegrationScenarioProviderDefinition> getProvidedIntegrationScenarios();
}
