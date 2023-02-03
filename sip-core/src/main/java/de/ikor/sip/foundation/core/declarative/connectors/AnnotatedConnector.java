package de.ikor.sip.foundation.core.declarative.connectors;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.annonations.Connector;
import de.ikor.sip.foundation.core.declarative.endpoints.InboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.OutboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.declarative.utils.ReflectionHelper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;

public class AnnotatedConnector implements ConnectorDefinition {

  private DeclarationsRegistry declarationsRegistry;

  private final Connector annotation = ReflectionHelper.getAnnotationOrThrow(Connector.class, this);

  @Override
  public final String getID() {
    return annotation.connectorId();
  }

  @Override
  public String getDocumentation() {
    final var annotationPath = annotation.pathToDocumentationResource();
    final var resourcePath =
        annotationPath.isEmpty()
            ? String.format("documents/structure/connectors/%s", getID())
            : annotationPath;
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

  @Override
  public final Map<String, IntegrationScenarioConsumerDefinition>
      getConsumedIntegrationScenarios() {
    List<OutboundEndpointDefinition> outboundEndpoints =
        declarationsRegistry.getOutboundEndpointsByConnectorId(getID());
    Map<String, IntegrationScenarioConsumerDefinition> consumedScenarios = new HashMap<>();
    outboundEndpoints.forEach(
        endpoint ->
            consumedScenarios.put(
                endpoint.getConsumedScenario().getID(),
                (IntegrationScenarioConsumerDefinition) endpoint.getConsumedScenario()));
    return consumedScenarios;
  }

  @Override
  public final Map<String, IntegrationScenarioProviderDefinition>
      getProvidedIntegrationScenarios() {
    List<InboundEndpointDefinition> inboundEndpoints =
        declarationsRegistry.getInboundEndpointsByConnectorId(getID());
    Map<String, IntegrationScenarioProviderDefinition> providedScenarios = new HashMap<>();
    inboundEndpoints.forEach(
        endpoint ->
            providedScenarios.put(
                endpoint.getProvidedScenario().getID(),
                (IntegrationScenarioProviderDefinition) endpoint.getProvidedScenario()));
    return providedScenarios;
  }

  public final void setDeclarationsRegistry(final DeclarationsRegistry declarationsRegistry) {
    this.declarationsRegistry = declarationsRegistry;
  }
}
