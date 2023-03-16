package de.ikor.sip.foundation.core.declarative.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestratable;
import de.ikor.sip.foundation.core.declarative.orchestration.ScenarioOrchestrationInfo;
import java.util.Optional;

/**
 * Interface used for specifying integration scenarios used within a SIP adapter.
 *
 * <p>Integration scenarios allow for decoupling of the connectors, as they define common domain
 * models that must be used and mapped to/from by all connectors that provide or consume from the
 * scenario.
 *
 * <p><em>Adapter developers should not implement this interface directly, but use {@link
 * IntegrationScenarioBase} instead.</em>
 *
 * @see IntegrationScenarioBase
 * @see de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario
 */
public interface IntegrationScenarioDefinition extends Orchestratable<ScenarioOrchestrationInfo> {

  /**
   * Returns the ID of the integration scenario. Must be unique within the scope of the adapter.
   *
   * <p>The scenario identifier should typically relate to the functional/business process the
   * integration is supporting.
   *
   * @return Integration scenario identifier
   */
  String getId();

  /**
   * Base class of the common request model used by the scenario. The model can be shared across
   * multiple scenarios.
   *
   * @return Request model base class
   */
  Class<?> getRequestModelClass();

  /**
   * Base class of the common response model used by the scenario. The model can be shared across
   * multiple scenarios.
   *
   * @return Response model base class, or {@link Optional#empty()} if the scenario does not have a
   *     response flow
   */
  Optional<Class<?>> getResponseModelClass();

  /**
   * Returns the path to the documentation resource for the integration scenario.
   *
   * @return Path to the documentation resource
   */
  String getPathToDocumentationResource();
}
