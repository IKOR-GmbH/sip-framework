package de.ikor.sip.foundation.core.declarative.composite.orchestration;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.DslDefinitionBase;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Base element for DSL classes used to orchestrate in integration scenario
 *
 * @param <M> type of the integration scenario's response model
 */
public abstract class ProcessDslDefinitionBase<S extends ProcessDslDefinitionBase<S, R, M>, R, M>
    extends DslDefinitionBase<S, R> {

  @Getter(AccessLevel.PACKAGE)
  private final CompositeProcessDefinition compositeProcess;

  ProcessDslDefinitionBase(
      final R dslReturnDefinition, final CompositeProcessDefinition compositeProcess) {
    super(dslReturnDefinition);
    this.compositeProcess = compositeProcess;
  }
}
