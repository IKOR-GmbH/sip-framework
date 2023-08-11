package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;

public class ForProcessStartConditionalImpl<R>
    extends ForProcessStartConditional<ForProcessStartConditionalImpl<R>, R> {

  /**
   * Constructor
   *
   * <p><em>For internal use only</em>
   *
   * @param dslReturnDefinition
   * @param compositeProcess Composite Process
   * @param providerClass
   */
  public ForProcessStartConditionalImpl(
      R dslReturnDefinition, CompositeProcessDefinition compositeProcess) {
    super(dslReturnDefinition, compositeProcess);
  }
}
