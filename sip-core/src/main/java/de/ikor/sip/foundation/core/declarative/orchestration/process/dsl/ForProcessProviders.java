package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * DSL class specifying a process provider specified by its class
 *
 * @param <R> DSL handle for the return DSL Verb/type.
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public final class ForProcessProviders<R> extends ProcessDslBase<ForProcessProviders<R>, R> {

  @Getter(AccessLevel.PACKAGE)
  private final List<CallProcessConsumer<ForProcessProviders<R>>> consumerCalls = new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioDefinition> providerClass;

  ForProcessProviders(
      final R dslReturnDefinition,
      final CompositeProcessDefinition compositeProcess,
      final Class<? extends IntegrationScenarioDefinition> providerClass) {
    super(dslReturnDefinition, compositeProcess);
    this.providerClass = providerClass;
  }

  /**
   * Attach consumer calls to this process by their class. Consumer calls can be chained.
   *
   * @param consumerClass class of the consumer
   * @return DSL handle
   */
  public CallProcessConsumer<ForProcessProviders<R>> callConsumer(
      Class<? extends IntegrationScenarioDefinition> consumerClass) {
    final CallProcessConsumer<ForProcessProviders<R>> def =
        new CallProcessConsumer<>(self(), getCompositeProcess(), consumerClass);
    consumerCalls.add(def);
    return def;
  }
}
