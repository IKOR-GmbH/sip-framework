package de.ikor.sip.foundation.core.declarative.composite;

import de.ikor.sip.foundation.core.declarative.orchestration.OrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import java.util.Map;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RoutesDefinition;

/**
 * Class that holds information to be used by orchestrators of an integration-scenario.
 *
 * @see ScenarioOrchestrator
 */
public interface CompositeOrchestrationInfo extends OrchestrationInfo {
  /**
   * Returns the integration scenario that is being orchestrated.
   *
   * @return the integration scenario
   */
  CompositeProcessDefinition getCompositeProcess();

  /**
   * Returns the routes definition that is being used to orchestrate the integration scenario.
   *
   * <p>All Camel routes used for the orchestration must be initialized from this element.
   *
   * @return the routes definition
   */
  RoutesDefinition getRoutesDefinition();

  /**
   * Returns the providers and their respective endpoints that are attached to this integration
   * scenario
   *
   * @return providers and their respective endpoints attached to the scenario
   */
  Map<IntegrationScenarioProviderDefinition, EndpointConsumerBuilder> getProviderEndpoints();

  /**
   * Returns the consumers and their respective endpoints that are attached to this integration
   * scenario
   *
   * @return consumers and their respective endpoints attached to the scenario
   */
  Map<IntegrationScenarioConsumerDefinition, EndpointProducerBuilder> getConsumerEndpoints();
}
