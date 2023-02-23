package de.ikor.sip.foundation.core.declarative.model;

public abstract class ModelMapperBase<S, T> implements ModelMapper<S, T> {

  private final Class<S> sourceModelClass;
  private final Class<T> targetModelClass;

  protected ModelMapperBase(final Class<S> sourceModelClass, final Class<T> targetModelClass) {
    this.sourceModelClass = sourceModelClass;
    this.targetModelClass = targetModelClass;
  }

  @Override
  public final Class<S> getSourceModelClass() {
    return sourceModelClass;
  }

  @Override
  public final Class<T> getTargetModelClass() {
    return targetModelClass;
  }
}
