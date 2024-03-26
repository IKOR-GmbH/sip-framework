package ${package}.scenarios.processes;

import ${package}.scenarios.definitions.GetAirQualityByCityScenario;
import ${package}.scenarios.definitions.GetAirQualityLatLonScenario;
import ${package}.scenarios.definitions.GetCityGeocodingScenario;
import ${package}.scenarios.models.AirQualityRequest;
import ${package}.scenarios.models.GeoCodingResponse;
import ${package}.scenarios.models.GeoCodingResult;
import de.ikor.sip.foundation.core.declarative.annonation.CompositeProcess;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.process.ProcessOrchestrator;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessBase;

@CompositeProcess(
        processId = "AirQualityProcessr",
        consumers = {GetCityGeocodingScenario.class, GetAirQualityLatLonScenario.class},
        provider = GetAirQualityByCityScenario.class)
public class AirQualityProcess extends CompositeProcessBase {

    @Override
    public Orchestrator<CompositeProcessOrchestrationInfo> getOrchestrator() {
        return ProcessOrchestrator.forOrchestrationDsl(
                dsl -> {
                    dsl.callConsumer(GetCityGeocodingScenario.class)
                            .withResponseHandling(
                                    (latestResponse, context) -> {
                                        GeoCodingResponse response = (GeoCodingResponse) latestResponse;
                                    })
                            .callConsumer(GetAirQualityLatLonScenario.class)
                            .withRequestPreparation(
                                    context -> {
                                        GeoCodingResponse response =
                                                (GeoCodingResponse) context.getLatestResponse().get();
                                        GeoCodingResult result = response.getResults().get(0);
                                        return AirQualityRequest.builder()
                                                .lat(result.getLatitude())
                                                .lon(result.getLongitude())
                                                .build();
                                    })
                            .withResponseHandling(
                                    (latestResponse, context) -> {
                                        System.out.println(latestResponse);
                                    });
                });
    }
}


