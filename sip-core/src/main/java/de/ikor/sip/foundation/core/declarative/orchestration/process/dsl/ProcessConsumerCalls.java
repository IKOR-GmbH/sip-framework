package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;


interface ProcessConsumerCalls<S extends ProcessConsumerCalls<S, R>, R> {

    CallProcessConsumerImpl<S> callConsumer(Class<? extends IntegrationScenarioDefinition> consumerClass);
}
