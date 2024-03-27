package ${package}.scenarios.definitions;

import ${package}.scenarios.models.AirQualityResponse;
import ${package}.scenarios.models.GeoCodingRequest;
import ${package}.scenarios.processes.AirQualityProcess;
import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioBase;

@IntegrationScenario(
        scenarioId = GetAirQualityByCityScenario.ID,
        requestModel = GeoCodingRequest.class,
        responseModel = AirQualityResponse.class)
public class GetAirQualityByCityScenario extends IntegrationScenarioBase {

    public static final String ID = "GetAirQualityByCityScenario";

    @Override
    public Orchestrator<ScenarioOrchestrationInfo> getOrchestrator() {
        return ScenarioOrchestrator.forOrchestrationDslWithResponse(
                AirQualityResponse.class,
                dsl -> {
                    dsl.forAnyUnspecifiedScenarioProvider()
                            .callScenarioConsumer(AirQualityProcess.class)
                            .andNoResponseHandling();
                });
    }
}
