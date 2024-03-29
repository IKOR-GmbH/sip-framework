package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * DSL class calling a consumer by its {@link IntegrationScenarioDefinition} class
 *
 * @param <S> DSL handle for caller
 * @param <R> DSL handle for the return DSL Verb/type.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ForProcessProvidersDelegate<S extends ProcessConsumerCalls<S, R>, R>
    implements ProcessConsumerCalls<S, R> {

  @Getter(AccessLevel.PACKAGE)
  private final List<CallableWithinProcessDefinition> consumerCalls;

  @Getter(AccessLevel.PACKAGE)
  private final S callerNode;

  @Getter(AccessLevel.PACKAGE)
  private final R returningNode;

  @Override
  public CallProcessConsumer<? extends CallProcessConsumer<?, ?>, S> callConsumer(
      Class<? extends IntegrationScenarioDefinition> consumerClass) {
    final CallProcessConsumer<? extends CallProcessConsumer<?, ?>, S> def =
        new CallProcessConsumer<>(callerNode, null, consumerClass);
    consumerCalls.add(def);
    return def;
  }
}
