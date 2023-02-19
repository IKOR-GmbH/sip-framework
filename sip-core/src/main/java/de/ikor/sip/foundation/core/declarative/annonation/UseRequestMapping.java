package de.ikor.sip.foundation.core.declarative.annonation;

import de.ikor.sip.foundation.core.declarative.model.FindAutomaticModelMapper;
import de.ikor.sip.foundation.core.declarative.model.ModelMapper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UseRequestMapping {
  Class<? extends ModelMapper> mapper() default FindAutomaticModelMapper.class;

  String dataFormat() default "";
}
