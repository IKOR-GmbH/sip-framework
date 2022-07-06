package de.ikor.sip.foundation.core.annotation;

import de.ikor.sip.foundation.core.util.FoundationFeature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation which marks, names and provides version of all framework features */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SIPFeature {
  FoundationFeature type();

  int[] versions();
}
