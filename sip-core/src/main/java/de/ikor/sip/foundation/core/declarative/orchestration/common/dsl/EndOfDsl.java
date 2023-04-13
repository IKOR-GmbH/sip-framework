package de.ikor.sip.foundation.core.declarative.orchestration.common.dsl;

/** Element used to terminate a DSL declaration */
public class EndOfDsl extends DslDefinitionBase<EndOfDsl, EndOfDsl> {
  public static final EndOfDsl INSTANCE = new EndOfDsl();

  private EndOfDsl() {
    super(null);
  }
}
