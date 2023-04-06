package de.ikor.sip.foundation.core.declarative.orchestration.dsl;

import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import java.io.Serializable;
import java.util.function.Function;
import org.apache.commons.lang3.SerializationUtils;

@FunctionalInterface
public interface StepResultCloner<T> extends Function<T, T> {

  static <T extends Cloneable> StepResultCloner<T> forCloneable() {
    return element -> DeclarativeHelper.invokeMethod(element, "clone");
  }

  static <T extends Serializable> StepResultCloner<T> forSerializable() {
    return SerializationUtils::clone;
  }

  @SuppressWarnings("unchecked")
  static <T> StepResultCloner<T> forCopyConstructor() {
    return element -> (T) DeclarativeHelper.invokeConstructor(element.getClass(), element);
  }
}
