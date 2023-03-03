package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;

public interface ModelMapper<S, T> {

  String MAPPING_METHOD_NAME = "mapToTargetModel";

  T mapToTargetModel(S sourceModel);

  default Class<S> getSourceModelClass() {
    return (Class<S>) DeclarativeHelper.getMappingMethod(getClass()).getParameterTypes()[0];
  }

  default Class<T> getTargetModelClass() {
    return (Class<T>) DeclarativeHelper.getMappingMethod(getClass()).getReturnType();
  }
}
