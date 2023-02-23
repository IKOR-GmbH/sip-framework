package de.ikor.sip.foundation.core.declarative.model;

public interface ModelMapper<S, T> {

  String MAPPING_METHOD_NAME = "mapToTargetModel";

  T mapToTargetModel(S sourceModel);

  Class<S> getSourceModelClass();

  Class<T> getTargetModelClass();
}
