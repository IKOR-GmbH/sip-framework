package de.ikor.sip.foundation.core.declarative.utils;

import java.lang.annotation.Annotation;

public class ReflectionHelper {

  private ReflectionHelper() {}

  public static <A extends Annotation> A getAnnotationOrThrow(Class<A> annotation, Object from) {
    var ann = from.getClass().getAnnotation(annotation);
    if (null == ann) {
      throw new IllegalArgumentException(
          String.format(
              "Annotation %s required on class %s", annotation.getSimpleName(), from.getClass()));
    }
    return ann;
  }
}
