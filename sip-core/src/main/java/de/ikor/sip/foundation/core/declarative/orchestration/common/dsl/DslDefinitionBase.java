package de.ikor.sip.foundation.core.declarative.orchestration.common.dsl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DslDefinitionBase<S extends DslDefinitionBase<S, R>, R> {

  @Getter(AccessLevel.PROTECTED)
  private final R dslReturnDefinition;

  protected <S> S self() {
    return (S) this;
  }
}
