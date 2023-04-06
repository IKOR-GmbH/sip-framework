package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.dsl.DslDefinitionBase;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class CallScenarioConsumerBaseDefinition<
        S extends CallScenarioConsumerBaseDefinition<S, R>, R>
    extends DslDefinitionBase<S, R> {

  @Getter(AccessLevel.PACKAGE)
  private Optional<ScenarioRequestPreparation<?>> requestPreparation = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<ScenarioResponseAggregator<?>> responseAggregator = Optional.empty();

  protected CallScenarioConsumerBaseDefinition(final R dslReturnDefinition) {
    super(dslReturnDefinition);
  }

  public <T> S withRequestPreparer(final ScenarioRequestPreparation<T> requestPreparation) {
    this.requestPreparation = Optional.of(requestPreparation);
    return self();
  }

  public R withNoResponseAggregation() {
    return getDslReturnDefinition();
  }

  public <T> R withResponseAggreagator(final ScenarioResponseAggregator<T> responseAggregator) {
    this.responseAggregator = Optional.of(responseAggregator);
    return getDslReturnDefinition();
  }
}
