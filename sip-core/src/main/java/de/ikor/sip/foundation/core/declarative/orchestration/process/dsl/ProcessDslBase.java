package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.DslDefinitionBase;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import lombok.AccessLevel;
import lombok.Getter;

/** Base element for DSL classes used to orchestrate in process orchestration */
public abstract class ProcessDslBase<S extends ProcessDslBase<S, R>, R>
    extends DslDefinitionBase<S, R> {

  @Getter(AccessLevel.PACKAGE)
  @JsonIgnore
  private final CompositeProcessDefinition compositeProcess;

  ProcessDslBase(final R dslReturnDefinition, final CompositeProcessDefinition compositeProcess) {
    super(dslReturnDefinition);
    this.compositeProcess = compositeProcess;
  }
}
