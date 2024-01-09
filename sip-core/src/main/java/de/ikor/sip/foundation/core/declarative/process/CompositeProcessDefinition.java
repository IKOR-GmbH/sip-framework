package de.ikor.sip.foundation.core.declarative.process;

import de.ikor.sip.foundation.core.declarative.DeclarativeElement;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestratable;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import java.util.List;

/**
 * Interface used for specifying composite processes used within a SIP adapter.
 *
 * <p>Composite process allows for connecting multiple integration scenarios into one process.
 * Process can be provided data from the integration scenario in the same way as the outbound
 * connectors do. Process can send data to be consumed by integration scenarios in the same way as
 * the inbound connectors do. This allows making composite and complex flows for integration
 * scenarios where one side of the integration scenario can not be fulfilled by a simple connector.
 *
 * <p><em>Adapter developers should not implement this interface directly, but use {@link
 * CompositeProcessBase} instead.</em>
 *
 * @see CompositeProcessBase
 * @see de.ikor.sip.foundation.core.declarative.annonation.CompositeProcess
 */
public interface CompositeProcessDefinition
    extends Orchestratable<CompositeProcessOrchestrationInfo>,
        IntegrationScenarioProviderDefinition,
        IntegrationScenarioConsumerDefinition,
        DeclarativeElement {

  List<Class<? extends IntegrationScenarioDefinition>> getConsumerDefinitions();

  Class<? extends IntegrationScenarioDefinition> getProviderDefinition();
}
