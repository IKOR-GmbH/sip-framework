package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.RouteRole;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestsDefinition;
import org.apache.camel.model.rest.VerbDefinition;

/**
 * Base class for defining inbound REST connectors via Camel's {@link RestDefinition} DSL.
 *
 * <p>Adapter developers should extend this class and annotate it with @{@link InboundConnector} to
 * specify the connector. The configuration of the REST endpoint is done by overriding the {@link
 * #configureRest(RestDefinition)}.
 *
 * @see ConnectorBase#defineTransformationOrchestrator() Infos on attaching transformation between
 *     domain models of connector and integration scenario
 * @see InboundConnector
 */
public abstract class RestInboundConnectorBase extends InboundConnectorBase
    implements InboundConnectorDefinition<RestsDefinition> {

  @Override
  public final void defineInboundEndpoints(
      final RestsDefinition definition,
      final EndpointProducerBuilder targetToDefinition,
      final RoutesRegistry routeRegistry) {
    var rest = definition.rest();
    var endpointCounter = 0;
    configureRest(rest);
    for (VerbDefinition verb : rest.getVerbs()) {
      verb.setId(
          routeRegistry.generateRouteIdForConnector(
              RouteRole.EXTERNAL_ENDPOINT, this, ++endpointCounter));
      verb.setTo(new ToDefinition(targetToDefinition));
    }
  }

  /**
   * Configures the REST endpoint used within this connector.
   *
   * <p>Note that while {@link RestDefinition} supports specifing multiple verbs/endpoints, all of
   * those will be mapped to the single integration scenario that this connector is linked with
   * though {@link #getScenarioId()}.
   *
   * @param definition the REST endpoint definition
   */
  protected abstract void configureRest(final RestDefinition definition);

  @Override
  public final Class<RestsDefinition> getEndpointDefinitionTypeClass() {
    return RestsDefinition.class;
  }
}
