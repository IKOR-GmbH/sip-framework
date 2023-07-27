package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.DslDefinitionBase;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Base element for DSL classes used to orchestrate in integration scenario
 *
 * @param <M> type of the integration scenario's response model
 */
public abstract class ProcessDslBase<S extends ProcessDslBase<S, R, M>, R, M>
    extends DslDefinitionBase<S, R> {

  @Getter(AccessLevel.PACKAGE)
  private final CompositeProcessDefinition compositeProcess;

  ProcessDslBase(final R dslReturnDefinition, final CompositeProcessDefinition compositeProcess) {
    super(dslReturnDefinition);
    this.compositeProcess = compositeProcess;
  }
}
