package de.ikor.sip.foundation.core.declarative.orchestration.common.dsl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Base element for DSL classes that specify their own type (in an inheritance hierarchy) as well as
 * the parent-type
 *
 * @param <S> Type of the element itself. This is used when methods in base classes want to return
 *     the actual subclass-type instead of the base class.
 * @param <R> Return type, i.e. the type the DSL can return to when leaving the current scope
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DslDefinitionBase<S extends DslDefinitionBase<S, R>, R> {

  @Getter(AccessLevel.PROTECTED)
  private final R dslReturnDefinition;

  /**
   * Use as an alternative to <code>this</code> in base-classes to return the actual subtype
   *
   * @return <code>this</code> casted to the declared element type
   */
  protected S self() {
    return (S) this;
  }
}
