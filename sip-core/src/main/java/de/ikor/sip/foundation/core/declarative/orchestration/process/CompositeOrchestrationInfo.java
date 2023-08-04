package de.ikor.sip.foundation.core.declarative.orchestration.process;

import de.ikor.sip.foundation.core.declarative.orchestration.OrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Map;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RoutesDefinition;

/**
 * Class that holds information to be used by orchestrators of a {@link
 * de.ikor.sip.foundation.core.declarative.annonation.CompositeProcess} This is structural
 * information that gives orchestrator an ability to generate orchestration routes
 *
 * @see CompositeOrchestrator
 */
public interface CompositeOrchestrationInfo extends OrchestrationInfo {

  /**
   * Returns the composite process that is being orchestrated.
   *
   * @return the composite process definition
   */
  CompositeProcessDefinition getCompositeProcess();

  /**
   * Returns the routes definition that is being used to orchestrate the composite process. It is a
   * hook where the orchestrator can add its routes.
   *
   * <p>All Camel routes used for the orchestration must be initialized from this element.
   *
   * @return the routes definition
   */
  RoutesDefinition getRoutesDefinition();

  /**
   * Returns the providers and their respective endpoints that are declared for this composite
   * process
   *
   * @return providers and their respective endpoints attached to the composite process
   */
  Map<IntegrationScenarioDefinition, EndpointConsumerBuilder> getProviderEndpoints();

  /**
   * Returns the consumers and their respective endpoints that are declared for this composite
   * process
   *
   * @return consumers and their respective endpoints attached to the composite process
   */
  Map<IntegrationScenarioDefinition, EndpointProducerBuilder> getConsumerEndpoints();
}
