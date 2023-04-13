package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;

/**
 * Interface for mappers between to data types
 *
 * @param <S> Source Type
 * @param <T> Target Type
 */
@SuppressWarnings("unchecked")
public interface ModelMapper<S, T> {

  String MAPPING_METHOD_NAME = "mapToTargetModel";

  /**
   * Maps the given <code>sourceModel</code> to the target type
   *
   * @param sourceModel Element to map
   * @return Element mapped to target type
   */
  T mapToTargetModel(S sourceModel);

  /**
   * Derives the source-class from the generic type
   *
   * @return Source class
   */
  default Class<S> getSourceModelClass() {
    return (Class<S>) DeclarativeHelper.getMappingMethod(getClass()).getParameterTypes()[0];
  }

  /**
   * Derives the target-class from the generic type
   *
   * @return Target class
   */
  default Class<T> getTargetModelClass() {
    return (Class<T>) DeclarativeHelper.getMappingMethod(getClass()).getReturnType();
  }
}
